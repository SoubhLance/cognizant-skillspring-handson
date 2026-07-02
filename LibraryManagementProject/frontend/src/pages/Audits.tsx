import React, { useState, useEffect } from 'react'
import API from '../services/api'
import { AuditLogDto } from '../types'
import { Shield } from 'lucide-react'

const Audits: React.FC = () => {
  const [logs, setLogs] = useState<AuditLogDto[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const res = await API.get('/admin/audit-logs')
        setLogs(res.data.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchLogs()
  }, [])

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-2">
        <Shield className="text-primary" size={28} />
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Security Audit Logs</h1>
          <p className="text-sm text-muted-foreground">Immutable logs detailing logins, settings changes, catalog operations, and database updates.</p>
        </div>
      </div>

      {loading ? (
        <div className="flex h-40 items-center justify-center">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
        </div>
      ) : (
        <div className="rounded-xl border bg-card shadow-sm overflow-x-auto">
          <table className="w-full border-collapse text-left text-sm text-muted-foreground">
            <thead>
              <tr className="border-b text-foreground font-semibold">
                <th className="py-3 px-6">ID</th>
                <th className="py-3 px-6">Timestamp</th>
                <th className="py-3 px-6">Operator</th>
                <th className="py-3 px-6">Action</th>
                <th className="py-3 px-6">Details</th>
                <th className="py-3 px-6">IP Address</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id} className="border-b hover:bg-muted/40 transition-colors">
                  <td className="py-3 px-6 font-mono text-xs">{log.id}</td>
                  <td className="py-3 px-6 text-xs">{new Date(log.createdAt).toLocaleString()}</td>
                  <td className="py-3 px-6 text-foreground font-semibold">{log.userName}</td>
                  <td className="py-3 px-6"><span className="text-xs font-mono font-bold uppercase text-primary bg-primary/5 px-2 py-0.5 rounded">{log.action}</span></td>
                  <td className="py-3 px-6 text-xs text-foreground">{log.details}</td>
                  <td className="py-3 px-6 text-xs">{log.ipAddress || '127.0.0.1'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default Audits
