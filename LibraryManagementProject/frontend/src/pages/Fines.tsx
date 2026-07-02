import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { FineDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { CreditCard, ShieldCheck } from 'lucide-react'

const Fines: React.FC = () => {
  const { user } = useAuth()
  const [fines, setFines] = useState<FineDto[]>([])
  const [loading, setLoading] = useState(true)

  // Payment Modal State
  const [payModalOpen, setPayModalOpen] = useState(false)
  const [selectedFineId, setSelectedFineId] = useState<number | null>(null)
  const [selectedFineAmount, setSelectedFineAmount] = useState(0)
  const [cardNumber, setCardNumber] = useState('')
  const [cvv, setCvv] = useState('')

  const isLibrarian = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN'

  const fetchFines = async () => {
    setLoading(true)
    try {
      const endpoint = isLibrarian ? '/fines' : `/fines/user/${user?.id}`
      const res = await API.get(endpoint)
      setFines(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchFines()
  }, [])

  const handleOpenPay = (fine: FineDto) => {
    setSelectedFineId(fine.id)
    setSelectedFineAmount(fine.amount)
    setPayModalOpen(true)
  }

  const handlePaySubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedFineId) return
    const payload = {
      fineId: selectedFineId,
      transactionId: 'TXN-' + Math.random().toString(36).substring(2, 9).toUpperCase()
    }
    try {
      await API.post('/fines/pay', payload)
      alert('Payment simulated successfully! Transaction ID: ' + payload.transactionId)
      setPayModalOpen(false)
      setCardNumber('')
      setCvv('')
      fetchFines()
    } catch (err) {
      alert('Failed to process payment')
    }
  }

  const handleWaive = async (id: number) => {
    if (window.confirm('Waive this fine? This cannot be undone.')) {
      try {
        await API.post(`/fines/${id}/waive`)
        alert('Fine waived successfully')
        fetchFines()
      } catch (err) {
        alert('Failed to waive fine')
      }
    }
  }

  const unpaidTotal = fines
    .filter(f => f.status === 'UNPAID')
    .reduce((sum, f) => sum + f.amount, 0)

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Fines & Penalties</h1>
          <p className="text-sm text-muted-foreground">
            {isLibrarian ? 'Monitor unpaid library fines and process manual waiver overrides.' : 'Manage your late return fines and clear outstanding balances.'}
          </p>
        </div>
        <div className="rounded-xl border bg-card px-4 py-2 shadow-sm text-center">
          <span className="text-xs font-semibold text-muted-foreground">Total Outstanding</span>
          <p className="text-xl font-bold text-red-500">${unpaidTotal.toFixed(2)}</p>
        </div>
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
                <th className="py-3 px-6">Fine Amount</th>
                <th className="py-3 px-6">Status</th>
                <th className="py-3 px-6">Payment Date</th>
                <th className="py-3 px-6">Transaction ID</th>
                <th className="py-3 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {fines.length === 0 ? (
                <tr>
                  <td colSpan={isLibrarian ? 8 : 7} className="py-4 text-center">No fines on record</td>
                </tr>
              ) : (
                fines.map((fine) => (
                  <tr key={fine.id} className="border-b hover:bg-muted/40 transition-colors">
                    <td className="py-3 px-6 font-mono text-xs">{fine.id}</td>
                    <td className="py-3 px-6 text-foreground font-semibold">{fine.bookTitle}</td>
                    {isLibrarian && <td className="py-3 px-6 text-xs">{fine.userName}</td>}
                    <td className="py-3 px-6 text-red-500 font-bold">${fine.amount.toFixed(2)}</td>
                    <td className="py-3 px-6">
                      <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                        fine.status === 'PAID' ? 'bg-emerald-500/10 text-emerald-500' :
                        fine.status === 'WAIVED' ? 'bg-amber-500/10 text-amber-500' :
                        'bg-red-500/10 text-red-500'
                      }`}>
                        {fine.status}
                      </span>
                    </td>
                    <td className="py-3 px-6 text-xs">{fine.paymentDate ? new Date(fine.paymentDate).toLocaleDateString() : '-'}</td>
                    <td className="py-3 px-6 font-mono text-xs">{fine.transactionId || '-'}</td>
                    <td className="py-3 px-6 text-right">
                      {fine.status === 'UNPAID' && (
                        <div className="flex items-center justify-end gap-2">
                          {!isLibrarian && (
                            <button onClick={() => handleOpenPay(fine)} className="flex items-center gap-1 rounded bg-primary px-2.5 py-1 text-xs font-semibold text-white hover:bg-primary/95">
                              <CreditCard size={12} /> Pay Fine
                            </button>
                          )}
                          {isLibrarian && (
                            <button onClick={() => handleWaive(fine.id)} className="flex items-center gap-1 rounded border bg-secondary px-2.5 py-1 text-xs font-semibold hover:bg-muted">
                              <ShieldCheck size={12} /> Waive
                            </button>
                          )}
                        </div>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Pay Fine Modal */}
      {payModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
          <div className="w-full max-w-sm rounded-xl border bg-card p-6 shadow-xl">
            <h2 className="text-lg font-bold mb-4 flex items-center gap-2 font-sans"><CreditCard /> Pay Fine Balance</h2>
            <p className="text-sm mb-4">Fine amount to pay: <span className="font-bold text-red-500">${selectedFineAmount.toFixed(2)}</span></p>
            <form onSubmit={handlePaySubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Credit/Debit Card Number</label>
                <input
                  type="text"
                  required
                  placeholder="xxxx-xxxx-xxxx-xxxx"
                  value={cardNumber}
                  onChange={e => setCardNumber(e.target.value)}
                  className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">CVV</label>
                  <input
                    type="password"
                    required
                    maxLength={3}
                    placeholder="•••"
                    value={cvv}
                    onChange={e => setCvv(e.target.value)}
                    className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none"
                  />
                </div>
              </div>
              <div className="flex items-center justify-end gap-2 border-t pt-4">
                <button type="button" onClick={() => setPayModalOpen(false)} className="rounded border px-4 py-2 text-xs font-semibold hover:bg-secondary">
                  Cancel
                </button>
                <button type="submit" className="rounded bg-primary px-4 py-2 text-xs font-semibold text-white hover:bg-primary/95">
                  Confirm Payment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Fines
