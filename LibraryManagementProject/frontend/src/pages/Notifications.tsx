import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { NotificationDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Bell, Check } from 'lucide-react'

const Notifications: React.FC = () => {
  const { user } = useAuth()
  const [notifications, setNotifications] = useState<NotificationDto[]>([])
  const [loading, setLoading] = useState(true)

  const fetchNotifications = async () => {
    setLoading(true)
    try {
      const res = await API.get(`/notifications/user/${user?.id}`)
      setNotifications(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchNotifications()
  }, [])

  const handleMarkAsRead = async (id: number) => {
    try {
      await API.put(`/notifications/${id}/read`)
      fetchNotifications()
    } catch (err) {
      alert('Failed to update notification')
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">System Alerts</h1>
        <p className="text-sm text-muted-foreground">Stay informed on borrow due dates, security actions, and catalog listings.</p>
      </div>

      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
        </div>
      ) : notifications.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground border rounded-xl bg-card">
          <Bell className="mx-auto mb-3 text-muted-foreground/40" size={40} />
          <p className="text-sm">You have no active notifications.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {notifications.map((notif) => (
            <div key={notif.id} className={`flex items-start justify-between border p-4 rounded-xl shadow-sm transition-colors ${
              notif.isRead ? 'bg-card' : 'bg-primary/5 border-primary/20'
            }`}>
              <div className="space-y-1">
                <span className={`text-[9px] uppercase font-bold px-2 py-0.5 rounded ${
                  notif.type === 'FINE' || notif.type === 'OVERDUE' ? 'bg-red-500/10 text-red-500' :
                  notif.type === 'SECURITY' ? 'bg-amber-500/10 text-amber-500' :
                  'bg-blue-500/10 text-blue-500'
                }`}>{notif.type}</span>
                <p className="text-sm leading-relaxed text-foreground">{notif.message}</p>
                <span className="text-[10px] text-muted-foreground block">{new Date(notif.createdAt).toLocaleString()}</span>
              </div>
              {!notif.isRead && (
                <button onClick={() => handleMarkAsRead(notif.id)} className="rounded-lg p-1.5 text-primary hover:bg-primary/10">
                  <Check size={16} />
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Notifications
