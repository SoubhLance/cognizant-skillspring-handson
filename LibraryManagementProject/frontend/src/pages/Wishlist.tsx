import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { WishlistDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { Heart, Trash2, ArrowRight } from 'lucide-react'
import { Link } from 'react-router-dom'

const Wishlist: React.FC = () => {
  const { user } = useAuth()
  const [items, setItems] = useState<WishlistDto[]>([])
  const [loading, setLoading] = useState(true)

  const fetchWishlist = async () => {
    setLoading(true)
    try {
      const res = await API.get(`/wishlist/user/${user?.id}`)
      setItems(res.data.data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchWishlist()
  }, [])

  const handleRemove = async (bookId: number) => {
    try {
      await API.delete(`/wishlist?userId=${user?.id}&bookId=${bookId}`)
      fetchWishlist()
    } catch (err) {
      alert('Failed to remove wishlist item')
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">My Wishlist</h1>
        <p className="text-sm text-muted-foreground">Keep track of books you plan to borrow in the future.</p>
      </div>

      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
        </div>
      ) : items.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground border rounded-xl bg-card">
          <Heart className="mx-auto mb-3 text-muted-foreground/40" size={40} />
          <p className="text-sm">Your wishlist is currently empty.</p>
          <Link to="/books" className="mt-4 inline-flex items-center gap-1 text-xs font-semibold text-primary underline">
            Browse books catalog <ArrowRight size={12} />
          </Link>
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => (
            <div key={item.id} className="flex items-center justify-between border bg-card p-4 rounded-xl shadow-sm">
              <div className="flex items-center gap-4">
                <div className="h-16 w-12 bg-muted rounded overflow-hidden flex-shrink-0 flex items-center justify-center">
                  {item.coverImageUrl ? (
                    <img src={item.coverImageUrl} alt={item.bookTitle} className="h-full w-full object-cover" />
                  ) : (
                    <Heart size={20} className="text-muted-foreground" />
                  )}
                </div>
                <div>
                  <Link to={`/books/${item.bookId}`} className="font-bold text-sm text-foreground hover:text-primary transition-colors line-clamp-1">
                    {item.bookTitle}
                  </Link>
                  <span className="text-[10px] text-muted-foreground font-mono">ISBN: {item.isbn}</span>
                  <p className="text-[10px] text-muted-foreground mt-0.5">Added at {new Date(item.addedAt).toLocaleDateString()}</p>
                </div>
              </div>
              <button onClick={() => handleRemove(item.bookId)} className="rounded p-2 text-destructive hover:bg-destructive/10">
                <Trash2 size={16} />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Wishlist
