import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from './AuthContext'
import { getAnalytics } from '../api/analytics'

export default function AdminDashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user || user.role !== 'ADMIN') { navigate('/'); return }
    getAnalytics().then(res => setData(res.data)).finally(() => setLoading(false))
  }, [user])

  if (loading) return <div className="text-center py-20 text-gray-400">Loading analytics...</div>
  if (!data) return null

  const statCards = [
    { label: 'Total Revenue', value: `$${data.totalRevenue?.toFixed(2)}`, icon: '💰' },
    { label: 'Total Orders', value: data.totalOrders, icon: '📦' },
    { label: 'Customers', value: data.totalCustomers, icon: '👥' },
    { label: 'Products', value: data.totalProducts, icon: '👗' },
  ]

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-bold mb-6">Admin Dashboard</h2>

      {/* Stat cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        {statCards.map(card => (
          <div key={card.label} className="bg-white rounded-xl shadow p-5 text-center">
            <div className="text-3xl mb-2">{card.icon}</div>
            <div className="text-2xl font-bold">{card.value}</div>
            <div className="text-gray-500 text-sm mt-1">{card.label}</div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Top products */}
        <div className="bg-white rounded-xl shadow p-6">
          <h3 className="font-semibold text-lg mb-4">Top Products</h3>
          <div className="space-y-3">
            {data.topProducts?.map((p, i) => (
              <div key={p.productName} className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <span className="w-6 h-6 rounded-full bg-gray-100 text-xs flex items-center justify-center font-bold text-gray-600">
                    {i + 1}
                  </span>
                  <span className="text-sm font-medium">{p.productName}</span>
                </div>
                <div className="text-right">
                  <div className="text-sm font-bold">${p.revenue?.toFixed(2)}</div>
                  <div className="text-xs text-gray-400">{p.totalSold} sold</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Revenue by category */}
        <div className="bg-white rounded-xl shadow p-6">
          <h3 className="font-semibold text-lg mb-4">Revenue by Category</h3>
          <div className="space-y-3">
            {data.revenueByCategory?.map(cat => (
              <div key={cat.category}>
                <div className="flex justify-between text-sm mb-1">
                  <span>{cat.category}</span>
                  <span className="font-semibold">${cat.revenue?.toFixed(2)}</span>
                </div>
                <div className="w-full bg-gray-100 rounded-full h-2">
                  <div className="bg-black h-2 rounded-full"
                    style={{ width: `${Math.min(100, (cat.revenue / data.totalRevenue) * 100)}%` }} />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Orders by status */}
        <div className="bg-white rounded-xl shadow p-6 md:col-span-2">
          <h3 className="font-semibold text-lg mb-4">Orders by Status</h3>
          <div className="flex flex-wrap gap-4">
            {data.ordersByStatus?.map(s => (
              <div key={s.status} className="flex-1 min-w-[120px] bg-gray-50 rounded-lg p-4 text-center">
                <div className="text-2xl font-bold">{s.count}</div>
                <div className="text-gray-500 text-sm mt-1">{s.status}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}