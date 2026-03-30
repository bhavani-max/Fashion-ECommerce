import api from './axios'

export const getRecommendations = (userId) => api.get(`/recommendations/${userId}`)
export const getAnalytics = () => api.get('/admin/analytics')