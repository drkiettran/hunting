import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { FolderOpen, Plus, Search, Calendar, User, Eye, Edit } from 'lucide-react';
import toast from 'react-hot-toast';
import { caseService } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

function Cases() {
	const [showCreateModal, setShowCreateModal] = useState(false);
	const [selectedPriority, setSelectedPriority] = useState('');
	const [selectedStatus, setSelectedStatus] = useState('');
	const [searchTerm, setSearchTerm] = useState('');
	const [page, setPage] = useState(0);
	const { user } = useAuth();
	const queryClient = useQueryClient();

	const { data: casesData, isLoading } = useQuery(
		['cases', page, selectedPriority, selectedStatus, searchTerm],
		() => caseService.getCases({
			page,
			size: 20,
			priority: selectedPriority,
			status: selectedStatus,
			search: searchTerm,
		}),
		{ keepPreviousData: true }
	);

	const createCaseMutation = useMutation(
		(data) => caseService.createCase(data),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('cases');
				setShowCreateModal(false);
				toast.success('Case created successfully');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to create case');
			},
		}
	);

	const assignCaseMutation = useMutation(
		({ caseId, analystId }) => caseService.assignCase(caseId, analystId),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('cases');
				toast.success('Case assigned successfully');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to assign case');
			},
		}
	);

	const getPriorityColor = (priority) => {
		switch (priority) {
			case 'CRITICAL': return 'bg-red-100 text-red-800';
			case 'HIGH': return 'bg-orange-100 text-orange-800';
			case 'MEDIUM': return 'bg-yellow-100 text-yellow-800';
			case 'LOW': return 'bg-blue-100 text-blue-800';
			default: return 'bg-gray-100 text-gray-800';
		}
	};

	const getStatusColor = (status) => {
		switch (status) {
			case 'OPEN': return 'bg-red-100 text-red-800';
			case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800';
			case 'PENDING_REVIEW': return 'bg-yellow-100 text-yellow-800';
			case 'CLOSED': return 'bg-green-100 text-green-800';
			default: return 'bg-gray-100 text-gray-800';
		}
	};

	const handleAssignToMe = (caseId) => {
		assignCaseMutation.mutate({ caseId, analystId: user.username });
	};

	if (isLoading) {
		return (
			<div className="flex items-center justify-center h-64">
				<div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-500"></div>
			</div>
		);
	}

	const cases = casesData?.data?.data?.content || [];

	return (
		<div className="space-y-6">
			<div className="flex justify-between items-center">
				<h1 className="text-3xl font-bold text-gray-900">Cases</h1>
				<button
					onClick={() => setShowCreateModal(true)}
					className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
				>
					<Plus className="h-4 w-4 mr-2" />
					Create Case
				</button>
			</div>

			{/* Filters */}
			<div className="bg-white p-4 rounded-lg shadow space-y-4">
				<div className="grid grid-cols-1 md:grid-cols-4 gap-4">
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Search</label>
						<input
							type="text"
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							placeholder="Search cases..."
							value={searchTerm}
							onChange={(e) => setSearchTerm(e.target.value)}
						/>
					</div>
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Priority</label>
						<select
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							value={selectedPriority}
							onChange={(e) => setSelectedPriority(e.target.value)}
						>
							<option value="">All Priorities</option>
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
							<option value="OPEN">Open</option>
							<option value="IN_PROGRESS">In Progress</option>
							<option value="PENDING_REVIEW">Pending Review</option>
							<option value="CLOSED">Closed</option>
						</select>
					</div>
				</div>
			</div>

			{/* Cases List */}
			<div className="bg-white shadow overflow-hidden sm:rounded-md">
				<ul className="divide-y divide-gray-200">
					{cases.map((caseItem) => (
						<li key={caseItem.id} className="px-6 py-4">
							<div className="flex items-center justify-between">
								<div className="flex items-center">
									<FolderOpen className="h-5 w-5 mr-3 text-blue-500" />
									<div className="min-w-0 flex-1">
										<div className="flex items-center space-x-2 mb-1">
											<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getPriorityColor(caseItem.priority)}`}>
												{caseItem.priority}
											</span>
											<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(caseItem.status)}`}>
												{caseItem.status}
											</span>
										</div>
										<p className="text-sm font-medium text-gray-900 mb-1">
											{caseItem.title}
										</p>
										<p className="text-sm text-gray-600 mb-2">
											{caseItem.description}
										</p>
										<div className="flex items-center text-xs text-gray-500 space-x-4">
											<div className="flex items-center">
												<User className="h-3 w-3 mr-1" />
												Created by: {caseItem.createdBy}
											</div>
											<div className="flex items-center">
												<Calendar className="h-3 w-3 mr-1" />
												{new Date(caseItem.createdDate).toLocaleDateString()}
											</div>
											{caseItem.assignedTo && (
												<div className="flex items-center">
													<User className="h-3 w-3 mr-1" />
													Assigned to: {caseItem.assignedTo}
												</div>
											)}
											{caseItem.dueDate && (
												<div className="flex items-center">
													<Calendar className="h-3 w-3 mr-1" />
													Due: {new Date(caseItem.dueDate).toLocaleDateString()}
												</div>
											)}
										</div>
										<div className="flex items-center text-xs text-gray-500 space-x-4 mt-1">
											<span>Investigations: {caseItem.investigationCount || 0}</span>
											<span>Tickets: {caseItem.ticketCount || 0}</span>
										</div>
									</div>
								</div>
								<div className="flex space-x-1">
									{!caseItem.assignedTo && caseItem.status === 'OPEN' && (
										<button
											onClick={() => handleAssignToMe(caseItem.id)}
											className="p-1 text-blue-600 hover:text-blue-800"
											title="Assign to me"
										>
											<User className="h-4 w-4" />
										</button>
									)}
									<button className="p-1 text-gray-600 hover:text-gray-800" title="View details">
										<Eye className="h-4 w-4" />
									</button>
									<button className="p-1 text-blue-600 hover:text-blue-800" title="Edit">
										<Edit className="h-4 w-4" />
									</button>
								</div>
							</div>
						</li>
					))}
				</ul>
			</div>

			{/* Create Modal */}
			{showCreateModal && (
				<CreateCaseModal
					onClose={() => setShowCreateModal(false)}
					onSubmit={createCaseMutation.mutate}
					loading={createCaseMutation.isLoading}
				/>
			)}
		</div>
	);
}

