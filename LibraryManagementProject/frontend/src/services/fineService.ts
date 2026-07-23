import API from './api'
import type { FineDto } from '../types'

const BASE = '/fines'

const fineService = {
  /** Get all fines (admin/librarian) */
  getAllFines: () =>
    API.get<FineDto[]>(BASE).then((r) => r.data),

  /** Get fines for a specific user */
  getUserFines: (userId: number) =>
    API.get<FineDto[]>(`${BASE}/user/${userId}`).then((r) => r.data),

  /** Mark a fine as paid */
  payFine: (fineId: number) =>
    API.post<FineDto>(`${BASE}/${fineId}/pay`).then((r) => r.data),
}

export default fineService
