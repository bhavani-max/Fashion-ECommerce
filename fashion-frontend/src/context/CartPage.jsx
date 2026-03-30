import { useCart } from './CartContext'
import { useNavigate } from 'react-router-dom'

export default function CartPage() {
  const { cartItems, removeItem, cartTotal } = useCart()
  const navigate = useNavigate()

  if (cartItems.length === 0) return (
    <div className="text-center py-20">
      <p className="text-5xl mb-4">🛒</p>
      <p className="text-gray-500 text-lg mb-4">Your cart is empty</p>
      <button
        onClick={() => navigate('/')}
        className="bg-black text-white px-6 py-2 rounded-lg hover:bg-gray-800 transition"
      >
        Continue Shopping
      </button>
    </div>
  )

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-bold mb-6">Your Cart ({cartItems.length} items)</h2>
      <div className="space-y-4">
        {cartItems.map(item => (
          <div key={item.id} className="flex items-center gap-4 bg-white p-4 rounded-xl shadow">
            <img
              src={item.productImage}
              alt={item.productName}
              className="w-20 h-20 object-cover rounded-lg"
              onError={(e) => {
                e.target.onerror = null
                e.target.src = 'https://placehold.co/80x80?text=No+Image'
              }}
            />
            <div className="flex-1">
              <h3 className="font-semibold">{item.productName}</h3>
              <p className="text-gray-500 text-sm">Qty: {item.quantity}</p>
              <p className="font-bold text-lg">${item.subtotal?.toFixed(2)}</p>
            </div>
            <button
              onClick={() => removeItem(item.id)}
              className="text-red-500 hover:text-red-700 text-sm font-medium"
            >
              Remove
            </button>
          </div>
        ))}
      </div>
      <div className="mt-6 bg-white p-4 rounded-xl shadow flex justify-between items-center">
        <div>
          <p className="text-gray-500 text-sm">Total</p>
          <span className="text-2xl font-bold">${cartTotal?.toFixed(2)}</span>
        </div>
        <button
          onClick={() => navigate('/checkout')}
          className="bg-black text-white px-8 py-3 rounded-lg hover:bg-gray-800 transition font-semibold"
        >
          Checkout
        </button>
      </div>
    </div>
  )
}