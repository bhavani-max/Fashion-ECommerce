import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { CartProvider } from './context/CartContext'

import Navbar from './context/Navbar'
import HomePage from './context/HomePage'
import LoginPage from './context/LoginPage'
import RegisterPage from './context/RegisterPage'
import CartPage from './context/CartPage'
import CheckoutPage from './context/CheckoutPage'
import WishlistPage from './context/WishlistPage'
import OrdersPage from './context/OrdersPage'
import AdminDashboard from './context/AdminDashboard'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <div className="min-h-screen bg-gray-50">
            <Navbar />
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/cart" element={<CartPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/wishlist" element={<WishlistPage />} />
              <Route path="/orders" element={<OrdersPage />} />
              <Route path="/admin" element={<AdminDashboard />} />
            </Routes>
          </div>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}