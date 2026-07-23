import API from './api'
import type { CategoryDto } from '../types'

export interface CategoryRequest {
  name: string
  description?: string
}

const BASE = '/categories'

const categoryService = {
  /** Get all categories (cached 30 min on the backend) */
  getAllCategories: () =>
    API.get<CategoryDto[]>(BASE).then((r) => r.data),

  /** Get a category by ID */
  getCategoryById: (id: number) =>
    API.get<CategoryDto>(`${BASE}/${id}`).then((r) => r.data),

  /** Create a new category */
  createCategory: (data: CategoryRequest) =>
    API.post<CategoryDto>(BASE, data).then((r) => r.data),

  /** Update an existing category */
  updateCategory: (id: number, data: CategoryRequest) =>
    API.put<CategoryDto>(`${BASE}/${id}`, data).then((r) => r.data),

  /** Delete a category */
  deleteCategory: (id: number) =>
    API.delete(`${BASE}/${id}`).then((r) => r.data),
}

export default categoryService
