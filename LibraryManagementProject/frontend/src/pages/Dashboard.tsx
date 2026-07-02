import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { DashboardAnalyticsDto } from '../types'
import { 
  AreaChart, Area, XAxis, YAxis, CartesianGrid, 
  Tooltip, ResponsiveContainer, BarChart, Bar, Cell 
} from 'recharts'
import { BookOpen, Users, BookMarked, AlertCircle, DollarSign, RefreshCw } from 'lucide-react'

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']

const Dashboard: React.FC = () => {
  const [data, setData] = useState<DashboardAnalyticsDto | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchAnalytics = async () => {
    setLoading(true)
    setError('')
    try {
      const res = await API.get('/dashboard')
      setData(res.data.data)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load dashboard metrics')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAnalytics()
  }, [])

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    )
  }

  if (error || !data) {
    return (
      <div className="rounded-lg bg-destructive/10 p-4 text-destructive border border-destructive/20">
        <p className="font-semibold">Error loading dashboard</p>
        <p className="text-sm">{error}</p>
        <button onClick={fetchAnalytics} className="mt-2 flex items-center gap-1 text-xs font-semibold underline">
          <RefreshCw size={12} /> Retry
        </button>
      </div>
    )
  }

  const statCards = [
    { label: 'Total Books Cataloged', value: data.totalBooks, icon: <BookOpen className="text-blue-500" />, bg: 'bg-blue-500/10' },
    { label: 'Registered Borrowers', value: data.totalUsers, icon: <Users className="text-emerald-500" />, bg: 'bg-emerald-500/10' },
    { label: 'Active Borrowed Books', value: data.totalBorrowedBooks, icon: <BookMarked className="text-purple-500" />, bg: 'bg-purple-500/10' },
    { label: 'Overdue Borrowings', value: data.totalOverdueBooks, icon: <AlertCircle className="text-red-500" />, bg: 'bg-red-500/10' },
    { label: 'Total Fine Revenue', value: `$${Number(data.totalFinesCollected || 0).toFixed(2)}`, icon: <DollarSign className="text-amber-500" />, bg: 'bg-amber-500/10' }
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">System Analytics Dashboard</h1>
          <p className="text-sm text-muted-foreground">Overview of book inventory, transactions, overdue fines, and user activity.</p>
        </div>
        <button onClick={fetchAnalytics} className="flex items-center gap-1 rounded-lg border px-3 py-1.5 text-xs font-medium hover:bg-secondary">
          <RefreshCw size={14} /> Refresh Stats
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
        {statCards.map((stat, i) => (
          <div key={i} className="rounded-xl border bg-card p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <span className="text-xs font-medium text-muted-foreground">{stat.label}</span>
              <div className={`rounded-lg p-2 ${stat.bg}`}>{stat.icon}</div>
            </div>
            <div className="mt-4">
              <span className="text-2xl font-bold">{stat.value}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Grid */}
      <div className="grid gap-6 md:grid-cols-2">
        {/* Borrowing Trend (Area Chart) */}
        <div className="rounded-xl border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-bold tracking-tight">6-Month Borrowing Trend</h2>
          <div className="h-80 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data.borrowTrend} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorBorrows" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis dataKey="month" stroke="currentColor" className="text-[10px] text-muted-foreground" />
                <YAxis stroke="currentColor" className="text-[10px] text-muted-foreground" />
                <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--card))', border: '1px solid hsl(var(--border))', borderRadius: '8px' }} />
                <Area type="monotone" dataKey="borrows" stroke="#3b82f6" fillOpacity={1} fill="url(#colorBorrows)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Category Distribution (Bar Chart) */}
        <div className="rounded-xl border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-bold tracking-tight">Category Wise Book Stock</h2>
          <div className="h-80 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.categoryDistribution} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis dataKey="category" stroke="currentColor" className="text-[10px] text-muted-foreground" />
                <YAxis stroke="currentColor" className="text-[10px] text-muted-foreground" />
                <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--card))', border: '1px solid hsl(var(--border))', borderRadius: '8px' }} />
                <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                  {data.categoryDistribution.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Recent Activity Table */}
      <div className="rounded-xl border bg-card p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-bold tracking-tight">Recent Administrative Actions</h2>
        <div className="overflow-x-auto">
          <table className="w-full border-collapse text-left text-sm text-muted-foreground">
            <thead>
              <tr className="border-b text-foreground font-semibold">
                <th className="py-3 px-4">Timestamp</th>
                <th className="py-3 px-4">Operator</th>
                <th className="py-3 px-4">Action</th>
                <th className="py-3 px-4">Details</th>
                <th className="py-3 px-4">IP Address</th>
              </tr>
            </thead>
            <tbody>
              {data.recentActivities.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-4 text-center">No recent activity logged</td>
                </tr>
              ) : (
                data.recentActivities.map((act) => (
                  <tr key={act.id} className="border-b hover:bg-muted/40 transition-colors">
                    <td className="py-3 px-4 text-xs">{new Date(act.createdAt).toLocaleString()}</td>
                    <td className="py-3 px-4 text-foreground font-medium">{act.userName}</td>
                    <td className="py-3 px-4 text-xs font-mono uppercase text-primary">{act.action}</td>
                    <td className="py-3 px-4 text-foreground text-xs">{act.details}</td>
                    <td className="py-3 px-4 text-xs">{act.ipAddress || '127.0.0.1'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
