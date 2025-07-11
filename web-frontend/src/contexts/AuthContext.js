import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiClient } from '../services/api';
import toast from 'react-hot-toast';

const AuthContext = createContext();

export function useAuth() {
	return useContext(AuthContext);
}

export function AuthProvider({ children }) {
	const [user, setUser] = useState(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const token = localStorage.getItem('token');
		if (token) {
			apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
			// Validate token and get user info
			getCurrentUser();
		} else {
			setLoading(false);
		}
	}, []);

	async function getCurrentUser() {
		try {
			const response = await apiClient.get('/api/users/me');
			if (response.data.success) {
				setUser(response.data.data);
			}
		} catch (error) {
			console.error('Failed to get current user:', error);
			logout();
		} finally {
			setLoading(false);
		}
	}

	async function login(username, password) {
		try {
			const response = await apiClient.post('/api/users/login', {
				username,
				password
			});

			if (response.data.success) {
				const { token, user } = response.data.data;
				localStorage.setItem('token', token);
				apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
				setUser(user);
				toast.success('Login successful!');
				return true;
			}
		} catch (error) {
			const message = error.response?.data?.message || 'Login failed';
			toast.error(message);
			return false;
		}
	}

	function logout() {
		localStorage.removeItem('token');
		delete apiClient.defaults.headers.common['Authorization'];
		setUser(null);
		toast.success('Logged out successfully');
	}

	const value = {
		user,
		login,
		logout,
		loading
	};

	return (
		<AuthContext.Provider value={value}>
			{children}
		</AuthContext.Provider>
	);
}