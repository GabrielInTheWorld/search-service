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
        searchQuery = this.createSearchCriteria(searchQuery);
        // searchQuery = searchQuery.split(' ').join(' | ');
        console.log(`Start search with query: ${searchQuery}`);
        const client = await this.getClient();
        console.time('search');
        const result = await client.query(
            searchQuery
            // `select * from models where text_search @@ to_tsquery(\'english\', \'${searchQuery}\');`
        );
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
        try {
            await this.client.query('alter table models add column text_search tsvector;');
            await this.client.query("update models set text_search = to_tsvector('english', data);");
        } catch (e) {
            console.log("COLUMN 'text_search' already exists. Aborting.");
            // console.error('Stacktrace:\n', e);
        }
        // ##############################
        // ##############################
        // ######## Query to create an index on specific fields:
        // ######## "create index <index-name> on models using gin (( data -> '<field>' ));" <- but that's not what we want!
        // ######## "create extension pg_trgm; <- required to make use of 'gin_trgm_ops'
        // ######## "create index <index-name> on models using gin (( data ->> '<field>' ) gin_trgm_ops );" <- that's it!
        // ######## "select * from models where data ->> '<field>' like '%<search_term>%' or ..." <- querying for the specific field
        // ##############################
        // ##############################
        await this.client.query('CREATE INDEX IF NOT EXISTS search_idx ON models USING GIN (text_search);');

        await this.client.query('CREATE EXTENSION IF NOT EXISTS pg_trgm with schema pg_catalog;');
        await this.createSearchIndexes();
        console.log('Created a search-index');
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
        const fields = this.repositories.flatMap(repo => repo.getSearchableFields());
        const fieldSet = new Set(fields);
        const promises = [];
        for (const field of fieldSet) {
            const query = `create index if not exists ${field}_idx on models using gin (( data ->> '${field}' ) gin_trgm_ops );`;
            promises.push(client.query(query));
        }
        await Promise.all(promises).then(() => console.log('Indexes created!'));
        // return `CREATE `
    }

    private createSearchCriteria(q: string): string {
        const words = q.split(' ');
        // const fields = ['title', 'text'];
        const fields = this.repositories.flatMap(repo => repo.getSearchableFields());
        const fieldSet = new Set(fields);
        let d = '';
        let i = 0;
        const max = words.length * fieldSet.size;
        for (const word of words) {
            // const word = words[i];
            for (const field of fieldSet) {
                // const field = fields[j];
                ++i;
                d += ` data ->> \'${field}\' like \'%${word}%\' ${i < max ? 'or' : ''}`;
            }
        }
        return `select fqid from models where ${d};`;
    }
}
