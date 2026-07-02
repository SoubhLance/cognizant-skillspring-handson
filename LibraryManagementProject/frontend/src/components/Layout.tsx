import React, { useState, useEffect } from 'react'
import { Link, useNavigate, useLocation, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { 
  BookOpen, LayoutDashboard, Users, BookMarked, 
  Layers, UserSquare, Building2, Receipt, 
  History, Heart, Bell, Shield, LogOut, 
  Sun, Moon, Menu, X, User 
} from 'lucide-react'

const Layout: React.FC = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [darkMode, setDarkMode] = useState(
    localStorage.getItem('theme') === 'dark'
  )
  const [sidebarOpen, setSidebarOpen] = useState(true)

  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark')
      localStorage.setItem('theme', 'dark')
    } else {
      document.documentElement.classList.remove('dark')
      localStorage.setItem('theme', 'light')
    }
  }, [darkMode])

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  // Define sidebar navigation items based on Role
  const navItems = []

  if (user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_LIBRARIAN') {
    navItems.push(
      { label: 'Dashboard', path: '/dashboard', icon: <LayoutDashboard size={20} /> },
      { label: 'Books Catalog', path: '/books', icon: <BookOpen size={20} /> },
      { label: 'Categories', path: '/categories', icon: <Layers size={20} /> },
      { label: 'Authors', path: '/authors', icon: <UserSquare size={20} /> },
      { label: 'Publishers', path: '/publishers', icon: <Building2 size={20} /> },
      { label: 'Book Loans', path: '/issues', icon: <BookMarked size={20} /> },
      { label: 'Reservations', path: '/reservations', icon: <History size={20} /> },
      { label: 'Fines & Payments', path: '/fines', icon: <Receipt size={20} /> }
    )

    if (user?.role === 'ROLE_ADMIN') {
      navItems.push(
        { label: 'User Profiles', path: '/users', icon: <Users size={20} /> },
        { label: 'Security Audits', path: '/audits', icon: <Shield size={20} /> }
      )
    }
  } else {
    // STUDENT or FACULTY
    navItems.push(
      { label: 'Books Catalog', path: '/books', icon: <BookOpen size={20} /> },
      { label: 'My Holds', path: '/reservations', icon: <History size={20} /> },
      { label: 'My Loans', path: '/issues', icon: <BookMarked size={20} /> },
      { label: 'My Fines', path: '/fines', icon: <Receipt size={20} /> },
      { label: 'My Wishlist', path: '/wishlist', icon: <Heart size={20} /> },
      { label: 'Notifications', path: '/notifications', icon: <Bell size={20} /> }
    )
  }

  return (
    <div className="flex h-screen overflow-hidden bg-background text-foreground">
      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r bg-card transition-transform duration-300 md:static md:translate-x-0 ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      }`}>
        <div className="flex h-16 items-center justify-between border-b px-6">
          <Link to="/" className="flex items-center gap-2 font-bold text-primary">
            <BookOpen size={24} />
            <span className="text-xl tracking-wide">LMS Enterprise</span>
          </Link>
          <button className="md:hidden" onClick={() => setSidebarOpen(false)}>
            <X size={20} />
          </button>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto p-4">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium transition-colors ${
                location.pathname === item.path
                  ? 'bg-primary text-primary-foreground'
                  : 'text-muted-foreground hover:bg-secondary hover:text-foreground'
              }`}
            >
              {item.icon}
              {item.label}
            </Link>
          ))}
        </nav>

        <div className="border-t p-4">
          <button
            onClick={handleLogout}
            className="flex w-full items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium text-destructive hover:bg-destructive/10"
          >
            <LogOut size={20} />
            Sign Out
          </button>
        </div>
      </div>

      {/* Main Panel */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Navbar */}
        <header className="flex h-16 items-center justify-between border-b bg-card px-6">
          <button className="text-muted-foreground hover:text-foreground" onClick={() => setSidebarOpen(!sidebarOpen)}>
            <Menu size={24} />
          </button>

          <div className="flex items-center gap-4">
            {/* Theme Toggle */}
            <button
              onClick={() => setDarkMode(!darkMode)}
              className="rounded-lg p-2 text-muted-foreground hover:bg-secondary hover:text-foreground"
            >
              {darkMode ? <Sun size={20} /> : <Moon size={20} />}
            </button>

            {/* Profile Dropdown Indicator */}
            <div className="flex items-center gap-3 border-l pl-4">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/10 text-primary">
                <User size={18} />
              </div>
              <div className="hidden text-left md:block">
                <p className="text-xs font-semibold">{user?.firstName} {user?.lastName}</p>
                <p className="text-[10px] text-muted-foreground uppercase">{user?.role?.replace('ROLE_', '')}</p>
              </div>
            </div>
          </div>
        </header>

        {/* Content Outlet */}
        <main className="flex-1 overflow-y-auto p-6 md:p-8">
          <div className="mx-auto max-w-7xl">
            <Outlet />
          </div>
        </main>

        {/* Footer */}
        <footer className="border-t bg-card py-4 text-center text-xs text-muted-foreground">
          &copy; {new Date().getFullYear()} Enterprise Library Management System. All rights reserved.
        </footer>
      </div>
    </div>
  )
}

export default Layout
