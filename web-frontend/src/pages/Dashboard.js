import React from 'react';
import { useQuery } from 'react-query';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import { AlertTriangle, FolderOpen, Shield, BarChart3, TrendingUp, Users } from 'lucide-react';
import { alertService, caseService, threatIntelService, analyticsService } from '../services/api';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

function Dashboard() {
	const { data: alertStats } = useQuery('alertStats', () => alertService.getStatistics());
	const { data: caseStats } = useQuery('caseStats', () => caseService.getStatistics());
	const { data: threatStats } = useQuery('threatStats', () => threatIntelService.getStatistics());
	const { data: analyticsStats } = useQuery('analyticsStats', () => analyticsService.getStatistics());

	const { data: highPriorityAlerts } = useQuery('highPriorityAlerts', () => alertService.getHighPriority());
	const { data: overdueCases } = useQuery('overdueCases', () => caseService.getOverdue());

	const stats = [
		{
			name: 'Active Alerts',
			value: alertStats?.data?.NEW || 0,
			icon: AlertTriangle,
			color: 'text-red-600',
			bgColor: 'bg-red-100',
		},
		{
			name: 'Open Cases',
			value: caseStats?.data?.OPEN || 0,
			icon: FolderOpen,
			color: 'text-blue-600',
			bgColor: 'bg-blue-100',
		},
		{
			name: 'Threat Intel',
			value: Object.values(threatStats?.data || {}).reduce((a, b) => a + b, 0),
			icon: Shield,
			color: 'text-green-600',
			bgColor: 'bg-green-100',
		},
		{
			name: 'Active Analytics',
			value: Object.values(analyticsStats?.data || {}).reduce((a, b) => a + b, 0),
			icon: BarChart3,
			color: 'text-purple-600',
			bgColor: 'bg-purple-100',
		},
	];

	const alertChartData = alertStats?.data ? Object.entries(alertStats.data).map(([status, count]) => ({
		status,
		count
	})) : [];

	const caseChartData = caseStats?.data ? Object.entries(caseStats.data).map(([status, count]) => ({
		status,
		count
	})) : [];

	return (
		<div className="space-y-6">
			<h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>

			{/* Stats Grid */}
			<div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
				{stats.map((stat) => {
					const Icon = stat.icon;
					return (
						<div key={stat.name} className="relative bg-white pt-5 px-4 pb-12 sm:pt-6 sm:px-6 shadow rounded-lg overflow-hidden">
							<div>
								<div className={`absolute ${stat.bgColor} rounded-md p-3`}>
									<Icon className={`h-6 w-6 ${stat.color}`} />
								</div>
								<p className="ml-16 text-sm font-medium text-gray-500 truncate">{stat.name}</p>
							</div>
							<div className="ml-16 pb-6 flex items-baseline">
								<p className="text-2xl font-semibold text-gray-900">{stat.value}</p>
							</div>
						</div>
					);
				})}
			</div>

			{/* Charts Row */}
			<div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
				{/* Alert Status Chart */}
				<div className="bg-white p-6 shadow rounded-lg">
					<h3 className="text-lg font-medium text-gray-900 mb-4">Alert Status Distribution</h3>
					<ResponsiveContainer width="100%" height={300}>
						<BarChart data={alertChartData}>
							<CartesianGrid strokeDasharray="3 3" />
							<XAxis dataKey="status" />
							<YAxis />
							<Tooltip />
							<Bar dataKey="count" fill="#3B82F6" />
						</BarChart>
					</ResponsiveContainer>
				</div>

				{/* Case Status Chart */}
				<div className="bg-white p-6 shadow rounded-lg">
					<h3 className="text-lg font-medium text-gray-900 mb-4">Case Status Distribution</h3>
					<ResponsiveContainer width="100%" height={300}>
						<PieChart>
							<Pie
								data={caseChartData}
								cx="50%"
								cy="50%"
								labelLine={false}
								label={({ status, percent }) => `${status} ${(percent * 100).toFixed(0)}%`}
								outerRadius={80}
								fill="#8884d8"
								dataKey="count"
							>
								{caseChartData.map((entry, index) => (
									<Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
								))}
							</Pie>
							<Tooltip />
						</PieChart>
					</ResponsiveContainer>
				</div>
			</div>

			{/* Recent Activity */}
			<div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
				{/* High Priority Alerts */}
				<div className="bg-white shadow rounded-lg">
					<div className="px-4 py-5 sm:p-6">
						<h3 className="text-lg font-medium text-gray-900 mb-4">High Priority Alerts</h3>
						<div className="space-y-3">
							{highPriorityAlerts?.data?.slice(0, 5).map((alert) => (
								<div key={alert.id} className="flex items-center justify-between p-3 bg-red-50 rounded-lg">
									<div className="flex items-center">
										<AlertTriangle className="h-5 w-5 text-red-500 mr-3" />
										<div>
											<p className="text-sm font-medium text-gray-900">{alert.description}</p>
											<p className="text-xs text-gray-500">{new Date(alert.timestamp).toLocaleString()}</p>
										</div>
									</div>
									<span className={`px-2 py-1 text-xs font-semibold rounded-full ${alert.severity === 'CRITICAL' ? 'bg-red-100 text-red-800' : 'bg-orange-100 text-orange-800'
										}`}>
										{alert.severity}
									</span>
								</div>
							)) || (
									<p className="text-gray-500 text-sm">No high priority alerts</p>
								)}
						</div>
					</div>
				</div>

				{/* Overdue Cases */}
				<div className="bg-white shadow rounded-lg">
					<div className="px-4 py-5 sm:p-6">
						<h3 className="text-lg font-medium text-gray-900 mb-4">Overdue Cases</h3>
						<div className="space-y-3">
							{overdueCases?.data?.slice(0, 5).map((caseItem) => (
								<div key={caseItem.id} className="flex items-center justify-between p-3 bg-yellow-50 rounded-lg">
									<div className="flex items-center">
										<FolderOpen className="h-5 w-5 text-yellow-500 mr-3" />
										<div>
											<p className="text-sm font-medium text-gray-900">{caseItem.title}</p>
											<p className="text-xs text-gray-500">Due: {new Date(caseItem.dueDate).toLocaleDateString()}</p>
										</div>
									</div>
									<span className={`px-2 py-1 text-xs font-semibold rounded-full ${caseItem.priority === 'CRITICAL' ? 'bg-red-100 text-red-800' :
											caseItem.priority === 'HIGH' ? 'bg-orange-100 text-orange-800' : 'bg-yellow-100 text-yellow-800'
										}`}>
										{caseItem.priority}
									</span>
								</div>
							)) || (
									<p className="text-gray-500 text-sm">No overdue cases</p>
								)}
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}

export default Dashboard;