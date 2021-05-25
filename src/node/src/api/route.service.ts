import { Request, Response } from 'express';
import PostgreAdapterService from '../adapter/postgre-adapter.service';

interface Presenter {
    presenter: string;
    data: PresenterData;
}

interface PresenterData {
    db: string;
    search_query: string;
    file: { [key: string]: { [key: string]: any }[] };
}

export default class RouteService {
    private postgre = new PostgreAdapterService();

    public async bulkInsert(request: Request, response: Response): Promise<void> {
        await this.postgre.bulkInsert(request.body);
        response.json({ message: 'success', data: 'void' });
    }

    public async getAll(request: PresenterData, response: Response): Promise<void> {
        switch (request.db) {
            case 'postgre':
                response.json({ message: 'success', data: await this.postgre.getAll() });
                break;
        }
    }

    public async search(request: PresenterData, response: Response): Promise<void> {
        switch (request.db) {
            case 'postgre':
                response.json({ message: 'success', data: await this.postgre.search(request.search_query) });
                break;
        }
    }

    public async presenter(request: Request, response: Response): Promise<void> {
        const presenter = request.body as Presenter[];
        switch (presenter[0].presenter) {
            case 'search':
                return await this.search(presenter[0].data, response);
            case 'get_all':
                return await this.getAll(presenter[0].data, response);
            // case 'insert':
            //     return await this.bulkInsert(presenter[0].data, response);
        }
    }
}
