// Central export – import any service from '../services'
export { default as bookService } from './bookService'
export { default as categoryService } from './categoryService'
export { default as authorService } from './authorService'
export { default as publisherService } from './publisherService'
export { default as dashboardService } from './dashboardService'
export { default as bookIssueService } from './bookIssueService'
export { default as reservationService } from './reservationService'
export { default as fineService } from './fineService'
export { default as notificationService } from './notificationService'
export { default as authService } from './authService'

// Re-export API instance for advanced use (e.g. custom one-off requests)
export { default as API } from './api'

// Re-export service-level types
export type { BookSearchParams, PageResponse } from './bookService'
export type { CategoryRequest } from './categoryService'
export type { AuthorRequest } from './authorService'
export type { PublisherRequest } from './publisherService'
export type { BookIssueRequest, BookReturnRequest } from './bookIssueService'
export type { ReservationRequest } from './reservationService'
export type { TokenRefreshRequest, ForgotPasswordRequest, ResetPasswordRequest } from './authService'
