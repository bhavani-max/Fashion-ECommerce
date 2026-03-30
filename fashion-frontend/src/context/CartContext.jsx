import { createContext, useContext, useState, useEffect } from 'react'
import { getCart, addToCart, removeFromCart } from '../api/cart'
import { useAuth } from './AuthContext'

const CartContext = createContext()

export function CartProvider({ children }) {
  const { user } = useAuth()
  const [cartItems, setCartItems] = useState([])

  useEffect(() => {
    if (user) fetchCart()
    else setCartItems([])
  }, [user])

  const fetchCart = async () => {
    try {
      console.log('[Cart] Fetching cart for userId:', user?.userId)
      const res = await getCart(user.userId)
      console.log('[Cart] Fetched items:', res.data)
      setCartItems(res.data)
    } catch (err) {
      console.error('[Cart] Failed to fetch cart:', err?.response?.status, err?.response?.data)
    }
  }

  const addItem = async (productId, quantity = 1) => {
    try {
      console.log('[Cart] Adding item — userId:', user?.userId, 'productId:', productId)
      const res = await addToCart(user.userId, productId, quantity)
      console.log('[Cart] Add response:', res.data)
      setCartItems(res.data)
    } catch (err) {
      console.error('[Cart] Failed to add item:', err?.response?.status, err?.response?.data)
      alert('Failed to add item to cart. Please try again.')
    }
  }

  const removeItem = async (cartItemId) => {
    try {
      const res = await removeFromCart(user.userId, cartItemId)
      setCartItems(res.data)
    } catch (err) {
      console.error('[Cart] Failed to remove item:', err?.response?.status, err?.response?.data)
    }
  }

  const cartCount = cartItems.reduce((sum, item) => sum + item.quantity, 0)
  const cartTotal = cartItems.reduce((sum, item) => sum + (item.subtotal || 0), 0)

  return (
    <CartContext.Provider value={{ cartItems, addItem, removeItem, cartCount, cartTotal, fetchCart }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)