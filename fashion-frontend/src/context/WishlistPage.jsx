import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getWishlist, removeFromWishlist } from '../api/wishlist'
import { useAuth } from './AuthContext'
import { useCart } from './CartContext'

export default function WishlistPage() {
  const { user } = useAuth()
  const { addItem } = useCart()
  const navigate = useNavigate()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user) { navigate('/login'); return }
    getWishlist(user.userId).then(res => setItems(res.data)).finally(() => setLoading(false))
  }, [user])

  const handleRemove = async (productId) => {
    await removeFromWishlist(user.userId, productId)
    setItems(items.filter(i => i.productId !== productId))
  }

  const handleAddToCart = async (productId) => {
    await addItem(productId, 1)
    alert('Added to cart!')
  }

  if (loading) return <div className="text-center py-20 text-gray-400">Loading wishlist...</div>

  if (items.length === 0) return (
    <div className="text-center py-20">
      <p className="text-5xl mb-4">🤍</p>
      <p className="text-gray-500 text-lg mb-4">Your wishlist is empty</p>
      <button onClick={() => navigate('/')}
        className="bg-black text-white px-6 py-2 rounded-lg hover:bg-gray-800 transition">
        Browse Products
      </button>
    </div>
  )

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <h2 className="text-2xl font-bold mb-6">My Wishlist ({items.length} items)</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {items.map(item => (
          <div key={item.id} className="bg-white rounded-xl shadow hover:shadow-lg transition overflow-hidden">
            <img src={item.productImage} alt={item.productName}
              className="w-full h-48 object-cover"
              onError={e => { e.target.src = 'https://placehold.co/400x300?text=No+Image' }} />
            <div className="p-4">
              <span className="text-xs text-gray-400 uppercase">{item.categoryName}</span>
              <h3 className="font-semibold text-lg mt-1">{item.productName}</h3>
              <p className="text-gray-500 text-sm">{item.brand}</p>
              <p className="font-bold text-lg mt-1">${item.productPrice}</p>
              <div className="flex gap-2 mt-3">
                <button onClick={() => handleAddToCart(item.productId)}
                  className="flex-1 bg-black text-white text-sm py-2 rounded-lg hover:bg-gray-800 transition">
                  Add to Cart
                </button>
                <button onClick={() => handleRemove(item.productId)}
                  className="px-3 py-2 border border-red-300 text-red-500 rounded-lg hover:bg-red-50 transition text-sm">
                  Remove
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}