import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { UserDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { ShieldAlert, ShieldCheck } from 'lucide-react'

const Users: React.FC = () => {
  const { user: currentUser } = useAuth()
  const [users, setUsers] = useState<UserDto[]>([])
  const [loading, setLoading] = useState(true)

  const fetchUsers = async () => {
    setLoading(true)
    try {
      const res = await API.get('/users')
      // Page result
      setUsers(res.data.data.content)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchUsers()
  }, [])

  const handleToggleStatus = async (user: UserDto) => {
    if (user.id === currentUser?.id) {
      alert('Cannot suspend your own active session account!')
      return
    }
    const newStatus = user.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'
    if (window.confirm(`Change status of ${user.firstName} to ${newStatus}?`)) {
      try {
        await API.put(`/users/${user.id}/status?status=${newStatus}`)
        fetchUsers()
      } catch (err) {
        alert('Failed to update status')
      }
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">System User Directory</h1>
        <p className="text-sm text-muted-foreground">Manage library patrons, faculty, librarians, and adjust permissions.</p>
      </div>

      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
        </div>
      ) : (
        <div className="rounded-xl border bg-card shadow-sm overflow-x-auto">
          <table className="w-full border-collapse text-left text-sm text-muted-foreground">
            <thead>
              <tr className="border-b text-foreground font-semibold">
                <th className="py-3 px-6">ID</th>
                <th className="py-3 px-6">Full Name</th>
                <th className="py-3 px-6">Email Address</th>
                <th className="py-3 px-6">Phone</th>
                <th className="py-3 px-6">System Role</th>
                <th className="py-3 px-6">Status</th>
                <th className="py-3 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-b hover:bg-muted/40 transition-colors">
                  <td className="py-3 px-6 font-mono text-xs">{u.id}</td>
                  <td className="py-3 px-6 text-foreground font-semibold">{u.firstName} {u.lastName}</td>
                  <td className="py-3 px-6 text-xs">{u.email}</td>
                  <td className="py-3 px-6 text-xs">{u.phone || 'N/A'}</td>
                  <td className="py-3 px-6">
                    <span className="text-xs font-semibold px-2 py-0.5 rounded bg-primary/10 text-primary">
                      {u.role.replace('ROLE_', '')}
                    </span>
                  </td>
                  <td className="py-3 px-6">
                    <span className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                      u.status === 'ACTIVE' ? 'bg-emerald-500/10 text-emerald-500' : 'bg-red-500/10 text-red-500 animate-pulse'
                    }`}>
                      {u.status}
                    </span>
                  </td>
                  <td className="py-3 px-6 text-right">
                    <button
                      onClick={() => handleToggleStatus(u)}
                      className={`inline-flex items-center gap-1 rounded border px-2.5 py-1 text-xs font-semibold ${
                        u.status === 'ACTIVE' 
                          ? 'bg-red-500/10 text-red-500 hover:bg-red-500 hover:text-white border-red-500/20' 
                          : 'bg-emerald-500/10 text-emerald-500 hover:bg-emerald-500 hover:text-white border-emerald-500/20'
                      }`}
                    >
                      {u.status === 'ACTIVE' ? (
                        <>
                          <ShieldAlert size={12} /> Suspend
                        </>
                      ) : (
                        <>
                          <ShieldCheck size={12} /> Activate
                        </>
                      )}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default Users
