import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import {
	Home,
	AlertTriangle,
	FolderOpen,
	Search,
	Shield,
	BarChart3,
	FileText,
	Database,
	Users,
	LogOut,
	Menu,
	X
} from 'lucide-react';

function Layout({ children }) {
	const [sidebarOpen, setSidebarOpen] = useState(false);
	const { user, logout } = useAuth();
	const location = useLocation();
	const navigate = useNavigate();

	const navigation = [
		{ name: 'Dashboard', href: '/', icon: Home },
		{ name: 'Alerts', href: '/alerts', icon: AlertTriangle },
		{ name: 'Cases', href: '/cases', icon: FolderOpen },
		{ name: 'Investigations', href: '/investigations', icon: Search },
		{ name: 'Threat Intelligence', href: '/threat-intelligence', icon: Shield },
		{ name: 'Analytics', href: '/analytics', icon: BarChart3 },
		{ name: 'Intelligence Products', href: '/products', icon: FileText },
		{ name: 'Knowledge Base', href: '/knowledge-base', icon: Database },
	];

	if (user?.role === 'ADMIN') {
		navigation.push({ name: 'Users', href: '/users', icon: Users });
	}

	const handleLogout = () => {
		logout();
		navigate('/login');
	};

	return (
		<div className="h-screen flex overflow-hidden bg-gray-100">
			{/* Mobile sidebar */}
			<div className={`fixed inset-0 flex z-40 md:hidden ${sidebarOpen ? '' : 'hidden'}`}>
				<div className="fixed inset-0 bg-gray-600 bg-opacity-75" onClick={() => setSidebarOpen(false)} />
				<div className="relative flex-1 flex flex-col max-w-xs w-full bg-white">
					<div className="absolute top-0 right-0 -mr-12 pt-2">
						<button
							className="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
							onClick={() => setSidebarOpen(false)}
						>
							<X className="h-6 w-6 text-white" />
						</button>
					</div>
					<SidebarContent navigation={navigation} currentPath={location.pathname} />
				</div>
			</div>

			{/* Static sidebar for desktop */}
			<div className="hidden md:flex md:flex-shrink-0">
				<div className="flex flex-col w-64">
					<SidebarContent navigation={navigation} currentPath={location.pathname} />
				</div>
			</div>

			{/* Main content */}
			<div className="flex flex-col w-0 flex-1 overflow-hidden">
				{/* Top nav */}
				<div className="md:hidden pl-1 pt-1 sm:pl-3 sm:pt-3">
					<button
						className="-ml-0.5 -mt-0.5 h-12 w-12 inline-flex items-center justify-center rounded-md text-gray-500 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500"
						onClick={() => setSidebarOpen(true)}
					>
						<Menu className="h-6 w-6" />
					</button>
				</div>

				{/* Header */}
				<header className="bg-white shadow-sm border-b border-gray-200">
					<div className="px-4 sm:px-6 lg:px-8">
						<div className="flex justify-between h-16 items-center">
							<h1 className="text-2xl font-semibold text-gray-900">
								Persistent Hunt System
							</h1>
							<div className="flex items-center space-x-4">
								<span className="text-sm text-gray-700">
									Welcome, {user?.username}
								</span>
								<button
									onClick={handleLogout}
									className="flex items-center space-x-2 text-gray-500 hover:text-gray-700 px-3 py-2 rounded-md text-sm font-medium"
								>
									<LogOut className="h-4 w-4" />
									<span>Logout</span>
								</button>
							</div>
						</div>
					</div>
				</header>

				{/* Main content area */}
				<main className="flex-1 relative overflow-y-auto focus:outline-none bg-gray-50">
					<div className="py-6">
						<div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
							{children}
						</div>
					</div>
				</main>
			</div>
		</div>
	);
}

function SidebarContent({ navigation, currentPath }) {
	return (
		<div className="flex flex-col h-full">
			{/* Logo */}
			<div className="flex items-center h-16 flex-shrink-0 px-4 bg-gray-900">
				<Shield className="h-8 w-8 text-white" />
				<span className="ml-2 text-white font-semibold text-lg">Hunt System</span>
			</div>

			{/* Navigation */}
			<div className="flex-1 flex flex-col overflow-y-auto">
				<nav className="flex-1 px-2 py-4 bg-gray-800 space-y-1">
					{navigation.map((item) => {
						const Icon = item.icon;
						const isActive = currentPath === item.href;
						return (
							<a
								key={item.name}
								href={item.href}
								className={`${isActive
										? 'bg-gray-900 text-white'
										: 'text-gray-300 hover:bg-gray-700 hover:text-white'
									} group flex items-center px-2 py-2 text-sm font-medium rounded-md`}
							>
								<Icon className="mr-3 h-5 w-5 flex-shrink-0" />
								{item.name}
							</a>
						);
					})}
				</nav>
			</div>
		</div>
	);
}

export default Layout;