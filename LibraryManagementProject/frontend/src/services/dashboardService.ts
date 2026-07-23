import API from './api'
import type { DashboardAnalyticsDto } from '../types'

const dashboardService = {
  /** Fetch dashboard analytics (cached 2 min on the backend) */
  getAnalytics: () =>
    API.get<DashboardAnalyticsDto>('/dashboard/analytics').then((r) => r.data),
}

export default dashboardService
