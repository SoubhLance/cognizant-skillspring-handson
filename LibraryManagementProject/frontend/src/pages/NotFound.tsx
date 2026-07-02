import React from 'react'
import { Link } from 'react-router-dom'
import { HelpCircle } from 'lucide-react'

const NotFound: React.FC = () => {
  return (
    <div className="flex h-[70vh] flex-col items-center justify-center text-center space-y-4">
      <div className="rounded-full bg-primary/10 p-4 text-primary">
        <HelpCircle size={48} />
      </div>
      <h1 className="text-3xl font-extrabold tracking-tight">404 Page Not Found</h1>
      <p className="max-w-md text-sm text-muted-foreground">The resource, book cover link, or interface path you are seeking does not exist or has been archived.</p>
      <Link to="/books" className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
        Return to Catalog
      </Link>
    </div>
  )
}

export default NotFound
