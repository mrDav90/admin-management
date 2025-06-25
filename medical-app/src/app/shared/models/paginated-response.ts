export interface PaginatedResponse<T> {
    totalElements: number;
    totalPages: number;
    size: number;
    content: T[];
    number: number;
    sort: Sort;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    pageable: Pageable;
    empty: boolean;
}

interface Sort {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
}

interface Pageable {
    offset: number;
    sort: Sort;
    paged: boolean;
    pageNumber: number;
    pageSize: number;
    unpaged: boolean;
}