function CreateCaseModal({ onClose, onSubmit, loading }) {
	const [formData, setFormData] = useState({
		title: '',
		description: '',
		priority: '',
		riskLevel: '',
		dueDate: '',
	});

	const handleSubmit = (e) => {
		e.preventDefault();
		const submitData = {
			...formData,
			dueDate: formData.dueDate ? new Date(formData.dueDate).toISOString() : null,
		};
		onSubmit(submitData);
	};

	return (
		<div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
			<div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
				<div className="mt-3">
					<h3 className="text-lg font-medium text-gray-900 mb-4">Create New Case</h3>
					<form onSubmit={handleSubmit} className="space-y-4">
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
							<input
								type="text"
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.title}
								onChange={(e) => setFormData({ ...formData, title: e.target.value })}
							/>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
							<textarea
								required
								rows={3}
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.description}
								onChange={(e) => setFormData({ ...formData, description: e.target.value })}
							/>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Priority</label>
							<select
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.priority}
								onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
							>
								<option value="">Select Priority</option>
								<option value="LOW">Low</option>
								<option value="MEDIUM">Medium</option>
								<option value="HIGH">High</option>
								<option value="CRITICAL">Critical</option>
							</select>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Risk Level</label>
							<select
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.riskLevel}
								onChange={(e) => setFormData({ ...formData, riskLevel: e.target.value })}
							>
								<option value="">Select Risk Level</option>
								<option value="LOW">Low</option>
								<option value="MEDIUM">Medium</option>
								<option value="HIGH">High</option>
								<option value="CRITICAL">Critical</option>
							</select>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Due Date</label>
							<input
								type="date"
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.dueDate}
								onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
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
								{loading ? 'Creating...' : 'Create Case'}
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
}

export default Cases;