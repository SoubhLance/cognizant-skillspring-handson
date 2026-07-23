import { useQuery, useQueryClient } from '@tanstack/react-query'
import { dashboardService } from '../services'

export const dashboardKeys = {
  analytics: ['dashboard', 'analytics'] as const,
}

/**
 * Fetches dashboard analytics.
 * - staleTime: 2 min  (matches backend Redis TTL)
 * - refetchInterval: 2 min  (auto-polls for a live dashboard feel)
 * - refetchIntervalInBackground: false  (pause polling when tab is hidden)
 */
export function useDashboard() {
  return useQuery({
    queryKey: dashboardKeys.analytics,
    queryFn: () => dashboardService.getAnalytics(),
    staleTime: 1000 * 60 * 2,
    refetchInterval: 1000 * 60 * 2,
    refetchIntervalInBackground: false,
  })
}

/** Manually trigger a dashboard refresh (e.g. after a borrow/return action) */
export function useInvalidateDashboard() {
  const queryClient = useQueryClient()
  return () => queryClient.invalidateQueries({ queryKey: dashboardKeys.analytics })
}
