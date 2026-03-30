import api from './axios'

export const getShipmentByOrder = (orderId) => api.get(`/shipments/order/${orderId}`)
export const trackShipment = (trackingNumber) => api.get(`/shipments/track/${trackingNumber}`)