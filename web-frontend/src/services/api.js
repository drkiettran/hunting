import axios from 'axios';
import toast from 'react-hot-toast';

export const apiClient = axios.create({
	baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
	timeout: 10000,
});

// Request interceptor
apiClient.interceptors.request.use(
	(config) => {
		const token = localStorage.getItem('token');
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	},
	(error) => {
		return Promise.reject(error);
	}
);

// Response interceptor
apiClient.interceptors.response.use(
	(response) => {
		return response;
	},
	(error) => {
		if (error.response?.status === 401) {
			localStorage.removeItem('token');
			window.location.href = '/login';
		} else if (error.response?.status >= 500) {
			toast.error('Server error occurred. Please try again.');
		} else if (error.code === 'ECONNABORTED') {
			toast.error('Request timeout. Please try again.');
		}
		return Promise.reject(error);
	}
);

// API service functions
export const authService = {
	login: (credentials) => apiClient.post('/api/users/login', credentials),
	getCurrentUser: () => apiClient.get('/api/users/me'),
};

export const alertService = {
	getAlerts: (params) => apiClient.get('/api/alerts', { params }),
	getAlert: (id) => apiClient.get(`/api/alerts/${id}`),
	updateAlert: (id, data) => apiClient.put(`/api/alerts/${id}`, data),
	assignAlert: (id, analystId) => apiClient.post(`/api/alerts/${id}/assign`, { analystId }),
	resolveAlert: (id, notes) => apiClient.post(`/api/alerts/${id}/resolve`, { resolutionNotes: notes }),
	markFalsePositive: (id, notes) => apiClient.post(`/api/alerts/${id}/false-positive`, { notes }),
	getHighPriority: () => apiClient.get('/api/alerts/high-priority'),
	getStatistics: () => apiClient.get('/api/alerts/statistics/status'),
};

export const caseService = {
	getCases: (params) => apiClient.get('/api/cases', { params }),
	getCase: (id) => apiClient.get(`/api/cases/${id}`),
	createCase: (data) => apiClient.post('/api/cases', data),
	updateCase: (id, data) => apiClient.put(`/api/cases/${id}`, data),
	assignCase: (id, analystId) => apiClient.post(`/api/cases/${id}/assign`, { analystId }),
	updateStatus: (id, status) => apiClient.post(`/api/cases/${id}/status`, { status }),
	getOverdue: () => apiClient.get('/api/cases/overdue'),
	getStatistics: () => apiClient.get('/api/cases/statistics/status'),
};

export const threatIntelService = {
	getThreatIntelligence: (params) => apiClient.get('/api/threat-intelligence', { params }),
	getThreatIntel: (id) => apiClient.get(`/api/threat-intelligence/${id}`),
	createThreatIntel: (data) => apiClient.post('/api/threat-intelligence', data),
	updateThreatIntel: (id, data) => apiClient.put(`/api/threat-intelligence/${id}`, data),
	getHighPriority: () => apiClient.get('/api/threat-intelligence/high-priority'),
	getStatistics: () => apiClient.get('/api/threat-intelligence/statistics/threat-types'),
};

export const analyticsService = {
	getAnalytics: (params) => apiClient.get('/api/analytics', { params }),
	getAnalytic: (id) => apiClient.get(`/api/analytics/${id}`),
	createAnalytic: (data) => apiClient.post('/api/analytics', data),
	updateAnalytic: (id, data) => apiClient.put(`/api/analytics/${id}`, data),
	activateAnalytic: (id) => apiClient.post(`/api/analytics/${id}/activate`),
	deactivateAnalytic: (id) => apiClient.post(`/api/analytics/${id}/deactivate`),
	executeAnalytic: (id) => apiClient.post(`/api/analytics/${id}/execute`),
	getStatistics: () => apiClient.get('/api/analytics/statistics/platform'),
};

export const userService = {
	getUsers: (params) => apiClient.get('/api/users', { params }),
	getUser: (id) => apiClient.get(`/api/users/${id}`),
	createUser: (data) => apiClient.post('/api/users/register', data),
	updateUser: (id, data) => apiClient.put(`/api/users/${id}`, data),
	deactivateUser: (id) => apiClient.delete(`/api/users/${id}`),
	changePassword: (data) => apiClient.post('/api/users/change-password', data),
};