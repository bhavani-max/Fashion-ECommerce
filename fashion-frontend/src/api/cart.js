import api from './axios'

export const getCart = (userId) => api.get(`/cart/${userId}`)
export const addToCart = (userId, productId, quantity) =>
  api.post(`/cart/${userId}/add`, { productId, quantity })
export const removeFromCart = (userId, cartItemId) =>
  api.delete(`/cart/${userId}/remove/${cartItemId}`)