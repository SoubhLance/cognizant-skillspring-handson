import React, { createContext, useState, useEffect, useContext } from 'react'
import API from '../services/api'
import { LoginRequest, RegisterRequest, JwtResponse, UserDto } from '../types' // We will create types.ts next

interface AuthContextType {
  user: UserDto | null
  token: string | null
  role: string | null
  permissions: string[]
  loading: boolean
  login: (credentials: LoginRequest) => Promise<void>
  register: (fields: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserDto | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [role, setRole] = useState<string | null>(null)
  const [permissions, setPermissions] = useState<string[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('token')
      const storedUser = localStorage.getItem('user')
      
      if (storedToken && storedUser) {
        setToken(storedToken)
        const parsedUser = JSON.parse(storedUser)
        setUser(parsedUser)
        setRole(parsedUser.role)
        setPermissions(parsedUser.permissions || [])
        
        // Optionally verify active session with backend /me endpoint
        try {
          const res = await API.get('/users/me')
          const freshUser = res.data.data
          setUser(freshUser)
          setRole(freshUser.role)
          localStorage.setItem('user', JSON.stringify({ ...parsedUser, ...freshUser }))
        } catch (err) {
          console.error("Session verification failed", err)
          // If expired, Axios interceptor will handle or we clear
        }
      }
      setLoading(false)
    }
    
    initializeAuth()
  }, [])

  const login = async (credentials: LoginRequest) => {
    setLoading(true)
    try {
      const res = await API.post('/auth/login', credentials)
      const data: JwtResponse = res.data.data
      
      localStorage.setItem('token', data.token)
      localStorage.setItem('refreshToken', data.refreshToken)
      
      const userProfile: UserDto = {
        id: data.id,
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        role: data.role,
        permissions: data.permissions,
        phone: '',
        status: 'ACTIVE',
        createdAt: ''
      }
      
      localStorage.setItem('user', JSON.stringify(userProfile))
      
      setUser(userProfile)
      setToken(data.token)
      setRole(data.role)
      setPermissions(data.permissions || [])
    } finally {
      setLoading(false)
    }
  }

  const register = async (fields: RegisterRequest) => {
    setLoading(true)
    try {
      await API.post('/auth/register', fields)
    } finally {
      setLoading(false)
    }
  }

  const logout = async () => {
    setLoading(true)
    try {
      await API.post('/auth/logout')
    } catch (err) {
      console.warn("Logout endpoint error", err)
    } finally {
      localStorage.clear()
      setUser(null)
      setToken(null)
      setRole(null)
      setPermissions([])
      setLoading(false)
    }
  }

  return (
    <AuthContext.Provider value={{ user, token, role, permissions, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
