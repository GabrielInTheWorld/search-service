import { SearchRepository } from './search-repository';

export class TopicRepositoryService implements SearchRepository {
    public static readonly TOPIC_FIELDS: string[] = ['title', 'text'];

    public getSearchableFields(): string[] {
        return TopicRepositoryService.TOPIC_FIELDS;
    }
}
