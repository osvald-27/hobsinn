import React, { useState } from 'react';

function App() {
  const [activeTab, setActiveTab] = useState('Overview');

  const menuItems = ['Overview', 'User Management', 'Campaigns', 'Gamification Metrics', 'Settings'];

  return (
    <div style={{ display: 'flex', height: '100vh', fontFamily: 'sans-serif' }}>
      {/* Sidebar */}
      <div style={{ width: '250px', backgroundColor: '#182a18', color: 'white', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '20px', fontSize: '20px', fontWeight: 'bold', borderBottom: '1px solid #2d452d' }}>
          HOBSINN Admin
        </div>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {menuItems.map(item => (
            <li
              key={item}
              onClick={() => setActiveTab(item)}
              style={{
                padding: '15px 20px',
                cursor: 'pointer',
                backgroundColor: activeTab === item ? '#1DB954' : 'transparent',
                transition: 'background 0.2s'
              }}
            >
              {item}
            </li>
          ))}
        </ul>
      </div>

      {/* Main Content */}
      <div style={{ flex: 1, backgroundColor: '#F4F6F4', padding: '30px', overflowY: 'auto' }}>
        <h1 style={{ margin: '0 0 20px 0', color: '#182a18' }}>{activeTab}</h1>
        
        {activeTab === 'Overview' && (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
            <MetricCard title="Total Users" value="1,245" change="+12%" />
            <MetricCard title="Active Campaigns" value="4" change="+1" />
            <MetricCard title="Total Waste Collected" value="4,520 kg" change="+540 kg" />
          </div>
        )}

        {/* Placeholder for other tabs */}
        {activeTab !== 'Overview' && (
          <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '8px', border: '1px solid #ddeadd' }}>
            <p>Module {activeTab} loaded. Waiting for backend API integration.</p>
          </div>
        )}
      </div>
    </div>
  );
}

function MetricCard({ title, value, change }: { title: string, value: string, change: string }) {
  return (
    <div style={{ backgroundColor: 'white', padding: '20px', borderRadius: '8px', border: '1px solid #ddeadd' }}>
      <div style={{ color: '#6b7c6b', fontSize: '14px', marginBottom: '10px' }}>{title}</div>
      <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#182a18' }}>{value}</div>
      <div style={{ color: '#1DB954', fontSize: '13px', marginTop: '5px', fontWeight: 'bold' }}>{change} this week</div>
    </div>
  );
}

export default App;
