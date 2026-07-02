import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import API from '../services/api'
import { BookDto, CategoryDto, AuthorDto, PublisherDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Search, Filter, Plus, FileSpreadsheet, Download, Upload, Edit, Trash, BookOpen } from 'lucide-react'

const Books: React.FC = () => {
  const { user } = useAuth()
  const [books, setBooks] = useState<BookDto[]>([])
  const [categories, setCategories] = useState<CategoryDto[]>([])
  const [authors, setAuthors] = useState<AuthorDto[]>([])
  const [publishers, setPublishers] = useState<PublisherDto[]>([])
  const [loading, setLoading] = useState(true)
  
  // Search Filters State
  const [title, setTitle] = useState('')
  const [isbn, setIsbn] = useState('')
  const [selectedCat, setSelectedCat] = useState('')
  const [selectedAuthor, setSelectedAuthor] = useState('')
  const [availableOnly, setAvailableOnly] = useState(false)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1)

  // Book Add/Edit Modal State
  const [modalOpen, setModalOpen] = useState(false)
  const [editingBookId, setEditingBookId] = useState<number | null>(null)
  const [formTitle, setFormTitle] = useState('')
  const [formIsbn, setFormIsbn] = useState('')
  const [formCat, setFormCat] = useState('')
  const [formPub, setFormPub] = useState('')
  const [formYear, setFormYear] = useState(new Date().getFullYear())
  const [formDesc, setFormDesc] = useState('')
  const [formCover, setFormCover] = useState('')
  const [formAuthors, setFormAuthors] = useState<number[]>([])
  const [formCopies, setFormCopies] = useState(1)
  const [importFile, setImportFile] = useState<File | null>(null)

  const isLibrarian = user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN'

  const fetchFilters = async () => {
    try {
      const [catsRes, authorsRes, pubsRes] = await Promise.all([
        API.get('/categories'),
        API.get('/authors'),
        API.get('/publishers'),
      ])
      setCategories(catsRes.data.data)
      setAuthors(authorsRes.data.data)
      setPublishers(pubsRes.data.data)
    } catch (err) {
      console.error('Failed to load filter directories', err)
    }
  }

  const fetchBooks = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      if (title) params.append('title', title)
      if (isbn) params.append('isbn', isbn)
      if (selectedCat) params.append('categoryId', selectedCat)
      if (selectedAuthor) params.append('authorId', selectedAuthor)
      if (availableOnly) params.append('available', 'true')
      params.append('page', page.toString())
      params.append('size', '8')
      
      const res = await API.get(`/books?${params.toString()}`)
      setBooks(res.data.data.content)
      setTotalPages(res.data.data.totalPages)
    } catch (err) {
      console.error('Failed to fetch books', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchFilters()
  }, [])

  useEffect(() => {
    fetchBooks()
  }, [title, isbn, selectedCat, selectedAuthor, availableOnly, page])

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this book?')) {
      try {
        await API.delete(`/books/${id}`)
        fetchBooks()
      } catch (err) {
        alert('Failed to delete book')
      }
    }
  }

  const handleOpenEdit = (book: BookDto) => {
    setEditingBookId(book.id)
    setFormTitle(book.title)
    setFormIsbn(book.isbn)
    setFormCat(book.categoryId.toString())
    setFormPub(book.publisherId.toString())
    setFormYear(book.publicationYear)
    setFormDesc(book.description)
    setFormCover(book.coverImageUrl)
    setFormAuthors(book.authors.map(a => a.id))
    setFormCopies(book.totalCopies)
    setModalOpen(true)
  }

  const handleOpenAdd = () => {
    setEditingBookId(null)
    setFormTitle('')
    setFormIsbn('')
    setFormCat(categories[0]?.id.toString() || '')
    setFormPub(publishers[0]?.id.toString() || '')
    setFormYear(new Date().getFullYear())
    setFormDesc('')
    setFormCover('')
    setFormAuthors([])
    setFormCopies(1)
    setModalOpen(true)
  }

  const handleSaveBook = async (e: React.FormEvent) => {
    e.preventDefault()
    if (formAuthors.length === 0) {
      alert('Select at least one author')
      return
    }

    const payload = {
      title: formTitle,
      isbn: formIsbn,
      categoryId: parseInt(formCat),
      publisherId: parseInt(formPub),
      publicationYear: formYear,
      description: formDesc,
      coverImageUrl: formCover,
      authorIds: formAuthors,
      totalCopies: formCopies,
    }

    try {
      if (editingBookId) {
        await API.put(`/books/${editingBookId}`, payload)
      } else {
        await API.post('/books', payload)
      }
      setModalOpen(false)
      fetchBooks()
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to save book')
    }
  }

  const handleImportCsv = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!importFile) return
    const formData = new FormData()
    formData.append('file', importFile)
    try {
      await API.post('/reports/books/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      alert('Import successful')
      setImportFile(null)
      fetchBooks()
    } catch (err) {
      alert('Failed to import books from CSV')
    }
  }

  const handleAuthorToggle = (authorId: number) => {
    setFormAuthors(prev =>
      prev.includes(authorId) ? prev.filter(id => id !== authorId) : [...prev, authorId]
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Library Catalog</h1>
          <p className="text-sm text-muted-foreground">Discover books, view availability, and manage files.</p>
        </div>
        {isLibrarian && (
          <button onClick={handleOpenAdd} className="flex items-center gap-1 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
            <Plus size={16} /> Add Book
          </button>
        )}
      </div>

      {/* Advanced Filter Panel */}
      <div className="rounded-xl border bg-card p-5 shadow-sm space-y-4">
        <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-5">
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search title..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full rounded-lg border bg-background py-1.5 pl-9 pr-4 text-sm focus:outline-none"
            />
          </div>
          <div>
            <input
              type="text"
              placeholder="ISBN code..."
              value={isbn}
              onChange={(e) => setIsbn(e.target.value)}
              className="w-full rounded-lg border bg-background py-1.5 px-4 text-sm focus:outline-none"
            />
          </div>
          <div>
            <select
              value={selectedCat}
              onChange={(e) => setSelectedCat(e.target.value)}
              className="w-full rounded-lg border bg-background py-1.5 px-3 text-sm focus:outline-none"
            >
              <option value="">All Categories</option>
              {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div>
            <select
              value={selectedAuthor}
              onChange={(e) => setSelectedAuthor(e.target.value)}
              className="w-full rounded-lg border bg-background py-1.5 px-3 text-sm focus:outline-none"
            >
              <option value="">All Authors</option>
              {authors.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
            </select>
          </div>
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="avail"
              checked={availableOnly}
              onChange={(e) => setAvailableOnly(e.target.checked)}
              className="rounded border"
            />
            <label htmlFor="avail" className="text-sm font-medium text-muted-foreground">Available Only</label>
          </div>
        </div>

        {/* Excel/PDF Exporters & CSV Import for Librarian */}
        {isLibrarian && (
          <div className="flex flex-wrap items-center justify-between gap-4 border-t pt-4">
            <div className="flex items-center gap-3">
              <a href="/api/reports/books/csv" className="flex items-center gap-1 rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary">
                <FileSpreadsheet size={14} /> Export CSV
              </a>
              <a href="/api/reports/books/excel" className="flex items-center gap-1 rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary">
                <Download size={14} /> Export Excel
              </a>
              <a href="/api/reports/books/pdf" target="_blank" rel="noreferrer" className="flex items-center gap-1 rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary">
                <Download size={14} /> Export PDF
              </a>
            </div>
            <form onSubmit={handleImportCsv} className="flex items-center gap-2">
              <input
                type="file"
                accept=".csv"
                onChange={(e) => setImportFile(e.target.files?.[0] || null)}
                className="text-xs file:mr-2 file:rounded file:border file:bg-secondary file:py-1 file:px-2 file:text-xs"
              />
              <button type="submit" disabled={!importFile} className="flex items-center gap-1 rounded bg-primary px-3 py-1.5 text-xs font-semibold text-white hover:bg-primary/95 disabled:opacity-50">
                <Upload size={12} /> Import CSV
              </button>
            </form>
          </div>
        )}
      </div>

      {/* Book Grid */}
      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
        </div>
      ) : books.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground border rounded-xl bg-card">
          No books found in the catalog.
        </div>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 md:grid-cols-4">
          {books.map((book) => (
            <div key={book.id} className="group relative flex flex-col justify-between rounded-xl border bg-card overflow-hidden shadow-sm hover:shadow-md transition-shadow">
              <div className="aspect-[3/4] bg-muted overflow-hidden relative">
                {book.coverImageUrl ? (
                  <img src={book.coverImageUrl} alt={book.title} className="h-full w-full object-cover transition-transform group-hover:scale-105" />
                ) : (
                  <div className="flex h-full w-full items-center justify-center text-muted-foreground">
                    <BookOpen size={48} />
                  </div>
                )}
                <span className={`absolute top-2 right-2 rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                  book.availableCopies > 0 ? 'bg-emerald-500/10 text-emerald-500' : 'bg-red-500/10 text-red-500'
                }`}>
                  {book.availableCopies > 0 ? `${book.availableCopies} Available` : 'Out of Stock'}
                </span>
              </div>
              <div className="p-4 flex-1 flex flex-col justify-between">
                <div>
                  <span className="text-[10px] uppercase font-bold text-primary">{book.categoryName}</span>
                  <Link to={`/books/${book.id}`} className="block mt-1 text-sm font-bold text-foreground hover:text-primary transition-colors line-clamp-1">
                    {book.title}
                  </Link>
                  <p className="text-xs text-muted-foreground mt-0.5 line-clamp-1">
                    by {book.authors.map(a => a.name).join(', ')}
                  </p>
                </div>
                <div className="mt-4 flex items-center justify-between border-t pt-3">
                  <span className="text-[10px] text-muted-foreground">ISBN: {book.isbn}</span>
                  {isLibrarian && (
                    <div className="flex items-center gap-2">
                      <button onClick={() => handleOpenEdit(book)} className="text-muted-foreground hover:text-foreground">
                        <Edit size={14} />
                      </button>
                      <button onClick={() => handleDelete(book.id)} className="text-destructive hover:text-destructive-foreground">
                        <Trash size={14} />
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 mt-6">
          <button
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary disabled:opacity-50"
          >
            Prev
          </button>
          <span className="text-xs font-medium">Page {page + 1} of {totalPages}</span>
          <button
            disabled={page >= totalPages - 1}
            onClick={() => setPage(page + 1)}
            className="rounded border px-3 py-1.5 text-xs font-semibold hover:bg-secondary disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}

      {/* Save Book Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
          <div className="w-full max-w-lg rounded-xl border bg-card p-6 shadow-xl max-h-[90vh] overflow-y-auto">
            <h2 className="text-lg font-bold mb-4">{editingBookId ? 'Edit Book Details' : 'Catalog New Book'}</h2>
            <form onSubmit={handleSaveBook} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Book Title</label>
                <input type="text" required value={formTitle} onChange={e => setFormTitle(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">ISBN Code</label>
                  <input type="text" required value={formIsbn} onChange={e => setFormIsbn(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">Total Copies</label>
                  <input type="number" required min={0} value={formCopies} onChange={e => setFormCopies(parseInt(e.target.value))} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">Category</label>
                  <select value={formCat} onChange={e => setFormCat(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none">
                    {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">Publisher</label>
                  <select value={formPub} onChange={e => setFormPub(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none">
                    {publishers.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">Publication Year</label>
                  <input type="number" required value={formYear} onChange={e => setFormYear(parseInt(e.target.value))} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-muted-foreground">Cover Image URL</label>
                  <input type="text" value={formCover} onChange={e => setFormCover(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" placeholder="https://..." />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground">Description</label>
                <textarea rows={3} value={formDesc} onChange={e => setFormDesc(e.target.value)} className="mt-1 w-full rounded border bg-background py-1.5 px-3 text-sm focus:outline-none" />
              </div>
              <div>
                <label className="block text-xs font-medium text-muted-foreground mb-1">Select Author(s)</label>
                <div className="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto border p-2 rounded bg-background">
                  {authors.map(auth => (
                    <div key={auth.id} className="flex items-center gap-2">
                      <input type="checkbox" id={`auth-${auth.id}`} checked={formAuthors.includes(auth.id)} onChange={() => handleAuthorToggle(auth.id)} className="rounded" />
                      <label htmlFor={`auth-${auth.id}`} className="text-xs">{auth.name}</label>
                    </div>
                  ))}
                </div>
              </div>
              <div className="flex items-center justify-end gap-2 border-t pt-4">
                <button type="button" onClick={() => setModalOpen(false)} className="rounded border px-4 py-2 text-xs font-semibold hover:bg-secondary">
                  Cancel
                </button>
                <button type="submit" className="rounded bg-primary px-4 py-2 text-xs font-semibold text-white hover:bg-primary/95">
                  Save Book
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Books
