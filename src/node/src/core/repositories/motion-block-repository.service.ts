import { SearchRepository } from './search-repository';

export class MotionBlockRepositoryService implements SearchRepository {
    public static readonly MOTION_BLOCK_FIELDS: string[] = ['title', 'internal'];

    public getSearchableFields(): string[] {
        return MotionBlockRepositoryService.MOTION_BLOCK_FIELDS;
    }
}
