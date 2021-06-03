import { Client } from 'pg';
import { MotionBlockRepositoryService } from '../core/repositories/motion-block-repository.service';
import { SearchRepository } from '../core/repositories/search-repository';
import { TopicRepositoryService } from '../core/repositories/topic-repository.service';

import HttpClientService from '../core/services/http-client.service';

interface Data {
    [key: string]: { [key: string]: any }[];
}

export default class PostgreAdapterService {
    private database = 'openslides';
    private user = 'openslides';
    private password = 'openslides';
    private client: Client | null = null;

    private http = new HttpClientService();

    private repositories: SearchRepository[] = [new TopicRepositoryService(), new MotionBlockRepositoryService()];

    public async bulkInsert(data: Data): Promise<void> {
        const events: never[] = [];
        for (const key in data) {
            for (const value of data[key]) {
                events.push({
                    type: 'create',
                    fqid: `${key}/${value['id']}`,
                    fields: value
                } as never);
            }
        }
        for (let i = 0; i < events.length; i += 1000) {
            await this.sendJson(events.slice(i, i + 1000));
        }
        if (events.length % 1000 > 0) {
            await this.sendJson(events.slice(-(events.length % 1000)));
        }
    }

    public async getAll(): Promise<any> {
        const client = await this.getClient();
        const result = await client.query('select fqid from models;');
        return result.rows;
    }

    public async search(searchQuery: string): Promise<any> {
        searchQuery = searchQuery.split(' ').join(' | ');
        searchQuery = `select * from models where topic_view_search @@ to_tsquery('${searchQuery}');`; // or any other collection-search-column
        console.log(`Start search with query: ${searchQuery}`);
        const client = await this.getClient();
        console.time('search');
        const result = await client.query(searchQuery);
        console.timeEnd('search');
        console.log(`Search result contains ${result.rows.length} entries.`);
        return result.rows;
    }

    private async init(): Promise<void> {
        this.client = new Client({
            host: 'localhost',
            database: this.database,
            user: this.user,
            password: this.password
        });
        await this.client.connect();
        const result = await this.client.query('select version()');
        console.log(`Connection to database successfully!\nDatabase version:`, result.rows);
        // ##############################
        // ##############################
        // ######## Query to create an index on specific fields:
        // ######## "alter table models add column <column-name> tsvector;"
        // ######## "update models set <column-name> = to_tsvector(coalesce(data ->> '<field>', '')) || to_tsvector(...) || ... where fqid ilike '...';"
        // ######## "create index <index-name> on models using gin ( <column-name> );"
        // ######## "select * from models where <column-name> @@ to_tsquery(query);" <- querying for the specific field
        // ##############################
        // ##############################
        await this.createSearchIndexes();
        console.log('Created a search-indexes');
    }

    private async getClient(): Promise<Client> {
        if (!this.client) {
            await this.init();
        }
        return this.client as Client;
    }

    private async sendJson(events: never[]): Promise<void> {
        const json = {
            user_id: 1,
            locked_fields: {},
            information: {},
            events
        };
        try {
            await this.http.post('http://localhost:9011/internal/datastore/writer/write', json);
        } catch (e) {
            console.log('Error:', e);
        }
    }

    private async createSearchIndexes(): Promise<void> {
        const client = await this.getClient();
        const collections = this.repositories.map(repo => repo.getCollection());
        await Promise.all(collections.map(collection => this.createColumn(this.getColumnName(collection))));
        await Promise.all(
            this.repositories
                .map(repo => ({ collection: repo.getCollection(), indexedFields: repo.getSearchableFields() }))
                .map(item => this.createIndexForCollection(item.collection, item.indexedFields))
        );
        await Promise.all(
            this.repositories
                .map(repo => ({ collection: repo.getCollection(), indexedFields: repo.getSearchableFields() }))
                .map(item => this.createTriggerFnForCollection(item.collection, item.indexedFields))
        );
    }

    private async createColumn(columnName: string): Promise<void> {
        const client = await this.getClient();
        await client
            .query(`alter table models add column ${columnName} tsvector;`)
            .then(() => console.log(`Column ${columnName} created;`))
            .catch(() => console.log(`Column ${columnName} already exists.`));
    }

    private async createIndexForCollection(collection: string, indexedFields: string[]): Promise<void> {
        const client = await this.getClient();
        const columnName = this.getColumnName(collection);
        const toTsVector = this.toTsVector(indexedFields);
        await client.query(`update models set ${columnName} = ${toTsVector} where fqid like '${collection}/%';`);
        await client.query(`create index if not exists ${columnName}_idx on models using gin (${columnName});`);
    }

    private async createTriggerFnForCollection(collection: string, indexedFields: string[]): Promise<void> {
        const client = await this.getClient();
        const columnName = this.getColumnName(collection);
        const triggerName = `${columnName}_trigger_fn()`;
        const toTsVector = this.toTsVector(indexedFields, 'new.');
        const triggerFn = `
            create function ${triggerName} returns trigger as $$
            begin 
                new.${columnName} = ${toTsVector};
            return new;
            end
            $$ language plpgsql;
        `;
        await this.executePromise(`Function ${triggerName}`, client.query(triggerFn));
        await this.executePromise(
            `Trigger ${columnName}_trigger`,
            client.query(
                `create trigger ${columnName}_trigger before insert or update on models for each row execute function ${triggerName};`
            )
        );
    }

    private toTsVector(indexedFields: string[], dataPrefix: string = ''): string {
        return indexedFields.map(field => `to_tsvector(coalesce(${dataPrefix}data ->> '${field}', ''))`).join(' || ');
    }

    private getColumnName(collection: string): string {
        return `${collection}_view_search`;
    }

    private async executePromise(name: string, promise: Promise<any>): Promise<void> {
        try {
            await promise;
        } catch (e) {
            console.log(`${name} already exists. Skipping.`);
        }
    }
}
