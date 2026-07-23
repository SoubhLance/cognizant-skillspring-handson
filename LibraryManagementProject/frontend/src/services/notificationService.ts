import API from './api'
import type { NotificationDto } from '../types'

const BASE = '/notifications'

const notificationService = {
  /** Get notifications for the current authenticated user */
  getMyNotifications: () =>
    API.get<NotificationDto[]>(`${BASE}/my`).then((r) => r.data),

  /** Mark a single notification as read */
  markAsRead: (notificationId: number) =>
    API.put<NotificationDto>(`${BASE}/${notificationId}/read`).then((r) => r.data),

  /** Mark all notifications as read for current user */
  markAllAsRead: () =>
    API.put(`${BASE}/read-all`).then((r) => r.data),
}

export default notificationService
