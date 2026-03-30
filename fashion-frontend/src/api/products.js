import api from './axios'

export const getAllProducts = () => api.get('/products')
export const getProductById = (id) => api.get(`/products/${id}`)
export const getProductsByCategory = (id) => api.get(`/products/category/${id}`)
export const searchProducts = (keyword) => api.get(`/products/search?keyword=${keyword}`)