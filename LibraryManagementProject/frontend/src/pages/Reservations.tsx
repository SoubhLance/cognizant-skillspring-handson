import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { ReservationDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Calendar, Trash2 } from 'lucide-react'

const Reservations: React.FC = () => {
  const { user } = useAuth()
  const [reservations, setReservations] = useState<ReservationDto[]>([])
  const [loading, setLoading] = useState(true)

  const isLibrarian = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN'

  const fetchReservations = async () => {
    setLoading(true)
    try {
      const endpoint = isLibrarian ? '/reservations' : `/reservations/user/${user?.id}`
      const res = await API.get(endpoint)
      setReservations(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchReservations()
  }, [])

  const handleCancelHold = async (id: number) => {
    if (window.confirm('Cancel this book hold reservation?')) {
      try {
        await API.post(`/reservations/${id}/cancel`)
        alert('Hold successfully cancelled')
        fetchReservations()
      } catch (err) {
        alert('Failed to cancel hold reservation')
      }
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Book Hold Reservations</h1>
        <p className="text-sm text-muted-foreground">
          {isLibrarian ? 'Manage user book holds and track reservation queue metrics.' : 'Track your active holds, pending books, and reservation expiration.'}
        </p>
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
                <th className="py-3 px-6">Book Title</th>
                {isLibrarian && <th className="py-3 px-6">Borrower</th>}
                <th className="py-3 px-6">Reserved At</th>
                <th className="py-3 px-6">Expiration Date</th>
                <th className="py-3 px-6">Status</th>
                <th className="py-3 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {reservations.length === 0 ? (
                <tr>
                  <td colSpan={isLibrarian ? 7 : 6} className="py-4 text-center">No holds or reservations cataloged</td>
                </tr>
              ) : (
                reservations.map((res) => (
                  <tr key={res.id} className="border-b hover:bg-muted/40 transition-colors">
                    <td className="py-3 px-6 font-mono text-xs">{res.id}</td>
                    <td className="py-3 px-6 text-foreground font-semibold">{res.bookTitle}</td>
                    {isLibrarian && <td className="py-3 px-6 text-xs">{res.userName}</td>}
                    <td className="py-3 px-6 text-xs">{new Date(res.reservationDate).toLocaleString()}</td>
                    <td className="py-3 px-6 text-xs">{new Date(res.expirationDate).toLocaleString()}</td>
                    <td className="py-3 px-6">
                      <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                        res.status === 'COMPLETED' ? 'bg-emerald-500/10 text-emerald-500' :
                        res.status === 'CANCELLED' || res.status === 'EXPIRED' ? 'bg-red-500/10 text-red-500' :
                        'bg-blue-500/10 text-blue-500'
                      }`}>
                        {res.status}
                      </span>
                    </td>
                    <td className="py-3 px-6 text-right">
                      {res.status === 'PENDING' && (
                        <button onClick={() => handleCancelHold(res.id)} className="flex items-center gap-1 ml-auto rounded border bg-destructive/10 text-destructive px-2.5 py-1 text-xs font-semibold hover:bg-destructive hover:text-white">
                          <Trash2 size={12} /> Cancel Hold
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default Reservations
