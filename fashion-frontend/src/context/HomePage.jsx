import { useEffect, useState } from 'react'
import { getAllProducts, searchProducts } from '../api/products'
import { getRecommendations } from '../api/analytics'
import { addToWishlist, removeFromWishlist, isWishlisted } from '../api/wishlist'
import { useCart } from './CartContext'
import { useAuth } from './AuthContext'
import { useNavigate } from 'react-router-dom'

function ProductCard({ product, user, onAddToCart, wishlisted, onToggleWishlist }) {
  return (
    <div className="bg-white rounded-xl shadow hover:shadow-lg transition overflow-hidden group">
      <div className="overflow-hidden relative">
        <img src={product.imageUrl} alt={product.name}
          className="w-full h-48 object-cover group-hover:scale-105 transition duration-300"
          onError={e => { e.target.onerror = null; e.target.src = 'https://placehold.co/400x300?text=No+Image' }} />
        {user && (
          <button onClick={() => onToggleWishlist(product.id)}
            className="absolute top-2 right-2 text-2xl hover:scale-110 transition">
            {wishlisted ? '❤️' : '🤍'}
          </button>
        )}
      </div>
      <div className="p-4">
        <span className="text-xs text-gray-400 uppercase tracking-wide">{product.categoryName}</span>
        <h3 className="font-semibold text-lg mt-1">{product.name}</h3>
        <p className="text-gray-500 text-sm">{product.brand} · {product.color} · {product.size}</p>
        <div className="flex items-center justify-between mt-3">
          <span className="font-bold text-lg">${product.price}</span>
          <button onClick={() => onAddToCart(product.id)}
            className="bg-black text-white text-sm px-3 py-1 rounded-lg hover:bg-gray-800 transition">
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  )
}

export default function HomePage() {
  const [products, setProducts] = useState([])
  const [recommendations, setRecommendations] = useState([])
  const [keyword, setKeyword] = useState('')
  const [wishlistedIds, setWishlistedIds] = useState(new Set())
  const { addItem } = useCart()
  const { user } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    getAllProducts().then(res => setProducts(res.data))
    if (user) {
      getRecommendations(user.userId).then(res => setRecommendations(res.data))
    }
  }, [user])

  const handleSearch = async (e) => {
    e.preventDefault()
    if (!keyword.trim()) { getAllProducts().then(res => setProducts(res.data)); return }
    const res = await searchProducts(keyword)
    setProducts(res.data)
  }

  const handleAddToCart = (productId) => {
    if (!user) { navigate('/login'); return }
    addItem(productId, 1)
  }

  const handleToggleWishlist = async (productId) => {
    if (!user) { navigate('/login'); return }
    if (wishlistedIds.has(productId)) {
      await removeFromWishlist(user.userId, productId)
      setWishlistedIds(prev => { const s = new Set(prev); s.delete(productId); return s })
    } else {
      await addToWishlist(user.userId, productId)
      setWishlistedIds(prev => new Set(prev).add(productId))
    }
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="text-center mb-10">
        <h1 className="text-4xl font-bold mb-2">New Arrivals</h1>
        <p className="text-gray-500">Discover the latest trends in fashion</p>
      </div>

      <form onSubmit={handleSearch} className="flex gap-2 mb-8 max-w-xl mx-auto">
        <input type="text" placeholder="Search products..."
          className="flex-1 border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-black"
          value={keyword} onChange={e => setKeyword(e.target.value)} />
        <button className="bg-black text-white px-6 py-2 rounded-lg hover:bg-gray-800 transition">Search</button>
      </form>

      {/* Recommendations section */}
      {user && recommendations.length > 0 && (
        <div className="mb-12">
          <h2 className="text-xl font-bold mb-4">✨ Recommended for You</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
            {recommendations.map(product => (
              <ProductCard key={product.id} product={product} user={user}
                onAddToCart={handleAddToCart}
                wishlisted={wishlistedIds.has(product.id)}
                onToggleWishlist={handleToggleWishlist} />
            ))}
          </div>
        </div>
      )}

      {/* All products */}
      <h2 className="text-xl font-bold mb-4">All Products</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        {products.map(product => (
          <ProductCard key={product.id} product={product} user={user}
            onAddToCart={handleAddToCart}
            wishlisted={wishlistedIds.has(product.id)}
            onToggleWishlist={handleToggleWishlist} />
        ))}
      </div>

      {products.length === 0 && (
        <p className="text-center text-gray-400 mt-20 text-lg">No products found.</p>
      )}
    </div>
  )
}