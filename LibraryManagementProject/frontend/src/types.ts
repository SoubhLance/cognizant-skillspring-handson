export interface UserDto {
  id: number
  firstName: string
  lastName: string
  email: string
  phone: string
  role: string
  status: string
  createdAt: string
  permissions?: string[]
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
  phone: string
  role: string
}

export interface JwtResponse {
  token: string
  refreshToken: string
  id: number
  firstName: string
  lastName: string
  email: string
  role: string
  permissions: string[]
}

export interface AuthorDto {
  id: number
  name: string
  biography: string
  birthDate: string
}

export interface PublisherDto {
  id: number
  name: string
  address: string
  contactEmail: string
}

export interface CategoryDto {
  id: number
  name: string
  description: string
}

export interface BookDto {
  id: number
  title: string
  isbn: string
  categoryId: number
  categoryName: string
  publisherId: number
  publisherName: string
  publicationYear: number
  description: string
  coverImageUrl: string
  totalCopies: number
  availableCopies: number
  authors: AuthorDto[]
  createdAt: string
}

export interface BookRequest {
  title: string
  isbn: string
  categoryId: number
  publisherId: number
  publicationYear: number
  description: string
  coverImageUrl: string
  authorIds: number[]
  totalCopies: number
}

export interface BookCopyDto {
  id: number
  bookId: number
  bookTitle: string
  barcode: string
  status: string
  bookCondition: string
}

export interface BookIssueDto {
  id: number
  userId: number
  userEmail: string
  userName: string
  copyId: number
  copyBarcode: string
  bookTitle: string
  isbn: string
  issueDate: string
  dueDate: string
  returnDate: string | null
  status: string
  createdAt: string
}

export interface ReservationDto {
  id: number
  userId: number
  userName: string
  bookId: number
  bookTitle: string
  reservationDate: string
  status: string
  expirationDate: string
}

export interface FineDto {
  id: number
  issueId: number
  bookTitle: string
  userName: string
  amount: number
  status: string
  paymentDate: string | null
  transactionId: string | null
}

export interface WishlistDto {
  id: number
  userId: number
  bookId: number
  bookTitle: string
  coverImageUrl: string
  isbn: string
  addedAt: string
}

export interface BookReviewDto {
  id: number
  bookId: number
  userId: number
  userName: string
  reviewText: string
  createdAt: string
}

export interface BookRatingDto {
  id: number
  bookId: number
  userId: number
  ratingValue: number
  createdAt: string
}

export interface NotificationDto {
  id: number
  userId: number
  message: string
  type: string
  isRead: boolean
  createdAt: string
}

export interface AuditLogDto {
  id: number
  userId: number | null
  userName: string
  action: string
  details: string
  ipAddress: string
  createdAt: string
}

export interface DashboardAnalyticsDto {
  totalBooks: number
  totalUsers: number
  totalBorrowedBooks: number
  totalOverdueBooks: number
  totalFinesCollected: number
  borrowTrend: Array<{ month: string; borrows: number }>
  categoryDistribution: Array<{ category: string; count: number }>
  recentActivities: AuditLogDto[]
}
