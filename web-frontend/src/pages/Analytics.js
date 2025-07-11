
import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { BarChart3, Plus, Play, Pause, Settings, TrendingUp, Activity } from 'lucide-react';
import toast from 'react-hot-toast';
import { analyticsService } from '../services/api';

function Analytics() {
	const [showCreateModal, setShowCreateModal] = useState(false);
	const [selectedPlatform, setSelectedPlatform] = useState('');
	const [searchTerm, setSearchTerm] = useState('');
	const [page, setPage] = useState(0);
	const queryClient = useQueryClient();

	const { data: analyticsData, isLoading } = useQuery(
		['analytics', page, selectedPlatform, searchTerm],
		() => analyticsService.getAnalytics({
			page,
			size: 20,
			platform: selectedPlatform,
			search: searchTerm,
			activeOnly: true,
		}),
		{ keepPreviousData: true }
	);

	const createAnalyticMutation = useMutation(
		(data) => analyticsService.createAnalytic(data),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('analytics');
				setShowCreateModal(false);
				toast.success('Detection analytic created successfully');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to create analytic');
			},
		}
	);

	const executeAnalyticMutation = useMutation(
		(analyticId) => analyticsService.executeAnalytic(analyticId),
		{
			onSuccess: () => {
				toast.success('Analytic executed successfully');
				queryClient.invalidateQueries('analytics');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to execute analytic');
			},
		}
	);

	const toggleAnalyticMutation = useMutation(
		({ analyticId, isActive }) => {
			return isActive
				? analyticsService.deactivateAnalytic(analyticId)
				: analyticsService.activateAnalytic(analyticId);
		},
		{
			onSuccess: (_, { isActive }) => {
				toast.success(`Analytic ${isActive ? 'deactivated' : 'activated'} successfully`);
				queryClient.invalidateQueries('analytics');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to toggle analytic');
			},
		}
	);

	const getPlatformColor = (platform) => {
		switch (platform) {
			case 'ELASTIC': return 'bg-yellow-100 text-yellow-800';
			case 'DATABRICKS': return 'bg-blue-100 text-blue-800';
			case 'HYBRID': return 'bg-purple-100 text-purple-800';
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

	const analytics = analyticsData?.data?.data?.content || [];

	return (
		<div className="space-y-6">
			<div className="flex justify-between items-center">
				<h1 className="text-3xl font-bold text-gray-900">Detection Analytics</h1>
				<button
					onClick={() => setShowCreateModal(true)}
					className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
				>
					<Plus className="h-4 w-4 mr-2" />
					Create Analytic
				</button>
			</div>

			{/* Filters */}
			<div className="bg-white p-4 rounded-lg shadow space-y-4">
				<div className="grid grid-cols-1 md:grid-cols-3 gap-4">
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Search</label>
						<input
							type="text"
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							placeholder="Search analytics..."
							value={searchTerm}
							onChange={(e) => setSearchTerm(e.target.value)}
						/>
					</div>
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Platform</label>
						<select
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							value={selectedPlatform}
							onChange={(e) => setSelectedPlatform(e.target.value)}
						>
							<option value="">All Platforms</option>
							<option value="ELASTIC">Elastic</option>
							<option value="DATABRICKS">Databricks</option>
							<option value="HYBRID">Hybrid</option>
						</select>
					</div>
				</div>
			</div>

			{/* Analytics List */}
			<div className="bg-white shadow overflow-hidden sm:rounded-md">
				<ul className="divide-y divide-gray-200">
					{analytics.map((analytic) => (
						<li key={analytic.id} className="px-6 py-4">
							<div className="flex items-center justify-between">
								<div className="flex items-center">
									<BarChart3 className="h-5 w-5 mr-3 text-blue-500" />
									<div className="min-w-0 flex-1">
										<div className="flex items-center space-x-2 mb-1">
											<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getPlatformColor(analytic.platform)}`}>
												{analytic.platform}
											</span>
											<span className={`px-2 py-1 text-xs font-semibold rounded-full ${analytic.isActive ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
												}`}>
												{analytic.isActive ? 'Active' : 'Inactive'}
											</span>
											{analytic.accuracy && (
												<span className="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">
													{analytic.accuracy}% Accuracy
												</span>
											)}
										</div>
										<p className="text-sm font-medium text-gray-900 mb-1">
											{analytic.name}
										</p>
										<p className="text-sm text-gray-600 mb-2">
											{analytic.description}
										</p>
										<div className="flex items-center text-xs text-gray-500 space-x-4">
											<span>Created by: {analytic.createdBy}</span>
											<span>Executions: {analytic.executionCount || 0}</span>
											<span>Alerts: {analytic.alertCount || 0}</span>
											{analytic.lastExecuted && (
												<span>Last run: {new Date(analytic.lastExecuted).toLocaleString()}</span>
											)}
										</div>
									</div>
								</div>
								<div className="flex space-x-1">
									<button
										onClick={() => executeAnalyticMutation.mutate(analytic.id)}
										className="p-1 text-green-600 hover:text-green-800"
										title="Execute now"
										disabled={!analytic.isActive}
									>
										<Play className="h-4 w-4" />
									</button>
									<button
										onClick={() => toggleAnalyticMutation.mutate({
											analyticId: analytic.id,
											isActive: analytic.isActive
										})}
										className={`p-1 ${analytic.isActive ? 'text-red-600 hover:text-red-800' : 'text-green-600 hover:text-green-800'}`}
										title={analytic.isActive ? 'Deactivate' : 'Activate'}
									>
										{analytic.isActive ? <Pause className="h-4 w-4" /> : <Activity className="h-4 w-4" />}
									</button>
									<button className="p-1 text-gray-600 hover:text-gray-800" title="Settings">
										<Settings className="h-4 w-4" />
									</button>
								</div>
							</div>
						</li>
					))}
				</ul>
			</div>

			{/* Create Modal */}
			{showCreateModal && (
				<CreateAnalyticModal
					onClose={() => setShowCreateModal(false)}
					onSubmit={createAnalyticMutation.mutate}
					loading={createAnalyticMutation.isLoading}
				/>
			)}
		</div>
	);
}

function CreateAnalyticModal({ onClose, onSubmit, loading }) {
	const [formData, setFormData] = useState({
		name: '',
		description: '',
		queryText: '',
		platform: '',
		threatIntelligenceId: '',
	});

	const handleSubmit = (e) => {
		e.preventDefault();
		onSubmit(formData);
	};

	return (
		<div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
			<div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
				<div className="mt-3">
					<h3 className="text-lg font-medium text-gray-900 mb-4">Create Detection Analytic</h3>
					<form onSubmit={handleSubmit} className="space-y-4">
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
							<input
								type="text"
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.name}
								onChange={(e) => setFormData({ ...formData, name: e.target.value })}
							/>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
							<textarea
								required
								rows={2}
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.description}
								onChange={(e) => setFormData({ ...formData, description: e.target.value })}
							/>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Platform</label>
							<select
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.platform}
								onChange={(e) => setFormData({ ...formData, platform: e.target.value })}
							>
								<option value="">Select Platform</option>
								<option value="ELASTIC">Elastic</option>
								<option value="DATABRICKS">Databricks</option>
								<option value="HYBRID">Hybrid</option>
							</select>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Query</label>
							<textarea
								required
								rows={4}
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500 font-mono text-sm"
								placeholder="Enter your detection query..."
								value={formData.queryText}
								onChange={(e) => setFormData({ ...formData, queryText: e.target.value })}
							/>
						</div>
						<div className="flex justify-end space-x-3 pt-4">
							<button
								type="button"
								onClick={onClose}
								className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
							>
								Cancel
							</button>
							<button
								type="submit"
								disabled={loading}
								className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
							>
								{loading ? 'Creating...' : 'Create Analytic'}
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
}

export default Analytics;