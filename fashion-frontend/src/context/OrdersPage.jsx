import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from './AuthContext'
import api from '../api/axios'
import { getShipmentByOrder } from '../api/shipments'

const STATUS_STEPS = ['PROCESSING', 'SHIPPED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED']

export default function OrdersPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [orders, setOrders] = useState([])
  const [shipments, setShipments] = useState({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user) { navigate('/login'); return }
    api.get(`/orders/${user.userId}`).then(async res => {
      setOrders(res.data)
      // Fetch shipment for each order
      const shipmentMap = {}
      await Promise.all(res.data.map(async order => {
        try {
          const s = await getShipmentByOrder(order.id)
          shipmentMap[order.id] = s.data
        } catch (_) {}
      }))
      setShipments(shipmentMap)
    }).finally(() => setLoading(false))
  }, [user])

  if (loading) return <div className="text-center py-20 text-gray-400">Loading orders...</div>

  if (orders.length === 0) return (
    <div className="text-center py-20">
      <p className="text-5xl mb-4">📦</p>
      <p className="text-gray-500 text-lg mb-4">No orders yet</p>
      <button onClick={() => navigate('/')}
        className="bg-black text-white px-6 py-2 rounded-lg hover:bg-gray-800 transition">
        Start Shopping
      </button>
    </div>
  )

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-bold mb-6">My Orders</h2>
      <div className="space-y-6">
        {orders.map(order => {
          const shipment = shipments[order.id]
          const stepIndex = shipment ? STATUS_STEPS.indexOf(shipment.status) : 0
          return (
            <div key={order.id} className="bg-white rounded-xl shadow p-6">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <p className="font-semibold text-lg">Order #{order.id}</p>
                  <p className="text-gray-500 text-sm">{order.createdAt?.split('T')[0]}</p>
                  <p className="text-gray-500 text-sm">Payment: {order.paymentMethod}</p>
                </div>
                <div className="text-right">
                  <span className="text-xs bg-gray-100 text-gray-600 px-3 py-1 rounded-full font-medium">
                    {order.status}
                  </span>
                  <p className="font-bold text-xl mt-2">${order.totalAmount?.toFixed(2)}</p>
                </div>
              </div>

              {shipment && (
                <div className="border-t pt-4">
                  <div className="flex justify-between text-sm text-gray-500 mb-3">
                    <span>🚚 {shipment.carrier} · {shipment.trackingNumber}</span>
                    <span>📍 {shipment.currentLocation}</span>
                  </div>
                  <div className="text-xs text-gray-400 mb-3">
                    Est. delivery: {shipment.estimatedDelivery}
                  </div>
                  {/* Progress bar */}
                  <div className="flex items-center gap-1">
                    {STATUS_STEPS.map((step, i) => (
                      <div key={step} className="flex-1 flex flex-col items-center">
                        <div className={`w-4 h-4 rounded-full border-2 mb-1 ${
                          i <= stepIndex
                            ? 'bg-black border-black'
                            : 'bg-white border-gray-300'
                        }`} />
                        <span className={`text-xs text-center leading-tight ${
                          i <= stepIndex ? 'text-black font-medium' : 'text-gray-400'
                        }`}>{step.replace('_', ' ')}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}