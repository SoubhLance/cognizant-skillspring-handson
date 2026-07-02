import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { CategoryDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Plus, Edit, Trash } from 'lucide-react'

const Categories: React.FC = () => {
  const { user } = useAuth()
  const [categories, setCategories] = useState<CategoryDto[]>([])
  const [loading, setLoading] = useState(true)

  // Form State
  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')

  const isLibrarian = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN'

  const fetchCategories = async () => {
    setLoading(true)
    try {
      const res = await API.get('/categories')
      setCategories(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchCategories()
  }, [])

  const handleDelete = async (id: number) => {
    if (window.confirm('Delete this category? This might affect catalog books.')) {
      try {
        await API.delete(`/categories/${id}`)
        fetchCategories()
      } catch (err) {
        alert('Failed to delete category')
      }
    }
  }

  const handleOpenEdit = (cat: CategoryDto) => {
    setEditingId(cat.id)
    setName(cat.name)
    setDescription(cat.description)
    setModalOpen(true)
  }

  const handleOpenAdd = () => {
    setEditingId(null)
    setName('')
    setDescription('')
    setModalOpen(true)
  }

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault()
    const payload = { name, description }
    try {
      if (editingId) {
        await API.put(`/categories/${editingId}`, payload)
      } else {
        await API.post('/categories', payload)
      }
      setModalOpen(false)
      fetchCategories()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to save category')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Book Categories</h1>
          <p className="text-sm text-muted-foreground">Manage genres and catalog groups.</p>
        </div>
        {isLibrarian && (
          <button onClick={handleOpenAdd} className="flex items-center gap-1 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
            <Plus size={16} /> Add Category
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
                <th className="py-3 px-6">Category Name</th>
                <th className="py-3 px-6">Description</th>
                {isLibrarian && <th className="py-3 px-6 text-right">Actions</th>}
              </tr>
            </thead>
            <tbody>
              {categories.map((cat) => (
                <tr key={cat.id} className="border-b hover:bg-muted/40 transition-colors">
                  <td className="py-3 px-6 font-mono text-xs">{cat.id}</td>
                  <td className="py-3 px-6 text-foreground font-semibold">{cat.name}</td>
                  <td className="py-3 px-6">{cat.description || 'No description provided'}</td>
                  {isLibrarian && (
                    <td className="py-3 px-6 text-right flex items-center justify-end gap-2.5">
                      <button onClick={() => handleOpenEdit(cat)} className="text-muted-foreground hover:text-foreground">
                        <Edit size={14} />
                      </button>
                      <button onClick={() => handleDelete(cat.id)} className="text-destructive hover:text-destructive-foreground">
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
            <h2 className="text-lg font-bold mb-4">{editingId ? 'Edit Category' : 'Create Category'}</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Category Name</label>
                <input type="text" required value={name} onChange={e => setName(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Description</label>
                <textarea rows={3} value={description} onChange={e => setDescription(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
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

export default Categories
