import React, { useEffect, useMemo, useState } from 'react'

type UserRole = 'CUSTOMER' | 'PROVIDER' | 'ADMIN' | 'HOUSEHOLD' | 'PICKUP' | 'AMBASSADOR' | 'ADMINISTRATOR'

type AccountStatus = 'ACTIVE' | 'SUSPENDED' | 'DEACTIVATED'

interface User {
  id: number
  name: string
  email: string
  phone: string
  role: UserRole
  accountStatus: AccountStatus
  createdAt: string
  updatedAt: string
  ecoPoints: number
  kgCollected: number
  badges: string[]
}

interface UserSummary {
  totalUsers: number
  adminCount: number
  providerCount: number
  customerCount: number
  ambassadorCount: number
  activeAccounts: number
  suspendedAccounts: number
  deactivatedAccounts: number
}

const roles: UserRole[] = ['ADMIN', 'ADMINISTRATOR', 'AMBASSADOR', 'PROVIDER', 'PICKUP', 'HOUSEHOLD', 'CUSTOMER']
const statuses: AccountStatus[] = ['ACTIVE', 'SUSPENDED', 'DEACTIVATED']

function App() {
  const [activeTab, setActiveTab] = useState('Overview')
  const [users, setUsers] = useState<User[]>([])
  const [summary, setSummary] = useState<UserSummary | null>(null)
  const [selectedUser, setSelectedUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState<string>('')
  const [statusFilter, setStatusFilter] = useState<string>('')
  const [createPayload, setCreatePayload] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    role: 'ADMIN' as UserRole,
    accountStatus: 'ACTIVE' as AccountStatus,
  })
  const [endpointResult, setEndpointResult] = useState<string>('')

  const menuItems = ['Overview', 'User Management', 'Endpoint Explorer', 'Settings']
  const backendUrl = '/api/users'

  useEffect(() => {
    fetchDashboardData()
  }, [])

  async function fetchDashboardData() {
    setLoading(true)
    setError(null)
    try {
      const [userResponse, summaryResponse] = await Promise.all([
        fetch(buildUserUrl()),
        fetch(`${backendUrl}/summary`),
      ])
      if (!userResponse.ok || !summaryResponse.ok) {
        throw new Error('Unable to load admin dashboard data')
      }
      const usersData = await userResponse.json()
      const summaryData = await summaryResponse.json()
      setUsers(usersData)
      setSummary(summaryData)
    } catch (err) {
      setError((err as Error).message)
    } finally {
      setLoading(false)
    }
  }

  function buildUserUrl() {
    const url = new URL(backendUrl, window.location.origin)
    if (search.trim()) url.searchParams.append('query', search.trim())
    if (roleFilter) url.searchParams.append('role', roleFilter)
    if (statusFilter) url.searchParams.append('status', statusFilter)
    return url.toString()
  }

  async function handleCreateUser() {
    setError(null)
    if (!createPayload.name || !createPayload.email || !createPayload.phone || !createPayload.password) {
      setError('All create fields are required.')
      return
    }
    try {
      const response = await fetch(backendUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(createPayload),
      })
      if (!response.ok) {
        throw new Error('Failed to create user')
      }
      await fetchDashboardData()
      setCreatePayload({ name: '', email: '', phone: '', password: '', role: 'ADMIN', accountStatus: 'ACTIVE' })
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function handleUpdateUser(update: Partial<User>) {
    if (!selectedUser) return
    setError(null)
    try {
      const response = await fetch(`${backendUrl}/${selectedUser.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: update.name,
          email: update.email,
          phone: update.phone,
          role: update.role,
          accountStatus: update.accountStatus,
        }),
      })
      if (!response.ok) throw new Error('Unable to update user')
      const updated = await response.json()
      setSelectedUser(updated)
      await fetchDashboardData()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function handleDeleteUser(id: number) {
    if (!window.confirm('Delete this user account?')) return
    setError(null)
    try {
      const response = await fetch(`${backendUrl}/${id}`, { method: 'DELETE' })
      if (!response.ok) throw new Error('Failed to delete user')
      if (selectedUser?.id === id) setSelectedUser(null)
      await fetchDashboardData()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function runEndpoint(path: string, method = 'GET') {
    setEndpointResult('Loading...')
    try {
      const response = await fetch(path, { method })
      const payload = await response.text()
      setEndpointResult(`Status ${response.status}\n${payload}`)
    } catch (err) {
      setEndpointResult(`Error: ${(err as Error).message}`)
    }
  }

  const userCountByRole = useMemo(() => {
    return roles.reduce<Record<UserRole, number>>((acc, role) => {
      acc[role] = users.filter(user => user.role === role).length
      return acc
    }, {} as Record<UserRole, number>)
  }, [users])

  return (
    <div style={{ display: 'flex', minHeight: '100vh', fontFamily: 'Inter, sans-serif' }}>
      <aside style={{ width: '260px', backgroundColor: '#122b17', color: '#fff', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '24px 20px', fontSize: '22px', fontWeight: 700, borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
          HOBSINN Admin
        </div>
        <nav style={{ flex: 1, padding: '18px 0' }}>
          {menuItems.map(item => (
            <button
              key={item}
              onClick={() => setActiveTab(item)}
              style={{
                width: '100%',
                textAlign: 'left',
                padding: '14px 24px',
                border: 'none',
                background: activeTab === item ? '#1cb85c' : 'transparent',
                color: '#fff',
                cursor: 'pointer',
                fontSize: '15px',
                transition: 'background 0.2s ease',
              }}
            >
              {item}
            </button>
          ))}
        </nav>
        <div style={{ padding: '20px', fontSize: '13px', color: '#c8ddc1', borderTop: '1px solid rgba(255,255,255,0.08)' }}>
          Admin features include role creation, account status control, and endpoint access.
        </div>
      </aside>

      <main style={{ flex: 1, backgroundColor: '#eef5ee', padding: '32px', overflowY: 'auto' }}>
        <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '28px' }}>
          <div>
            <p style={{ margin: 0, color: '#5f7d61', textTransform: 'uppercase', fontSize: '13px', letterSpacing: '0.12em' }}>Admin Dashboard</p>
            <h1 style={{ margin: '10px 0 0', color: '#1d3321' }}>{activeTab}</h1>
          </div>
          <div style={{ color: '#4f6b56' }}>
            {loading ? 'Refreshing...' : 'Ready'}
          </div>
        </header>

        {error && (
          <div style={{ marginBottom: '20px', padding: '16px', background: '#ffe9e9', border: '1px solid #f6c2c2', color: '#7a1d1d', borderRadius: '10px' }}>
            {error}
          </div>
        )}

        {activeTab === 'Overview' && (
          <>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: '20px', marginBottom: '22px' }}>
              <MetricCard label="Total Users" value={summary?.totalUsers.toString() ?? '--'} />
              <MetricCard label="Admins" value={summary?.adminCount.toString() ?? '--'} />
              <MetricCard label="Active Accounts" value={summary?.activeAccounts.toString() ?? '--'} />
              <MetricCard label="Suspended Accounts" value={summary?.suspendedAccounts.toString() ?? '--'} />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
              <div style={{ padding: '24px', background: '#fff', borderRadius: '16px', boxShadow: '0 1px 4px rgba(15, 23, 17, 0.08)' }}>
                <h2 style={{ marginTop: 0, color: '#1d3321' }}>What Admin Can Do</h2>
                <ul style={{ paddingLeft: '18px', color: '#4d624f', lineHeight: 1.8 }}>
                  <li>Create any user type, including CUSTOMER, PROVIDER, AMBASSADOR, ADMINISTRATOR.</li>
                  <li>Search and filter accounts by role, status or keyword.</li>
                  <li>Edit account details, change user role, and suspend or deactivate users.</li>
                  <li>Delete accounts, review endpoint access and trigger admin APIs.</li>
                </ul>
              </div>
              <div style={{ padding: '24px', background: '#fff', borderRadius: '16px', boxShadow: '0 1px 4px rgba(15, 23, 17, 0.08)' }}>
                <h2 style={{ marginTop: 0, color: '#1d3321' }}>Role Summary</h2>
                {roles.map(role => (
                  <div key={role} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                    <span style={{ color: '#4d624f' }}>{role}</span>
                    <strong style={{ color: '#1d3321' }}>{userCountByRole[role]}</strong>
                  </div>
                ))}
              </div>
            </div>
          </>
        )}

        {activeTab === 'User Management' && (
          <div style={{ display: 'grid', gridTemplateColumns: '2.2fr 1fr', gap: '24px' }}>
            <section style={{ padding: '24px', background: '#fff', borderRadius: '16px', border: '1px solid #e3ebdf' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
                <div>
                  <h2 style={{ margin: 0, color: '#1d3321' }}>Manage Users</h2>
                  <p style={{ margin: '8px 0 0', color: '#5f7d61' }}>Create, update, and maintain account security from one place.</p>
                </div>
                <button onClick={fetchDashboardData} style={buttonStyle}>Refresh</button>
              </div>

              <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginBottom: '18px' }}>
                <input placeholder="Search users" value={search} onChange={e => setSearch(e.target.value)} style={inputStyle} />
                <select value={roleFilter} onChange={e => setRoleFilter(e.target.value)} style={inputStyle}>
                  <option value="">All roles</option>
                  {roles.map(role => <option key={role} value={role}>{role}</option>)}
                </select>
                <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)} style={inputStyle}>
                  <option value="">All statuses</option>
                  {statuses.map(status => <option key={status} value={status}>{status}</option>)}
                </select>
                <button onClick={fetchDashboardData} style={buttonStyle}>Apply</button>
              </div>

              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', minWidth: '920px' }}>
                  <thead>
                    <tr style={{ textAlign: 'left', color: '#5f7d61' }}>
                      {['Name', 'Email', 'Phone', 'Role', 'Status', 'EcoPoints', 'KG Collected', 'Actions'].map(title => (
                        <th key={title} style={{ padding: '14px 12px', borderBottom: '2px solid #e7efe6' }}>{title}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {users.map(user => (
                      <tr key={user.id} style={{ background: selectedUser?.id === user.id ? '#f4f9f1' : 'transparent' }}>
                        <td style={tableCellStyle}>{user.name}</td>
                        <td style={tableCellStyle}>{user.email}</td>
                        <td style={tableCellStyle}>{user.phone}</td>
                        <td style={tableCellStyle}>{user.role}</td>
                        <td style={tableCellStyle}>{user.accountStatus}</td>
                        <td style={tableCellStyle}>{user.ecoPoints}</td>
                        <td style={tableCellStyle}>{user.kgCollected.toFixed(1)}</td>
                        <td style={tableCellStyle}>
                          <button onClick={() => setSelectedUser(user)} style={actionButton}>Edit</button>
                          <button onClick={() => handleDeleteUser(user.id)} style={{ ...actionButton, background: '#d64545' }}>Delete</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            <section style={{ padding: '24px', background: '#fff', borderRadius: '16px', border: '1px solid #e3ebdf' }}>
              <div style={{ marginBottom: '18px' }}>
                <h2 style={{ margin: 0, color: '#1d3321' }}>Create New User</h2>
                <p style={{ margin: '8px 0 0', color: '#5f7d61' }}>Admin can create any type of account with role and status control.</p>
              </div>
              <div style={{ display: 'grid', gap: '12px' }}>
                <input value={createPayload.name} placeholder="Full name" onChange={e => setCreatePayload({ ...createPayload, name: e.target.value })} style={inputStyle} />
                <input value={createPayload.email} placeholder="Email address" onChange={e => setCreatePayload({ ...createPayload, email: e.target.value })} style={inputStyle} />
                <input value={createPayload.phone} placeholder="Phone number" onChange={e => setCreatePayload({ ...createPayload, phone: e.target.value })} style={inputStyle} />
                <input type="password" value={createPayload.password} placeholder="Password" onChange={e => setCreatePayload({ ...createPayload, password: e.target.value })} style={inputStyle} />
                <select value={createPayload.role} onChange={e => setCreatePayload({ ...createPayload, role: e.target.value as UserRole })} style={inputStyle}>
                  {roles.map(role => <option key={role} value={role}>{role}</option>)}
                </select>
                <select value={createPayload.accountStatus} onChange={e => setCreatePayload({ ...createPayload, accountStatus: e.target.value as AccountStatus })} style={inputStyle}>
                  {statuses.map(status => <option key={status} value={status}>{status}</option>)}
                </select>
                <button onClick={handleCreateUser} style={{ ...buttonStyle, width: '100%' }}>Create User</button>
              </div>

              {selectedUser && (
                <div style={{ marginTop: '28px', padding: '18px', background: '#f9fdf8', borderRadius: '14px', border: '1px solid #dfe7d8' }}>
                  <h3 style={{ margin: '0 0 12px', color: '#1d3321' }}>Edit Selected User</h3>
                  <label style={labelStyle}>Name</label>
                  <input value={selectedUser.name} onChange={e => setSelectedUser({ ...selectedUser, name: e.target.value })} style={inputStyle} />
                  <label style={labelStyle}>Email</label>
                  <input value={selectedUser.email} onChange={e => setSelectedUser({ ...selectedUser, email: e.target.value })} style={inputStyle} />
                  <label style={labelStyle}>Phone</label>
                  <input value={selectedUser.phone} onChange={e => setSelectedUser({ ...selectedUser, phone: e.target.value })} style={inputStyle} />
                  <label style={labelStyle}>Role</label>
                  <select value={selectedUser.role} onChange={e => setSelectedUser({ ...selectedUser, role: e.target.value as UserRole })} style={inputStyle}>
                    {roles.map(role => <option key={role} value={role}>{role}</option>)}
                  </select>
                  <label style={labelStyle}>Status</label>
                  <select value={selectedUser.accountStatus} onChange={e => setSelectedUser({ ...selectedUser, accountStatus: e.target.value as AccountStatus })} style={inputStyle}>
                    {statuses.map(status => <option key={status} value={status}>{status}</option>)}
                  </select>
                  <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginTop: '16px' }}>
                    <button onClick={() => selectedUser && handleUpdateUser(selectedUser)} style={buttonStyle}>Save changes</button>
                    <button onClick={() => setSelectedUser(null)} style={{ ...buttonStyle, background: '#999', color: '#fff' }}>Cancel</button>
                  </div>
                </div>
              )}
            </section>
          </div>
        )}

        {activeTab === 'Endpoint Explorer' && (
          <section style={{ display: 'grid', gridTemplateColumns: '1fr 0.9fr', gap: '24px' }}>
            <div style={{ padding: '24px', background: '#fff', borderRadius: '16px', border: '1px solid #e3ebdf' }}>
              <h2 style={{ marginTop: 0, color: '#1d3321' }}>Admin API Endpoints</h2>
              <p style={{ color: '#5f7d61' }}>Run the most important admin endpoints directly from the dashboard.</p>
              {[
                { label: 'List All Users', path: '/api/users', method: 'GET' },
                { label: 'Load User Summary', path: '/api/users/summary', method: 'GET' },
                { label: 'List Roles', path: '/api/users/roles', method: 'GET' },
                { label: 'List Status Types', path: '/api/users/statuses', method: 'GET' },
              ].map(endpoint => (
                <div key={endpoint.path} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #eef4ec', padding: '14px 0' }}>
                  <div>
                    <div style={{ fontWeight: 600, color: '#1d3321' }}>{endpoint.label}</div>
                    <div style={{ fontSize: '13px', color: '#5f7d61' }}>{endpoint.method} {endpoint.path}</div>
                  </div>
                  <button onClick={() => runEndpoint(endpoint.path, endpoint.method)} style={buttonStyle}>Run</button>
                </div>
              ))}
            </div>
            <div style={{ padding: '24px', background: '#fff', borderRadius: '16px', border: '1px solid #e3ebdf', minHeight: '320px' }}>
              <h2 style={{ marginTop: 0, color: '#1d3321' }}>Endpoint Result</h2>
              <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word', color: '#3d523f' }}>{endpointResult || 'Run an endpoint to see its response here.'}</pre>
            </div>
          </section>
        )}

        {activeTab === 'Settings' && (
          <section style={{ padding: '24px', background: '#fff', borderRadius: '16px', border: '1px solid #e3ebdf' }}>
            <h2 style={{ marginTop: 0, color: '#1d3321' }}>Admin Settings</h2>
            <p style={{ color: '#5f7d61' }}>Use this section to keep your admin dashboard and account policy aligned with your platform.</p>
            <div style={{ display: 'grid', gap: '16px', marginTop: '18px' }}>
              <div style={panelRowStyle}>
                <span>Default user creation role</span>
                <strong>ADMIN</strong>
              </div>
              <div style={panelRowStyle}>
                <span>Default account status</span>
                <strong>ACTIVE</strong>
              </div>
              <div style={panelRowStyle}>
                <span>Accessible admin endpoints</span>
                <strong>{['/api/users', '/api/users/summary', '/api/users/roles', '/api/users/statuses'].length}</strong>
              </div>
            </div>
          </section>
        )}
      </main>
    </div>
  )
}

function MetricCard({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ padding: '22px', background: '#fff', borderRadius: '18px', border: '1px solid #e7efe6', boxShadow: '0 1px 6px rgba(22, 57, 26, 0.06)' }}>
      <div style={{ color: '#5f7d61', fontSize: '14px', marginBottom: '10px' }}>{label}</div>
      <div style={{ fontSize: '32px', fontWeight: 700, color: '#182a18' }}>{value}</div>
    </div>
  )
}

const buttonStyle: React.CSSProperties = {
  border: 'none',
  borderRadius: '10px',
  padding: '12px 18px',
  background: '#1cb85c',
  color: '#fff',
  fontWeight: 600,
  cursor: 'pointer',
}

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '12px 14px',
  borderRadius: '12px',
  border: '1px solid #d6e1d3',
  background: '#fbfdf9',
  color: '#1c3220',
}

const tableCellStyle: React.CSSProperties = {
  padding: '14px 12px',
  borderBottom: '1px solid #f1f6ef',
  color: '#3b4f3d',
}

const actionButton: React.CSSProperties = {
  border: 'none',
  borderRadius: '10px',
  padding: '8px 12px',
  background: '#488e3f',
  color: '#fff',
  cursor: 'pointer',
  marginRight: '8px',
}

const labelStyle: React.CSSProperties = {
  fontSize: '13px',
  color: '#5f7d61',
  margin: '12px 0 6px',
  display: 'block',
}

const panelRowStyle: React.CSSProperties = {
  display: 'flex',
  justifyContent: 'space-between',
  padding: '16px 18px',
  borderRadius: '14px',
  background: '#f5fbf5',
  color: '#405643',
}

export default App;

