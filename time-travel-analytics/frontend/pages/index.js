import { useState } from 'react';

export default function Home() {
  const [table, setTable] = useState('inventory');
  const [ts, setTs] = useState(new Date().toISOString().slice(0,19));
  const [rows, setRows] = useState(null);
  const [from, setFrom] = useState(ts);
  const [to, setTo] = useState(ts);
  const [diff, setDiff] = useState(null);

  async function fetchAsOf() {
    const res = await fetch(`/api/proxy/api/asof?table=${table}&timestamp=${encodeURIComponent(ts)}`);
    const data = await res.json();
    setRows(data);
  }

  async function fetchDiff() {
    const res = await fetch(`/api/proxy/api/diff?table=${table}&from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`);
    const data = await res.json();
    setDiff(data);
  }

  return (
    <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
      <h1>Time-Travel Analytics</h1>

      <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 12 }}>
        <label>Table:
          <select value={table} onChange={e => setTable(e.target.value)} style={{ marginLeft: 8 }}>
            <option value="inventory">inventory</option>
            <option value="users">users</option>
            <option value="orders">orders</option>
          </select>
        </label>

        <label>Timestamp:
          <input value={ts} onChange={e => setTs(e.target.value)} style={{ marginLeft: 8 }} />
        </label>

        <button onClick={fetchAsOf}>Show As-Of</button>
      </div>

      <div>
        <h3>Snapshot</h3>
        <pre style={{ maxHeight: 300, overflow: 'auto', background: '#f7f7f7', padding: 12 }}>
          {rows ? JSON.stringify(rows, null, 2) : 'no data'}
        </pre>
      </div>

      <hr />

      <div style={{ marginTop: 12 }}>
        <h3>Diff between two times</h3>
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 8 }}>
          <input value={from} onChange={e => setFrom(e.target.value)} />
          <input value={to} onChange={e => setTo(e.target.value)} />
          <button onClick={fetchDiff}>Get Diff</button>
        </div>

        <pre style={{ maxHeight: 300, overflow: 'auto', background: '#f7f7f7', padding: 12 }}>
          {diff ? JSON.stringify(diff, null, 2) : 'no diff'}
        </pre>
      </div>
    </div>
  );
}

