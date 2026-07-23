import API from './api'
import type { BookDto, BookRequest } from '../types'

export interface BookSearchParams {
  title?: string
  isbn?: string
  categoryId?: number
  authorId?: number
  publisherId?: number
  available?: boolean
  page?: number
  size?: number
  sort?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

const BASE = '/books'

const bookService = {
  /** Search / list books with optional filters and pagination */
  searchBooks: (params: BookSearchParams = {}) =>
    API.get<PageResponse<BookDto>>(BASE, { params }).then((r) => r.data),

  /** Get a single book by its ID */
  getBookById: (id: number) =>
    API.get<BookDto>(`${BASE}/${id}`).then((r) => r.data),

  /** Get a book by its ISBN */
  getBookByIsbn: (isbn: string) =>
    API.get<BookDto>(`${BASE}/isbn/${isbn}`).then((r) => r.data),

  /** Create a new book */
  createBook: (data: BookRequest) =>
    API.post<BookDto>(BASE, data).then((r) => r.data),

  /** Update an existing book */
  updateBook: (id: number, data: BookRequest) =>
    API.put<BookDto>(`${BASE}/${id}`, data).then((r) => r.data),

  /** Delete a book */
  deleteBook: (id: number) =>
    API.delete(`${BASE}/${id}`).then((r) => r.data),

  /** Get book recommendations based on a category */
  getRecommendations: (categoryId: number) =>
    API.get<BookDto[]>(`${BASE}/recommendations`, { params: { categoryId } }).then((r) => r.data),

  /** Get title search suggestions (autocomplete) */
  getSearchSuggestions: (query: string) =>
    API.get<string[]>(`${BASE}/suggestions`, { params: { query } }).then((r) => r.data),

  /** Update the cover image URL for a book */
  updateCoverImage: (id: number, coverImageUrl: string) =>
    API.patch<BookDto>(`${BASE}/${id}/cover`, { coverImageUrl }).then((r) => r.data),
}

export default bookService
