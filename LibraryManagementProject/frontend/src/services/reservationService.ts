import API from './api'
import type { ReservationDto } from '../types'

export interface ReservationRequest {
  userId: number
  bookId: number
}

const BASE = '/reservations'

const reservationService = {
  /** Create a new book hold/reservation */
  createReservation: (data: ReservationRequest) =>
    API.post<ReservationDto>(BASE, data).then((r) => r.data),

  /** Cancel an existing reservation */
  cancelReservation: (reservationId: number) =>
    API.put<ReservationDto>(`${BASE}/${reservationId}/cancel`).then((r) => r.data),

  /** Get all reservations (admin/librarian) */
  getAllReservations: () =>
    API.get<ReservationDto[]>(BASE).then((r) => r.data),

  /** Get reservations for a specific user */
  getUserReservations: (userId: number) =>
    API.get<ReservationDto[]>(`${BASE}/user/${userId}`).then((r) => r.data),
}

export default reservationService
