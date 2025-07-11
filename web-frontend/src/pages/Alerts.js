import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { AlertTriangle, Clock, User, Check, X, Eye } from 'lucide-react';
import toast from 'react-hot-toast';
import { alertService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

function Alerts() {
	const [selectedSeverity, setSelectedSeverity] = useState('');
	const [selectedStatus, setSelectedStatus] = useState('');
	const [searchTerm, setSearchTerm] = useState('');
	const [page, setPage] = useState(0);
	const { user } = useAuth();
	const queryClient = useQueryClient();

	const { data: alertsData, isLoading } = useQuery(
		['alerts', page, selectedSeverity, selectedStatus, searchTerm],
		() => alertService.getAlerts({
			page,
			size: 20,
			severity: selectedSeverity,
			status: selectedStatus,
			search: searchTerm,
		}),
		{ keepPreviousData: true }
	);

	const assignAlertMutation = useMutation(
		({ alertId, analystId }) => alertService.assignAlert(alertId, analystId),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('alerts');
				toast.success('Alert assigned successfully');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to assign alert');
			},
		}
	);

	const resolveAlertMutation = useMutation(
		({ alertId, notes }) => alertService.resolveAlert(alertId, notes),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('alerts');
				toast.success('Alert resolved successfully');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to resolve alert');
			},
		}
	);

	const markFalsePositiveMutation = useMutation(
		({ alertId, notes }) => alertService.markFalsePositive(alertId, notes),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('alerts');
				toast.success('Alert marked as false positive');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to mark as false positive');
			},
		}
	);

	const handleAssignToMe = (alertId) => {
		assignAlertMutation.mutate({ alertId, analystId: user.username });
	};

	const handleResolve = (alertId) => {
		const notes = prompt('Enter resolution notes:');
		if (notes) {
			resolveAlertMutation.mutate({ alertId, notes });
		}
	};

	const handleFalsePositive = (alertId) => {
		const notes = prompt('Enter notes for false positive:');
		if (notes) {
			markFalsePositiveMutation.mutate({ alertId, notes });
		}
	};

	const getSeverityColor = (severity) => {
		switch (severity) {
			case 'CRITICAL': return 'bg-red-100 text-red-800';
			case 'HIGH': return 'bg-orange-100 text-orange-800';
			case 'MEDIUM': return 'bg-yellow-100 text-yellow-800';
			case 'LOW': return 'bg-blue-100 text-blue-800';
			default: return 'bg-gray-100 text-gray-800';
		}
	};

	const getStatusColor = (status) => {
		switch (status) {
			case 'NEW': return 'bg-red-100 text-red-800';
			case 'ASSIGNED': return 'bg-blue-100 text-blue-800';
			case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800';
			case 'RESOLVED': return 'bg-green-100 text-green-800';
			case 'FALSE_POSITIVE': return 'bg-gray-100 text-gray-800';
			default: return 'bg-gray-100 text-gray-800';
		}
	};

	if (isLoading) {
		return (
			<div className="flex items-center justify-center h-64">
				<div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
			</div>
		);
	}

	const alerts = alertsData?.data?.data?.content || [];

	return (
		<div className="space-y-6">
			<div className="flex justify-between items-center">
				<h1 className="text-3xl font-bold text-gray-900">Alerts</h1>
			</div>

			{/* Filters */}
			<div className="bg-white p-4 rounded-lg shadow space-y-4">
				<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Search</label>
						<input
							type="text"
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							placeholder="Search alerts..."
							value={searchTerm}
							onChange={(e) => setSearchTerm(e.target.value)}
						/>
					</div>
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Severity</label>
						<select
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							value={selectedSeverity}
							onChange={(e) => setSelectedSeverity(e.target.value)}
						>
							<option value="">All Severities</option>
							<option value="CRITICAL">Critical</option>
							<option value="HIGH">High</option>
							<option value="MEDIUM">Medium</option>
							<option value="LOW">Low</option>
						</select>
					</div>
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
						<select
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							value={selectedStatus}
							onChange={(e) => setSelectedStatus(e.target.value)}
						>
							<option value="">All Statuses</option>
							<option value="NEW">New</option>
							<option value="ASSIGNED">Assigned</option>
							<option value="IN_PROGRESS">In Progress</option>
							<option value="RESOLVED">Resolved</option>
							<option value="FALSE_POSITIVE">False Positive</option>
						</select>
					</div>
				</div>
			</div>

			{/* Alerts List */}
			<div className="bg-white shadow overflow-hidden sm:rounded-md">
				<ul className="divide-y divide-gray-200">
					{alerts.map((alert) => (
						<li key={alert.id} className="px-6 py-4">
							<div className="flex items-center justify-between">
								<div className="flex items-center">
									<AlertTriangle className={`h-5 w-5 mr-3 ${alert.severity === 'CRITICAL' || alert.severity === 'HIGH' ? 'text-red-500' : 'text-yellow-500'
										}`} />
									<div className="min-w-0 flex-1">
										<p className="text-sm font-medium text-gray-900 truncate">
											{alert.description}
										</p>
										<div className="flex items-center mt-1 space-x-4">
											<div className="flex items-center text-sm text-gray-500">
												<Clock className="h-4 w-4 mr-1" />
												{new Date(alert.timestamp).toLocaleString()}
											</div>
											{alert.assignedTo && (
												<div className="flex items-center text-sm text-gray-500">
													<User className="h-4 w-4 mr-1" />
													{alert.assignedTo}
												</div>
											)}
										</div>
									</div>
								</div>
								<div className="flex items-center space-x-2">
									<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getSeverityColor(alert.severity)}`}>
										{alert.severity}
									</span>
									<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(alert.status)}`}>
										{alert.status}
									</span>
									<div className="flex space-x-1">
										{alert.status === 'NEW' && (
											<button
												onClick={() => handleAssignToMe(alert.id)}
												className="p-1 text-blue-600 hover:text-blue-800"
												title="Assign to me"
											>
												<User className="h-4 w-4" />
											</button>
										)}
										{(alert.status === 'ASSIGNED' || alert.status === 'IN_PROGRESS') && (
											<>
												<button
													onClick={() => handleResolve(alert.id)}
													className="p-1 text-green-600 hover:text-green-800"
													title="Resolve"
												>
													<Check className="h-4 w-4" />
												</button>
												<button
													onClick={() => handleFalsePositive(alert.id)}
													className="p-1 text-gray-600 hover:text-gray-800"
													title="Mark as false positive"
												>
													<X className="h-4 w-4" />
												</button>
											</>
										)}
										<button className="p-1 text-gray-600 hover:text-gray-800" title="View details">
											<Eye className="h-4 w-4" />
										</button>
									</div>
								</div>
							</div>
						</li>
					))}
				</ul>
			</div>

			{/* Pagination */}
			{alertsData?.data?.data?.totalPages > 1 && (
				<div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
					<div className="flex-1 flex justify-between sm:hidden">
						<button
							onClick={() => setPage(Math.max(0, page - 1))}
							disabled={page === 0}
							className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
						>
							Previous
						</button>
						<button
							onClick={() => setPage(page + 1)}
							disabled={page >= alertsData.data.data.totalPages - 1}
							className="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
						>
							Next
						</button>
					</div>
				</div>
			)}
		</div>
	);
}

export default Alerts;