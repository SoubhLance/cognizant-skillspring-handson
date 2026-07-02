import React from 'react'
import { Link } from 'react-router-dom'
import { ShieldAlert } from 'lucide-react'

const Unauthorized: React.FC = () => {
  return (
    <div className="flex h-[70vh] flex-col items-center justify-center text-center space-y-4">
      <div className="rounded-full bg-red-500/10 p-4 text-red-500">
        <ShieldAlert size={48} />
      </div>
      <h1 className="text-3xl font-extrabold tracking-tight">403 Forbidden Access</h1>
      <p className="max-w-md text-sm text-muted-foreground">You do not have the security clearance or privileges required to access this resource.</p>
      <Link to="/books" className="rounded-lg bg-primary px-4 py-2 text-sm font-semibold text-white hover:bg-primary/95">
        Return to Safety
      </Link>
    </div>
  )
}

export default Unauthorized
