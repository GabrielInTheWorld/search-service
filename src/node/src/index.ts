import express, { NextFunction, Request, Response } from 'express';
import { createServer } from 'http';
import RouteService from './api/route.service';

class Server {
    public static readonly PORT: number = 8080;
    public static readonly PROTOCOL: string = 'http';
    public static readonly DOMAIN: string = 'localhost';

    private app = express();
    private httpServer = createServer(this.app);
    private routeService = new RouteService();

    public get port(): number {
        return Server.PORT;
    }

    public constructor() {
        this.initializeConfig();
        this.initializeRoutes();
    }

    public start(): void {
        this.httpServer.listen(Server.PORT, () => {
            console.log(`Server is running on ${Server.PROTOCOL}://${Server.DOMAIN}:${Server.PORT}`);
        });
    }

    private initializeConfig(): void {
        this.app.use(express.json({ limit: '100mb' }));
        this.app.use(express.urlencoded({ extended: true, limit: '100mb' }));
        this.app.use((req, res, next) => {
            console.log('Incoming size:', req.headers['content-length']);
            console.log('Incoming body:', req.body);
            next();
        });
        this.app.use((req, res, next) => this.setCors(req, res, next));
    }

    private initializeRoutes(): void {
        this.app.all('/', (req, res) => res.json('Hello World, Obi-Wan!'));
        this.app.post('/presenter', (req, res) => this.routeService.presenter(req, res));
        this.app.post('/presenter:postgre.update', (req, res) => this.routeService.bulkInsert(req, res));
    }

    private setCors(req: Request, res: Response, next: NextFunction): void {
        const origin = req.headers.origin;
        const requestingOrigin = Array.isArray(origin) ? origin.join(' ') : origin || '';
        console.log(`${req.protocol}://${req.headers.host}: ${req.method} -- ${req.originalUrl}`);
        res.setHeader('Content-Type', 'application/json');
        res.setHeader('Access-Control-Allow-Origin', requestingOrigin);
        res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS, POST');
        res.setHeader(
            'Access-Control-Allow-Headers',
            'Origin, X-Requested-With, Content-Type, X-Content-Type, Authentication, Authorization, X-Access-Token, Accept'
        );
        res.setHeader('Access-Control-Allow-Credentials', 'false');
        return next();
    }
}

const server = new Server();
server.start();
