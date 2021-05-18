import axios, { AxiosError } from 'axios';

import { HttpHeaders, HttpMethod } from './interfaces/http';

export interface HttpData {
    [key: string]: any;
}

type AxiosResponseType = 'arraybuffer' | 'blob' | 'document' | 'json' | 'text' | 'stream' | undefined;

export default class HttpClientService {
    public async get<T = any>(
        url: string,
        data?: any,
        headers?: HttpHeaders,
        responseType?: AxiosResponseType
    ): Promise<T> {
        return this.send<T>(url, HttpMethod.GET, headers, data, responseType);
    }
    public async post<T = any>(url: string, data?: any, headers?: HttpHeaders): Promise<T> {
        return this.send<T>(url, HttpMethod.POST, data, headers);
    }
    public async delete<T = any>(url: string, data?: any, headers?: HttpHeaders): Promise<T> {
        return this.send<T>(url, HttpMethod.DELETE, data, headers);
    }

    private async send<T>(
        url: string,
        method: HttpMethod,
        data?: HttpData,
        headers: HttpHeaders = {},
        responseType: AxiosResponseType = 'json'
    ): Promise<T> {
        console.debug(`Sending a request: ${method} ${url} ${JSON.stringify(headers)}`);
        return (await axios({ url, method, data, headers, responseType })
            .then(response => response.data)
            .catch(reason => this.handleError(reason, url, method, data, headers, responseType))) as any;
    }

    private handleError(
        error: AxiosError,
        url: string,
        method: HttpMethod,
        data?: HttpData,
        headers?: HttpHeaders,
        responseType?: AxiosResponseType
    ): void {
        console.error('HTTP-error occurred: ', error.message);
        console.error(`Error is occurred while sending the following information: ${method} ${url} ${responseType}`);
        console.error(
            `Request contains the following data ${/* JSON.stringify(data) */ {}} and headers ${JSON.stringify(
                headers
            )}`
        );
        throw new Error(error.message);
    }
}
