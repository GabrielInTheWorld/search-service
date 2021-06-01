import { SearchRepository } from './search-repository';

export class MotionBlockRepositoryService implements SearchRepository {
    public static readonly COLLECTION = 'motion_block';
    public static readonly MOTION_BLOCK_FIELDS: string[] = ['title', 'internal'];

    public getCollection(): string {
        return MotionBlockRepositoryService.COLLECTION;
    }

    public getSearchableFields(): string[] {
        return MotionBlockRepositoryService.MOTION_BLOCK_FIELDS;
    }
}
