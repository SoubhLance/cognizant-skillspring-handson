import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { BookIssueDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Plus, CheckSquare, History, FileSpreadsheet, Download } from 'lucide-react'

const BookIssues: React.FC = () => {
  const { user } = useAuth()
  const [issues, setIssues] = useState<BookIssueDto[]>([])
  const [loading, setLoading] = useState(true)

  // Modals state
  const [issueModalOpen, setIssueModalOpen] = useState(false)
  const [returnModalOpen, setReturnModalOpen] = useState(false)

  // Issue Form State
  const [issueUserId, setIssueUserId] = useState('')
  const [issueBarcode, setIssueBarcode] = useState('')
  const [issueDueDate, setIssueDueDate] = useState('')

  // Return Form State
  const [returnBarcode, setReturnBarcode] = useState('')
  const [returnCondition, setReturnCondition] = useState('Good')

  const isLibrarian = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN'

  const fetchIssues = async () => {
    setLoading(true)
    try {
      const endpoint = isLibrarian ? '/issues' : `/issues/user/${user?.id}`
      const res = await API.get(endpoint)
      setIssues(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchIssues()
  }, [])

  const handleIssueSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const payload = {
      userId: parseInt(issueUserId),
      barcode: issueBarcode,
      dueDate: issueDueDate || null
    }
    try {
      await API.post('/issues/issue', payload)
      setIssueModalOpen(false)
      setIssueUserId('')
      setIssueBarcode('')
      setIssueDueDate('')
      fetchIssues()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to issue book')
    }
  }

  const handleReturnSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const payload = {
      barcode: returnBarcode,
      condition: returnCondition
    }
    try {
      await API.post('/issues/return', payload)
      setReturnModalOpen(false)
      setReturnBarcode('')
      setReturnCondition('Good')
      fetchIssues()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to return book')
    }
  }

  const handleRenew = async (id: number) => {
    try {
      await API.post(`/issues/${id}/renew`)
      alert('Book loan successfully extended!')
      fetchIssues()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to renew book')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">
            {isLibrarian ? 'Book Borrow Management' : 'My Borrow History'}
          </h1>
          <p className="text-sm text-muted-foreground">
            {isLibrarian ? 'Check out book copies, process return condition reviews, and assess overdue timelines.' : 'Track your borrowed books, active loans, and due dates.'}
          </p>
        </div>
        {isLibrarian && (
          <div className="flex items-center gap-2">
            <button onClick={() => setIssueModalOpen(true)} className="flex items-center gap-1 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
              <Plus size={16} /> Checkout Book
            </button>
            <button onClick={() => setReturnModalOpen(true)} className="flex items-center gap-1 rounded-lg border px-4 py-2 text-sm font-semibold hover:bg-secondary">
              <CheckSquare size={16} /> Return Book
            </button>
          </div>
        )}
      </div>

      {isLibrarian && (
        <div className="rounded-xl border bg-card p-4 shadow-sm flex gap-3">
          <a href="/api/reports/issues/csv" className="flex items-center gap-1 rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary">
            <FileSpreadsheet size={14} /> Export CSV
          </a>
          <a href="/api/reports/issues/excel" className="flex items-center gap-1 rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary">
            <Download size={14} /> Export Excel
          </a>
          <a href="/api/reports/issues/pdf" target="_blank" rel="noreferrer" className="flex items-center gap-1 rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary">
            <Download size={14} /> Export PDF
          </a>
        </div>
      )}

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
                <th className="py-3 px-6">Copy Barcode</th>
                {isLibrarian && <th className="py-3 px-6">Borrower</th>}
                <th className="py-3 px-6">Issue Date</th>
                <th className="py-3 px-6">Due Date</th>
                <th className="py-3 px-6">Return Date</th>
                <th className="py-3 px-6">Status</th>
                <th className="py-3 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {issues.length === 0 ? (
                <tr>
                  <td colSpan={isLibrarian ? 9 : 8} className="py-4 text-center">No book issues recorded</td>
                </tr>
              ) : (
                issues.map((issue) => (
                  <tr key={issue.id} className="border-b hover:bg-muted/40 transition-colors">
                    <td className="py-3 px-6 font-mono text-xs">{issue.id}</td>
                    <td className="py-3 px-6 text-foreground font-semibold">{issue.bookTitle}</td>
                    <td className="py-3 px-6 font-mono text-xs">{issue.copyBarcode}</td>
                    {isLibrarian && <td className="py-3 px-6 text-xs">{issue.userName} ({issue.userEmail})</td>}
                    <td className="py-3 px-6 text-xs">{new Date(issue.issueDate).toLocaleDateString()}</td>
                    <td className="py-3 px-6 text-xs">{new Date(issue.dueDate).toLocaleDateString()}</td>
                    <td className="py-3 px-6 text-xs">{issue.returnDate ? new Date(issue.returnDate).toLocaleDateString() : 'Active'}</td>
                    <td className="py-3 px-6">
                      <span className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                        issue.status === 'RETURNED' ? 'bg-emerald-500/10 text-emerald-500' :
                        issue.status === 'OVERDUE' ? 'bg-red-500/10 text-red-500 animate-pulse' :
                        'bg-blue-500/10 text-blue-500'
                      }`}>
                        {issue.status}
                      </span>
                    </td>
                    <td className="py-3 px-6 text-right">
                      {issue.status !== 'RETURNED' && (
                        <button onClick={() => handleRenew(issue.id)} className="flex items-center gap-1 ml-auto rounded border bg-secondary px-2.5 py-1 text-xs font-semibold hover:bg-muted">
                          <History size={12} /> Renew
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

      {/* Checkout Book Modal */}
      {issueModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
          <div className="w-full max-w-md rounded-xl border bg-card p-6 shadow-xl">
            <h2 className="text-lg font-bold mb-4 font-sans">Checkout Book Copy</h2>
            <form onSubmit={handleIssueSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Borrower User ID</label>
                <input type="number" required value={issueUserId} onChange={e => setIssueUserId(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" placeholder="e.g. 3" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Book Copy Barcode</label>
                <input type="text" required value={issueBarcode} onChange={e => setIssueBarcode(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" placeholder="e.g. CC-COPY-2" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Custom Due Date (Optional)</label>
                <input type="date" value={issueDueDate} onChange={e => setIssueDueDate(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div className="flex items-center justify-end gap-2 border-t pt-4">
                <button type="button" onClick={() => setIssueModalOpen(false)} className="rounded border px-4 py-2 text-xs font-semibold hover:bg-secondary">
                  Cancel
                </button>
                <button type="submit" className="rounded bg-primary px-4 py-2 text-xs font-semibold text-white hover:bg-primary/95">
                  Confirm Checkout
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Return Book Modal */}
      {returnModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
          <div className="w-full max-w-md rounded-xl border bg-card p-6 shadow-xl">
            <h2 className="text-lg font-bold mb-4 font-sans">Process Book Return</h2>
            <form onSubmit={handleReturnSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Copy Barcode</label>
                <input type="text" required value={returnBarcode} onChange={e => setReturnBarcode(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" placeholder="e.g. CC-COPY-1" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Returned Physical Condition</label>
                <select value={returnCondition} onChange={e => setReturnCondition(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none">
                  <option value="Good">Good (Ready for Reissue)</option>
                  <option value="Fair">Fair (Minor Wear)</option>
                  <option value="Damaged">Damaged (Requires Repair)</option>
                  <option value="Lost">Lost</option>
                </select>
              </div>
              <div className="flex items-center justify-end gap-2 border-t pt-4">
                <button type="button" onClick={() => setReturnModalOpen(false)} className="rounded border px-4 py-2 text-xs font-semibold hover:bg-secondary">
                  Cancel
                </button>
                <button type="submit" className="rounded bg-primary px-4 py-2 text-xs font-semibold text-white hover:bg-primary/95">
                  Process Check-in
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default BookIssues
