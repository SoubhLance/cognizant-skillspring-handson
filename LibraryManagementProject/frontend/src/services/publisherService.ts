import API from './api'
import type { PublisherDto } from '../types'

export interface PublisherRequest {
  name: string
  address?: string
  contactEmail?: string
}

const BASE = '/publishers'

const publisherService = {
  /** Get all publishers (cached 30 min on the backend) */
  getAllPublishers: () =>
    API.get<PublisherDto[]>(BASE).then((r) => r.data),

  /** Get a publisher by ID */
  getPublisherById: (id: number) =>
    API.get<PublisherDto>(`${BASE}/${id}`).then((r) => r.data),

  /** Create a new publisher */
  createPublisher: (data: PublisherRequest) =>
    API.post<PublisherDto>(BASE, data).then((r) => r.data),

  /** Update an existing publisher */
  updatePublisher: (id: number, data: PublisherRequest) =>
    API.put<PublisherDto>(`${BASE}/${id}`, data).then((r) => r.data),

  /** Delete a publisher */
  deletePublisher: (id: number) =>
    API.delete(`${BASE}/${id}`).then((r) => r.data),
}

export default publisherService
