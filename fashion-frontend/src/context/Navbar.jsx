import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from './AuthContext'
import { useCart } from './CartContext'

export default function Navbar() {
  const { user, logoutUser } = useAuth()
  const { cartCount } = useCart()
  const navigate = useNavigate()

  const handleLogout = () => { logoutUser(); navigate('/') }

  return (
    <nav className="bg-black text-white px-6 py-4 flex items-center justify-between sticky top-0 z-50">
      <Link to="/" className="text-xl font-bold tracking-widest">FASHION</Link>
      <div className="flex items-center gap-5">
        <Link to="/" className="hover:text-gray-300 transition text-sm">Home</Link>
        {user ? (
          <>
            <Link to="/wishlist" className="hover:text-gray-300 transition text-sm">Wishlist</Link>
            <Link to="/orders" className="hover:text-gray-300 transition text-sm">Orders</Link>
            <Link to="/cart" className="relative hover:text-gray-300 transition text-sm">
              Cart
              {cartCount > 0 && (
                <span className="absolute -top-2 -right-3 bg-white text-black text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold">
                  {cartCount}
                </span>
              )}
            </Link>
            {user.role === 'ADMIN' && (
              <Link to="/admin" className="text-yellow-400 hover:text-yellow-300 transition text-sm font-semibold">
                Admin
              </Link>
            )}
            <span className="text-gray-400 text-sm">Hi, {user.fullName?.split(' ')[0]}</span>
            <button onClick={handleLogout} className="hover:text-gray-300 transition text-sm">Logout</button>
          </>
        ) : (
          <>
            <Link to="/login" className="hover:text-gray-300 transition text-sm">Login</Link>
            <Link to="/register" className="bg-white text-black px-4 py-1 rounded-lg hover:bg-gray-200 transition text-sm font-semibold">
              Register
            </Link>
          </>
        )}
      </div>
    </nav>
  )
}