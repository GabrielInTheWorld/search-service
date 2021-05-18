import { Client } from 'pg';
import HttpClientService from './http-client.service';

interface Data {
    [key: string]: { [key: string]: any }[];
}

export default class PostgreAdapterService {
    private database = 'openslides';
    private user = 'openslides';
    private password = 'openslides';
    private client: Client | null = null;

    private http = new HttpClientService();

    public async bulkInsert(data: Data): Promise<void> {
        // const json = {
        //     user_id: 1,
        //     locked_fields: {},
        //     information: {},
        //     events: []
        // };
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
            // try {
            //     await this.http.post('http://localhost:9011/internal/datastore/writer/write', json);
            // } catch (e) {
            //     console.log('Error:', e);
            // }
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
        console.log(`Start search with query: ${searchQuery}`);
        const client = await this.getClient();
        console.time('search');
        const result = await client.query(
            `select fqid from models where to_tsvector(data) @@ to_tsquery(\'english\', \'${searchQuery}\');`
            // `select fqid from models where to_tsvector(data) @@ to_tsquery(\'${searchQuery}\');`
        );
        console.timeEnd('search');
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
        await this.client.query(
            "CREATE INDEX IF NOT EXISTS search_idx ON models USING GIN (to_tsvector('english', data));"
        );
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
}
