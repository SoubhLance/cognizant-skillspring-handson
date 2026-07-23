import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AuthProvider } from './context/AuthContext'
import { ToastProvider } from './context/ToastContext'
import ToastContainer from './components/Toast'

// Components
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'

// Pages
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Books from './pages/Books'
import BookDetails from './pages/BookDetails'
import Categories from './pages/Categories'
import Authors from './pages/Authors'
import Publishers from './pages/Publishers'
import BookIssues from './pages/BookIssues'
import Reservations from './pages/Reservations'
import Fines from './pages/Fines'
import Wishlist from './pages/Wishlist'
import Notifications from './pages/Notifications'
import Users from './pages/Users'
import Audits from './pages/Audits'
import Unauthorized from './pages/Unauthorized'
import NotFound from './pages/NotFound'

const queryClient = new QueryClient()

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <ToastProvider>
        <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/unauthorized" element={<Unauthorized />} />

            {/* Protected Routes */}
            <Route element={<ProtectedRoute />}>
              <Route element={<Layout />}>
                {/* Redirect home to books */}
                <Route path="/" element={<Navigate to="/books" replace />} />
                <Route path="/books" element={<Books />} />
                <Route path="/books/:id" element={<BookDetails />} />
                <Route path="/reservations" element={<Reservations />} />
                <Route path="/issues" element={<BookIssues />} />
                <Route path="/fines" element={<Fines />} />
                <Route path="/wishlist" element={<Wishlist />} />
                <Route path="/notifications" element={<Notifications />} />

                {/* Librarian & Admin Routes */}
                <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_LIBRARIAN']} />}>
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/categories" element={<Categories />} />
                  <Route path="/authors" element={<Authors />} />
                  <Route path="/publishers" element={<Publishers />} />
                </Route>

                {/* Admin Only Routes */}
                <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
                  <Route path="/users" element={<Users />} />
                  <Route path="/audits" element={<Audits />} />
                </Route>
              </Route>
            </Route>

            {/* Fallback */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
        </AuthProvider>
        <ToastContainer />
      </ToastProvider>
    </QueryClientProvider>
  )
}

export default App
