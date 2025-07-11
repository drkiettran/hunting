import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Alerts from './pages/Alerts';
import Cases from './pages/Cases';
import Investigations from './pages/Investigations';
import ThreatIntelligence from './pages/ThreatIntelligence';
import Analytics from './pages/Analytics';
import Products from './pages/Products';
import KnowledgeBase from './pages/KnowledgeBase';
import Users from './pages/Users';
import './App.css';

const queryClient = new QueryClient({
	defaultOptions: {
		queries: {
			refetchOnWindowFocus: false,
			retry: 1,
		},
	},
});

function ProtectedRoute({ children }) {
	const { user, loading } = useAuth();

	if (loading) {
		return (
			<div className="min-h-screen flex items-center justify-center">
				<div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
			</div>
		);
	}

	return user ? children : <Navigate to="/login" />;
}

function AppRoutes() {
	const { user } = useAuth();

	return (
		<Routes>
			<Route path="/login" element={user ? <Navigate to="/" /> : <Login />} />
			<Route path="/" element={
				<ProtectedRoute>
					<Layout>
						<Dashboard />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/alerts" element={
				<ProtectedRoute>
					<Layout>
						<Alerts />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/cases" element={
				<ProtectedRoute>
					<Layout>
						<Cases />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/investigations" element={
				<ProtectedRoute>
					<Layout>
						<Investigations />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/threat-intelligence" element={
				<ProtectedRoute>
					<Layout>
						<ThreatIntelligence />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/analytics" element={
				<ProtectedRoute>
					<Layout>
						<Analytics />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/products" element={
				<ProtectedRoute>
					<Layout>
						<Products />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/knowledge-base" element={
				<ProtectedRoute>
					<Layout>
						<KnowledgeBase />
					</Layout>
				</ProtectedRoute>
			} />
			<Route path="/users" element={
				<ProtectedRoute>
					<Layout>
						<Users />
					</Layout>
				</ProtectedRoute>
			} />
		</Routes>
	);
}

function App() {
	return (
		<QueryClientProvider client={queryClient}>
			<AuthProvider>
				<Router>
					<div className="App">
						<AppRoutes />
						<Toaster
							position="top-right"
							toastOptions={{
								duration: 4000,
								style: {
									background: '#363636',
									color: '#fff',
								},
								success: {
									duration: 3000,
									theme: {
										primary: 'green',
										secondary: 'black',
									},
								},
							}}
						/>
					</div>
				</Router>
			</AuthProvider>
		</QueryClientProvider>
	);
}

export default App;