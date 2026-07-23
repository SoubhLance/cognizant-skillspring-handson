import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { bookService } from '../services'
import type { BookSearchParams } from '../services/bookService'
import type { BookRequest } from '../types'

// ─── Query Keys ───────────────────────────────────────────────────────────────
export const bookKeys = {
  all: ['books'] as const,
  lists: () => [...bookKeys.all, 'list'] as const,
  list: (params: BookSearchParams) => [...bookKeys.lists(), params] as const,
  detail: (id: number) => [...bookKeys.all, 'detail', id] as const,
  suggestions: (q: string) => [...bookKeys.all, 'suggestions', q] as const,
  recommendations: (catId: number) => [...bookKeys.all, 'recommendations', catId] as const,
}

// ─── Queries ──────────────────────────────────────────────────────────────────

/** Paginated book search with filters */
export function useBooks(params: BookSearchParams = {}) {
  return useQuery({
    queryKey: bookKeys.list(params),
    queryFn: () => bookService.searchBooks(params),
    staleTime: 1000 * 60 * 2, // treat as fresh for 2 min
  })
}

/** Single book by ID */
export function useBook(id: number) {
  return useQuery({
    queryKey: bookKeys.detail(id),
    queryFn: () => bookService.getBookById(id),
    enabled: !!id,
    staleTime: 1000 * 60 * 5,
  })
}

/** Autocomplete title suggestions */
export function useBookSuggestions(query: string) {
  return useQuery({
    queryKey: bookKeys.suggestions(query),
    queryFn: () => bookService.getSearchSuggestions(query),
    enabled: query.length > 1,
    staleTime: 1000 * 30,
  })
}

/** Recommendations by category */
export function useBookRecommendations(categoryId: number) {
  return useQuery({
    queryKey: bookKeys.recommendations(categoryId),
    queryFn: () => bookService.getRecommendations(categoryId),
    enabled: !!categoryId,
    staleTime: 1000 * 60 * 10,
  })
}

// ─── Mutations ────────────────────────────────────────────────────────────────

/** Create / update / delete book mutations with automatic list cache invalidation */
export function useBookMutations() {
  const queryClient = useQueryClient()

  const invalidateBooks = () => queryClient.invalidateQueries({ queryKey: bookKeys.all })

  const createBook = useMutation({
    mutationFn: (data: BookRequest) => bookService.createBook(data),
    onSuccess: invalidateBooks,
  })

  const updateBook = useMutation({
    mutationFn: ({ id, data }: { id: number; data: BookRequest }) =>
      bookService.updateBook(id, data),
    onSuccess: invalidateBooks,
  })

  const deleteBook = useMutation({
    mutationFn: (id: number) => bookService.deleteBook(id),
    onSuccess: invalidateBooks,
  })

  return { createBook, updateBook, deleteBook }
}
