import React, { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import API from '../services/api'
import { BookDto, BookReviewDto, UserDto } from '../types'
import { useAuth } from '../context/AuthContext'
import { BookOpen, Star, MessageSquare, Plus, Heart, Calendar } from 'lucide-react'

const BookDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const { user } = useAuth()
  const [book, setBook] = useState<BookDto | null>(null)
  const [reviews, setReviews] = useState<BookReviewDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // Rating & Review Form State
  const [rating, setRating] = useState(5)
  const [reviewText, setReviewText] = useState('')
  const [submittingReview, setSubmittingReview] = useState(false)

  const fetchBookDetails = async () => {
    setLoading(true)
    setError('')
    try {
      const [bookRes, reviewsRes] = await Promise.all([
        API.get(`/books/${id}`),
        API.get(`/reviews/book/${id}`),
      ])
      setBook(bookRes.data.data)
      setReviews(reviewsRes.data.data)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch book specifications')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchBookDetails()
  }, [id])

  const handleAddToWishlist = async () => {
    if (!user) return
    try {
      await API.post(`/wishlist?userId=${user.id}&bookId=${book?.id}`)
      alert('Book added to your wishlist!')
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to add to wishlist')
    }
  }

  const handleReserveBook = async () => {
    if (!user || !book) return
    try {
      await API.post('/reservations', { userId: user.id, bookId: book.id })
      alert('Hold reservation created successfully! You will be notified when available.')
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to place reservation hold')
    }
  }

  const handlePostReview = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!user || !book) return
    setSubmittingReview(true)
    try {
      // Post Rating first, then review text
      await API.post('/ratings', { bookId: book.id, userId: user.id, ratingValue: rating })
      await API.post('/reviews', { bookId: book.id, userId: user.id, reviewText })
      
      setReviewText('')
      fetchBookDetails() // Refresh
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to post review details')
    } finally {
      setSubmittingReview(false)
    }
  }

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-10 w-10 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    )
  }

  if (error || !book) {
    return (
      <div className="rounded-lg bg-destructive/10 p-4 text-destructive border border-destructive/20">
        <p className="font-semibold">Error loading book specifications</p>
        <p className="text-sm">{error}</p>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      {/* Book Primary Card */}
      <div className="rounded-xl border bg-card p-6 shadow-sm flex flex-col md:flex-row gap-8">
        <div className="w-full md:w-64 aspect-[3/4] bg-muted rounded-lg overflow-hidden flex-shrink-0">
          {book.coverImageUrl ? (
            <img src={book.coverImageUrl} alt={book.title} className="h-full w-full object-cover" />
          ) : (
            <div className="flex h-full w-full items-center justify-center text-muted-foreground">
              <BookOpen size={64} />
            </div>
          )}
        </div>

        <div className="flex-1 flex flex-col justify-between space-y-4">
          <div>
            <div className="flex items-center justify-between">
              <span className="text-xs uppercase font-bold text-primary">{book.categoryName}</span>
              <span className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                book.availableCopies > 0 ? 'bg-emerald-500/10 text-emerald-500' : 'bg-red-500/10 text-red-500'
              }`}>
                {book.availableCopies > 0 ? `${book.availableCopies} Copies Available` : 'Out of Stock'}
              </span>
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight mt-1">{book.title}</h1>
            <p className="text-sm text-muted-foreground mt-1">
              by <span className="font-medium text-foreground">{book.authors.map(a => a.name).join(', ')}</span>
            </p>
            <p className="text-sm text-muted-foreground">
              Published by <span className="font-medium text-foreground">{book.publisherName}</span> ({book.publicationYear})
            </p>

            <div className="mt-4 border-t pt-4">
              <h2 className="font-bold text-sm text-foreground">Synopsis</h2>
              <p className="text-sm text-muted-foreground mt-1 leading-relaxed">{book.description || 'No description cataloged for this book.'}</p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3 border-t pt-4">
            <button onClick={handleReserveBook} className="flex items-center gap-1.5 rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
              <Calendar size={16} /> Reserve Book
            </button>
            <button onClick={handleAddToWishlist} className="flex items-center gap-1.5 rounded-lg border px-4 py-2 text-sm font-semibold hover:bg-secondary">
              <Heart size={16} className="text-rose-500 fill-rose-500" /> Add to Wishlist
            </button>
            <Link to="/books" className="rounded-lg border px-4 py-2 text-sm font-semibold hover:bg-secondary">
              Back to Catalog
            </Link>
          </div>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        {/* Reviews Section */}
        <div className="md:col-span-2 rounded-xl border bg-card p-6 shadow-sm space-y-4">
          <h2 className="text-lg font-bold tracking-tight">User Reviews & Discussion</h2>

          {/* Add Review Form */}
          {user && (
            <form onSubmit={handlePostReview} className="border-b pb-4 space-y-3">
              <div>
                <label className="block text-xs font-semibold mb-1">Your Star Rating</label>
                <div className="flex items-center gap-1">
                  {[1, 2, 3, 4, 5].map(num => (
                    <button type="button" key={num} onClick={() => setRating(num)} className="text-amber-400">
                      <Star size={20} fill={num <= rating ? 'currentColor' : 'none'} />
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="block text-xs font-semibold mb-1">Write your feedback</label>
                <textarea
                  required
                  rows={3}
                  value={reviewText}
                  onChange={e => setReviewText(e.target.value)}
                  className="w-full rounded border bg-background p-2 text-sm focus:outline-none"
                  placeholder="Share your thoughts on Uncle Bob's principles..."
                />
              </div>
              <button
                type="submit"
                disabled={submittingReview}
                className="flex items-center gap-1.5 rounded bg-primary px-3 py-1.5 text-xs font-semibold text-white hover:bg-primary/95"
              >
                <MessageSquare size={12} /> Post Review
              </button>
            </form>
          )}

          {/* Reviews List */}
          <div className="space-y-4 max-h-[400px] overflow-y-auto pr-2">
            {reviews.length === 0 ? (
              <p className="text-sm text-muted-foreground py-4">No reviews posted yet. Be the first to share your thoughts!</p>
            ) : (
              reviews.map((rev) => (
                <div key={rev.id} className="border-b pb-3 space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-semibold text-foreground">{rev.userName}</span>
                    <span className="text-muted-foreground">{new Date(rev.createdAt).toLocaleDateString()}</span>
                  </div>
                  <p className="text-sm leading-relaxed text-muted-foreground">{rev.reviewText}</p>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Book Details Directory Sidebar */}
        <div className="rounded-xl border bg-card p-6 shadow-sm space-y-4 h-fit">
          <h2 className="text-lg font-bold tracking-tight">System Catalog Information</h2>
          <div className="space-y-2.5 text-sm">
            <div className="flex justify-between border-b pb-1.5">
              <span className="text-muted-foreground">ISBN-13 Code</span>
              <span className="font-semibold">{book.isbn}</span>
            </div>
            <div className="flex justify-between border-b pb-1.5">
              <span className="text-muted-foreground">Category</span>
              <span className="font-semibold">{book.categoryName}</span>
            </div>
            <div className="flex justify-between border-b pb-1.5">
              <span className="text-muted-foreground">Publisher</span>
              <span className="font-semibold">{book.publisherName}</span>
            </div>
            <div className="flex justify-between border-b pb-1.5">
              <span className="text-muted-foreground">Publication Year</span>
              <span className="font-semibold">{book.publicationYear}</span>
            </div>
            <div className="flex justify-between border-b pb-1.5">
              <span className="text-muted-foreground">Total Stock</span>
              <span className="font-semibold">{book.totalCopies} Copies</span>
            </div>
            <div className="flex justify-between border-b pb-1.5">
              <span className="text-muted-foreground">Current Available</span>
              <span className="font-semibold">{book.availableCopies} Copies</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default BookDetails
