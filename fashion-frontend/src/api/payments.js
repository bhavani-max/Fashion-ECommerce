import api from './axios'

export const createPayment = (orderId) => api.post(`/payments/create/${orderId}`)
export const verifyPayment = (data) => api.post('/payments/verify', data)