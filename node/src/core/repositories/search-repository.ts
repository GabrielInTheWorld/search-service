export interface SearchRepository {
    getCollection(): string;
    getSearchableFields(): string[];
}
