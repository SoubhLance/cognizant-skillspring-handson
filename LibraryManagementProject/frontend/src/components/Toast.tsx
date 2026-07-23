import React from 'react'
import { CheckCircle2, XCircle, AlertTriangle, Info, X } from 'lucide-react'
import { useToast } from '../context/ToastContext'
import type { ToastType } from '../context/ToastContext'

// ─── Icon + colour map ────────────────────────────────────────────────────────
const TOAST_STYLES: Record<
  ToastType,
  { icon: React.ReactNode; classes: string }
> = {
  success: {
    icon: <CheckCircle2 size={18} className="shrink-0 text-emerald-400" />,
    classes: 'border-emerald-500/30 bg-emerald-950/80',
  },
  error: {
    icon: <XCircle size={18} className="shrink-0 text-red-400" />,
    classes: 'border-red-500/30 bg-red-950/80',
  },
  warning: {
    icon: <AlertTriangle size={18} className="shrink-0 text-amber-400" />,
    classes: 'border-amber-500/30 bg-amber-950/80',
  },
  info: {
    icon: <Info size={18} className="shrink-0 text-sky-400" />,
    classes: 'border-sky-500/30 bg-sky-950/80',
  },
}

// ─── ToastContainer ────────────────────────────────────────────────────────────
/**
 * Mount this once, at the root of your app (inside ToastProvider).
 * It renders all active toasts stacked in the bottom-right corner.
 */
const ToastContainer: React.FC = () => {
  const { toasts, dismissToast } = useToast()

  if (toasts.length === 0) return null

  return (
    <div
      aria-live="polite"
      aria-label="Notifications"
      className="fixed bottom-6 right-6 z-[9999] flex flex-col gap-3"
      style={{ maxWidth: '360px' }}
    >
      {toasts.map((toast) => {
        const { icon, classes } = TOAST_STYLES[toast.type]
        return (
          <div
            key={toast.id}
            role="alert"
            className={`flex items-start gap-3 rounded-xl border px-4 py-3 shadow-xl backdrop-blur-sm
              text-sm text-foreground transition-all duration-300 animate-in fade-in slide-in-from-right-4
              ${classes}`}
          >
            {icon}
            <div className="flex-1 min-w-0">
              <p className="font-semibold leading-tight">{toast.title}</p>
              {toast.message && (
                <p className="mt-0.5 text-xs text-muted-foreground leading-snug">
                  {toast.message}
                </p>
              )}
            </div>
            <button
              onClick={() => dismissToast(toast.id)}
              aria-label="Dismiss notification"
              className="shrink-0 text-muted-foreground hover:text-foreground transition-colors"
            >
              <X size={15} />
            </button>
          </div>
        )
      })}
    </div>
  )
}

export default ToastContainer
