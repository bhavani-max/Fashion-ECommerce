import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from './AuthContext'
import { useCart } from './CartContext'

// ── Dummy Payment Modal ──────────────────────────────────────────────────────
function DummyPaymentModal({ amount, onSuccess, onCancel }) {
  const [card, setCard] = useState({ number: '', name: '', expiry: '', cvv: '' })
  const [processing, setProcessing] = useState(false)
  const [error, setError] = useState('')

  const formatCardNumber = (val) =>
    val.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim()

  const formatExpiry = (val) =>
    val.replace(/\D/g, '').slice(0, 4).replace(/^(\d{2})(\d)/, '$1/$2')

  const handlePay = async () => {
    setError('')
    const rawNumber = card.number.replace(/\s/g, '')
    if (rawNumber.length !== 16)   { setError('Enter a valid 16-digit card number.'); return }
    if (!card.name.trim())         { setError('Enter the cardholder name.'); return }
    if (card.expiry.length !== 5)  { setError('Enter a valid expiry (MM/YY).'); return }
    if (card.cvv.length !== 3)     { setError('Enter a valid 3-digit CVV.'); return }

    setProcessing(true)
    // Simulate a 2-second payment gateway delay
    await new Promise(r => setTimeout(r, 2000))
    setProcessing(false)
    onSuccess()
  }

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6">

        {/* Header */}
        <div className="flex items-center justify-between mb-5">
          <div>
            <h3 className="text-lg font-bold text-gray-800">Secure Payment</h3>
            <p className="text-sm text-gray-500">Demo mode — no real money charged</p>
          </div>
          <button onClick={onCancel} className="text-gray-400 hover:text-gray-600 text-2xl leading-none">&times;</button>
        </div>

        {/* Amount badge */}
        <div className="bg-gray-50 rounded-xl px-4 py-3 mb-5 flex justify-between items-center">
          <span className="text-gray-500 text-sm">Amount to Pay</span>
          <span className="text-xl font-bold text-black">₹{amount?.toFixed(2)}</span>
        </div>

        {/* Card fields */}
        <div className="space-y-3">
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Card Number</label>
            <input
              type="text" inputMode="numeric" placeholder="1234 5678 9012 3456"
              maxLength={19}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-black tracking-widest"
              value={card.number}
              onChange={e => setCard({ ...card, number: formatCardNumber(e.target.value) })}
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Cardholder Name</label>
            <input
              type="text" placeholder="Name on card"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-black"
              value={card.name}
              onChange={e => setCard({ ...card, name: e.target.value })}
            />
          </div>
          <div className="flex gap-3">
            <div className="flex-1">
              <label className="block text-xs font-semibold text-gray-600 mb-1">Expiry (MM/YY)</label>
              <input
                type="text" inputMode="numeric" placeholder="MM/YY" maxLength={5}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-black"
                value={card.expiry}
                onChange={e => setCard({ ...card, expiry: formatExpiry(e.target.value) })}
              />
            </div>
            <div className="flex-1">
              <label className="block text-xs font-semibold text-gray-600 mb-1">CVV</label>
              <input
                type="password" inputMode="numeric" placeholder="•••" maxLength={3}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-black"
                value={card.cvv}
                onChange={e => setCard({ ...card, cvv: e.target.value.replace(/\D/g, '').slice(0, 3) })}
              />
            </div>
          </div>
        </div>

        {/* Error */}
        {error && <p className="text-red-500 text-xs mt-3">{error}</p>}

        {/* Pay button */}
        <button
          onClick={handlePay}
          disabled={processing}
          className="w-full mt-5 bg-black text-white py-3 rounded-xl font-semibold text-sm hover:bg-gray-800 transition disabled:opacity-60 flex items-center justify-center gap-2"
        >
          {processing ? (
            <>
              <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
              </svg>
              Processing Payment...
            </>
          ) : (
            `Pay ₹${amount?.toFixed(2)}`
          )}
        </button>

        <p className="text-center text-gray-400 text-xs mt-3">🔒 This is a dummy payment — safe to test</p>
      </div>
    </div>
  )
}

// ── Main CheckoutPage ────────────────────────────────────────────────────────
export default function CheckoutPage() {
  const [form, setForm] = useState({ shippingAddress: '', paymentMethod: 'CARD' })
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)
  const [showPaymentModal, setShowPaymentModal] = useState(false)
  const [pendingOrder, setPendingOrder] = useState(null)

  const { user } = useAuth()
  const { cartTotal, fetchCart } = useCart()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.shippingAddress.trim()) { alert('Please enter a shipping address'); return }
    setLoading(true)

    try {
      // Place order in backend
      const orderRes = await api.post(`/orders/${user.userId}/place`, form)
      const appOrder = orderRes.data
      await fetchCart()

      if (form.paymentMethod === 'CARD') {
        // Show dummy card payment modal
        setPendingOrder(appOrder)
        setShowPaymentModal(true)
      } else {
        // Cash on Delivery — no payment needed
        setSuccess(true)
        setTimeout(() => navigate('/orders'), 3000)
      }
    } catch (err) {
      alert('Order failed. Please try again.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handlePaymentSuccess = () => {
    setShowPaymentModal(false)
    setSuccess(true)
    setTimeout(() => navigate('/orders'), 3000)
  }

  const handlePaymentCancel = () => {
    setShowPaymentModal(false)
    setPendingOrder(null)
  }

  // ── Success screen ──
  if (success) return (
    <div className="text-center py-20">
      <p className="text-6xl mb-4">✅</p>
      <h2 className="text-2xl font-bold text-green-600">Order Placed Successfully!</h2>
      <p className="text-gray-500 mt-2">Redirecting to your orders in 3 seconds...</p>
    </div>
  )

  return (
    <>
      {/* Dummy payment popup */}
      {showPaymentModal && (
        <DummyPaymentModal
          amount={cartTotal}
          onSuccess={handlePaymentSuccess}
          onCancel={handlePaymentCancel}
        />
      )}

      {/* Checkout form */}
      <div className="max-w-lg mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold mb-6">Checkout</h2>
        <form onSubmit={handleSubmit} className="space-y-4 bg-white p-6 rounded-xl shadow">

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Shipping Address</label>
            <textarea rows={3} placeholder="Enter your full shipping address"
              className="w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-black"
              value={form.shippingAddress}
              onChange={e => setForm({ ...form, shippingAddress: e.target.value })}
              required />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Payment Method</label>
            <select
              className="w-full border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-black"
              value={form.paymentMethod}
              onChange={e => setForm({ ...form, paymentMethod: e.target.value })}>
              <option value="CARD">💳 Credit / Debit Card (Demo)</option>
              <option value="CASH_ON_DELIVERY">🚚 Cash on Delivery</option>
            </select>
          </div>

          <div className="border-t pt-4 flex justify-between items-center">
            <span className="text-gray-500">Order Total</span>
            <span className="text-xl font-bold">₹{cartTotal?.toFixed(2)}</span>
          </div>

          <button
            disabled={loading}
            className="w-full bg-black text-white py-3 rounded-lg hover:bg-gray-800 transition font-semibold disabled:opacity-50">
            {loading ? 'Processing...' : form.paymentMethod === 'CARD' ? '💳 Proceed to Pay' : '🚚 Place Order'}
          </button>
        </form>
      </div>
    </>
  )
}