import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { PublisherDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Plus, Edit, Trash } from 'lucide-react'

const Publishers: React.FC = () => {
  const { user } = useAuth()
  const [publishers, setPublishers] = useState<PublisherDto[]>([])
  const [loading, setLoading] = useState(true)

  // Form State
  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [name, setName] = useState('')
  const [address, setAddress] = useState('')
  const [contactEmail, setContactEmail] = useState('')

  const isLibrarian = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN'

  const fetchPublishers = async () => {
    setLoading(true)
    try {
      const res = await API.get('/publishers')
      setPublishers(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPublishers()
  }, [])

  const handleDelete = async (id: number) => {
    if (window.confirm('Delete this publisher? This will cascade to catalog books.')) {
      try {
        await API.delete(`/publishers/${id}`)
        fetchPublishers()
      } catch (err) {
        alert('Failed to delete publisher')
      }
    }
  }

  const handleOpenEdit = (pub: PublisherDto) => {
    setEditingId(pub.id)
    setName(pub.name)
    setAddress(pub.address)
    setContactEmail(pub.contactEmail)
    setModalOpen(true)
  }

  const handleOpenAdd = () => {
    setEditingId(null)
    setName('')
    setAddress('')
    setContactEmail('')
    setModalOpen(true)
  }

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault()
    const payload = { name, address, contactEmail }
    try {
      if (editingId) {
        await API.put(`/publishers/${editingId}`, payload)
      } else {
        await API.post('/publishers', payload)
      }
      setModalOpen(false)
      fetchPublishers()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to save publisher information')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Book Publishers</h1>
          <p className="text-sm text-muted-foreground">Manage printing press companies and contacts.</p>
        </div>
        {isLibrarian && (
          <button onClick={handleOpenAdd} className="flex items-center gap-1 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
            <Plus size={16} /> Add Publisher
          </button>
        )}
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
                <th className="py-3 px-6">Publisher Name</th>
                <th className="py-3 px-6">Contact Email</th>
                <th className="py-3 px-6">Address</th>
                {isLibrarian && <th className="py-3 px-6 text-right">Actions</th>}
              </tr>
            </thead>
            <tbody>
              {publishers.map((pub) => (
                <tr key={pub.id} className="border-b hover:bg-muted/40 transition-colors">
                  <td className="py-3 px-6 font-mono text-xs">{pub.id}</td>
                  <td className="py-3 px-6 text-foreground font-semibold">{pub.name}</td>
                  <td className="py-3 px-6 text-xs">{pub.contactEmail || 'N/A'}</td>
                  <td className="py-3 px-6 text-xs max-w-xs truncate">{pub.address || 'N/A'}</td>
                  {isLibrarian && (
                    <td className="py-3 px-6 text-right flex items-center justify-end gap-2.5">
                      <button onClick={() => handleOpenEdit(pub)} className="text-muted-foreground hover:text-foreground">
                        <Edit size={14} />
                      </button>
                      <button onClick={() => handleDelete(pub.id)} className="text-destructive hover:text-destructive-foreground">
                        <Trash size={14} />
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
          <div className="w-full max-w-md rounded-xl border bg-card p-6 shadow-xl">
            <h2 className="text-lg font-bold mb-4">{editingId ? 'Edit Publisher' : 'Add Publisher'}</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Publisher Name</label>
                <input type="text" required value={name} onChange={e => setName(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Contact Email</label>
                <input type="email" value={contactEmail} onChange={e => setContactEmail(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Address Office</label>
                <textarea rows={3} value={address} onChange={e => setAddress(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div className="flex items-center justify-end gap-2 border-t pt-4">
                <button type="button" onClick={() => setModalOpen(false)} className="rounded border px-4 py-2 text-xs font-semibold hover:bg-secondary">
                  Cancel
                </button>
                <button type="submit" className="rounded bg-primary px-4 py-2 text-xs font-semibold text-white hover:bg-primary/95">
                  Save
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Publishers
