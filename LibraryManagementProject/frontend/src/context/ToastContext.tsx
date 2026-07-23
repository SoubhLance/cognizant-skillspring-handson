import React, {
  createContext,
  useContext,
  useReducer,
  useCallback,
  useRef,
  useEffect,
} from 'react'

// ─── Types ────────────────────────────────────────────────────────────────────
export type ToastType = 'success' | 'error' | 'warning' | 'info'

export interface Toast {
  id: string
  type: ToastType
  title: string
  message?: string
  duration?: number
}

type Action =
  | { type: 'ADD'; toast: Toast }
  | { type: 'REMOVE'; id: string }

function reducer(state: Toast[], action: Action): Toast[] {
  switch (action.type) {
    case 'ADD':
      return [action.toast, ...state].slice(0, 5) // cap at 5 visible toasts
    case 'REMOVE':
      return state.filter((t) => t.id !== action.id)
    default:
      return state
  }
}

// ─── Context ──────────────────────────────────────────────────────────────────
interface ToastContextType {
  toasts: Toast[]
  showToast: (type: ToastType, title: string, message?: string, duration?: number) => void
  dismissToast: (id: string) => void
}

const ToastContext = createContext<ToastContextType | undefined>(undefined)

/**
 * Module-level ref that lets the Axios interceptor (outside React tree)
 * dispatch toasts without needing the context directly.
 */
export let globalShowToast: ToastContextType['showToast'] | null = null

// ─── Provider ─────────────────────────────────────────────────────────────────
export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, dispatch] = useReducer(reducer, [])

  const showToast = useCallback(
    (type: ToastType, title: string, message?: string, duration = 4000) => {
      const id = crypto.randomUUID()
      dispatch({ type: 'ADD', toast: { id, type, title, message, duration } })

      if (duration > 0) {
        setTimeout(() => dispatch({ type: 'REMOVE', id }), duration)
      }
    },
    []
  )

  const dismissToast = useCallback((id: string) => {
    dispatch({ type: 'REMOVE', id })
  }, [])

  // Expose globally for Axios interceptor
  const showToastRef = useRef(showToast)
  showToastRef.current = showToast
  useEffect(() => {
    globalShowToast = (...args) => showToastRef.current(...args)
    return () => { globalShowToast = null }
  }, [])

  return (
    <ToastContext.Provider value={{ toasts, showToast, dismissToast }}>
      {children}
    </ToastContext.Provider>
  )
}

// ─── Hook ─────────────────────────────────────────────────────────────────────
export const useToast = () => {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used inside <ToastProvider>')
  return ctx
}
