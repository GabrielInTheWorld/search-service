import { SearchRepository } from './search-repository';

export class TopicRepositoryService implements SearchRepository {
    public static readonly COLLECTION = 'topic';
    public static readonly TOPIC_FIELDS: string[] = ['title', 'text'];

    public getCollection(): string {
        return TopicRepositoryService.COLLECTION;
    }

    public getSearchableFields(): string[] {
        return TopicRepositoryService.TOPIC_FIELDS;
    }
}
