import api from './axios'

export const getWishlist = (userId) => api.get(`/wishlist/${userId}`)
export const addToWishlist = (userId, productId) =>
  api.post(`/wishlist/${userId}/add`, { productId })
export const removeFromWishlist = (userId, productId) =>
  api.delete(`/wishlist/${userId}/remove/${productId}`)
export const isWishlisted = (userId, productId) =>
  api.get(`/wishlist/${userId}/check/${productId}`)