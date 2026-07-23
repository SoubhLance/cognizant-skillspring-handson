import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { categoryService } from '../services'
import type { CategoryRequest } from '../services/categoryService'

export const categoryKeys = {
  all: ['categories'] as const,
  lists: () => [...categoryKeys.all, 'list'] as const,
  detail: (id: number) => [...categoryKeys.all, 'detail', id] as const,
}

/** All categories — staleTime matches backend Redis TTL (30 min) */
export function useCategories() {
  return useQuery({
    queryKey: categoryKeys.lists(),
    queryFn: () => categoryService.getAllCategories(),
    staleTime: 1000 * 60 * 30,
  })
}

/** Single category by ID */
export function useCategory(id: number) {
  return useQuery({
    queryKey: categoryKeys.detail(id),
    queryFn: () => categoryService.getCategoryById(id),
    enabled: !!id,
    staleTime: 1000 * 60 * 30,
  })
}

/** CRUD mutations with automatic cache invalidation */
export function useCategoryMutations() {
  const queryClient = useQueryClient()

  const invalidate = () => queryClient.invalidateQueries({ queryKey: categoryKeys.all })

  const createCategory = useMutation({
    mutationFn: (data: CategoryRequest) => categoryService.createCategory(data),
    onSuccess: invalidate,
  })

  const updateCategory = useMutation({
    mutationFn: ({ id, data }: { id: number; data: CategoryRequest }) =>
      categoryService.updateCategory(id, data),
    onSuccess: invalidate,
  })

  const deleteCategory = useMutation({
    mutationFn: (id: number) => categoryService.deleteCategory(id),
    onSuccess: invalidate,
  })

  return { createCategory, updateCategory, deleteCategory }
}
