import api from './axios'

export const placeOrder = (userId, data) => api.post(`/orders/${userId}/place`, data)
export const getOrders = (userId) => api.get(`/orders/${userId}`)