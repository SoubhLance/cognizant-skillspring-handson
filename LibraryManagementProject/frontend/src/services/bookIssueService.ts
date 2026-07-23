import API from './api'
import type { BookIssueDto } from '../types'

export interface BookIssueRequest {
  userId: number
  barcode: string
  dueDate?: string
}

export interface BookReturnRequest {
  barcode: string
  condition: 'GOOD' | 'FAIR' | 'POOR' | 'DAMAGED'
}

const BASE = '/issues'

const bookIssueService = {
  /** Issue a book copy to a user */
  issueBook: (data: BookIssueRequest) =>
    API.post<BookIssueDto>(BASE, data).then((r) => r.data),

  /** Return a borrowed book copy */
  returnBook: (data: BookReturnRequest) =>
    API.post<BookIssueDto>(`${BASE}/return`, data).then((r) => r.data),

  /** Renew an active loan by 14 days */
  renewBook: (issueId: number) =>
    API.post<BookIssueDto>(`${BASE}/${issueId}/renew`).then((r) => r.data),

  /** Get all book loans (admin/librarian) */
  getAllIssues: () =>
    API.get<BookIssueDto[]>(BASE).then((r) => r.data),

  /** Get loans for a specific user */
  getUserIssues: (userId: number) =>
    API.get<BookIssueDto[]>(`${BASE}/user/${userId}`).then((r) => r.data),

  /** Get all currently overdue loans */
  getOverdueIssues: () =>
    API.get<BookIssueDto[]>(`${BASE}/overdue`).then((r) => r.data),
}

export default bookIssueService
