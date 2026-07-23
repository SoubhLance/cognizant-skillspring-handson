import API from './api'
import type { AuthorDto } from '../types'

export interface AuthorRequest {
  name: string
  biography?: string
  birthDate?: string
}

const BASE = '/authors'

const authorService = {
  /** Get all authors (cached 30 min on the backend) */
  getAllAuthors: () =>
    API.get<AuthorDto[]>(BASE).then((r) => r.data),

  /** Get an author by ID */
  getAuthorById: (id: number) =>
    API.get<AuthorDto>(`${BASE}/${id}`).then((r) => r.data),

  /** Create a new author */
  createAuthor: (data: AuthorRequest) =>
    API.post<AuthorDto>(BASE, data).then((r) => r.data),

  /** Update an existing author */
  updateAuthor: (id: number, data: AuthorRequest) =>
    API.put<AuthorDto>(`${BASE}/${id}`, data).then((r) => r.data),

  /** Delete an author */
  deleteAuthor: (id: number) =>
    API.delete(`${BASE}/${id}`).then((r) => r.data),
}

export default authorService
