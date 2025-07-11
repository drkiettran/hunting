import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { Shield, Plus, Search, Filter, Eye, Edit, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { threatIntelService } from '../services/api';

function ThreatIntelligence() {
	const [showCreateModal, setShowCreateModal] = useState(false);
	const [selectedThreatType, setSelectedThreatType] = useState('');
	const [selectedSeverity, setSelectedSeverity] = useState('');
	const [searchTerm, setSearchTerm] = useState('');
	const [page, setPage] = useState(0);
	const queryClient = useQueryClient();

	const { data: threatIntelData, isLoading } = useQuery(
		['threat-intelligence', page, selectedThreatType, selectedSeverity, searchTerm],
		() => threatIntelService.getThreatIntelligence({
			page,
			size: 20,
			threatType: selectedThreatType,
			severity: selectedSeverity,
			search: searchTerm,
		}),
		{ keepPreviousData: true }
	);

	const createThreatIntelMutation = useMutation(
		(data) => threatIntelService.createThreatIntel(data),
		{
			onSuccess: () => {
				queryClient.invalidateQueries('threat-intelligence');
				setShowCreateModal(false);
				toast.success('Threat intelligence created successfully');
			},
			onError: (error) => {
				toast.error(error.response?.data?.message || 'Failed to create threat intelligence');
			},
		}
	);

	const getSeverityColor = (severity) => {
		switch (severity) {
			case 'CRITICAL': return 'bg-red-100 text-red-800';
			case 'HIGH': return 'bg-orange-100 text-orange-800';
			case 'MEDIUM': return 'bg-yellow-100 text-yellow-800';
			case 'LOW': return 'bg-blue-100 text-blue-800';
			default: return 'bg-gray-100 text-gray-800';
		}
	};

	const getThreatTypeColor = (type) => {
		switch (type) {
			case 'APT': return 'bg-red-100 text-red-800';
			case 'MALWARE': return 'bg-purple-100 text-purple-800';
			case 'PHISHING': return 'bg-orange-100 text-orange-800';
			case 'VULNERABILITY': return 'bg-yellow-100 text-yellow-800';
			case 'BOTNET': return 'bg-blue-100 text-blue-800';
			case 'INSIDER_THREAT': return 'bg-indigo-100 text-indigo-800';
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

	const threatIntel = threatIntelData?.data?.data?.content || [];

	return (
		<div className="space-y-6">
			<div className="flex justify-between items-center">
				<h1 className="text-3xl font-bold text-gray-900">Threat Intelligence</h1>
				<button
					onClick={() => setShowCreateModal(true)}
					className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
				>
					<Plus className="h-4 w-4 mr-2" />
					Add Threat Intelligence
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
							placeholder="Search threat intelligence..."
							value={searchTerm}
							onChange={(e) => setSearchTerm(e.target.value)}
						/>
					</div>
					<div>
						<label className="block text-sm font-medium text-gray-700 mb-1">Threat Type</label>
						<select
							className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
							value={selectedThreatType}
							onChange={(e) => setSelectedThreatType(e.target.value)}
						>
							<option value="">All Types</option>
							<option value="APT">APT</option>
							<option value="MALWARE">Malware</option>
							<option value="PHISHING">Phishing</option>
							<option value="VULNERABILITY">Vulnerability</option>
							<option value="BOTNET">Botnet</option>
							<option value="INSIDER_THREAT">Insider Threat</option>
						</select>
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
				</div>
			</div>

			{/* Threat Intelligence List */}
			<div className="bg-white shadow overflow-hidden sm:rounded-md">
				<ul className="divide-y divide-gray-200">
					{threatIntel.map((threat) => (
						<li key={threat.id} className="px-6 py-4">
							<div className="flex items-center justify-between">
								<div className="flex items-center">
									<Shield className="h-5 w-5 mr-3 text-blue-500" />
									<div className="min-w-0 flex-1">
										<div className="flex items-center space-x-2 mb-1">
											<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getThreatTypeColor(threat.threatType)}`}>
												{threat.threatType}
											</span>
											<span className={`px-2 py-1 text-xs font-semibold rounded-full ${getSeverityColor(threat.severity)}`}>
												{threat.severity}
											</span>
										</div>
										<p className="text-sm font-medium text-gray-900 mb-1">
											{threat.description}
										</p>
										<p className="text-sm text-gray-600 mb-2">
											<strong>TTP:</strong> {threat.ttp}
										</p>
										<div className="flex items-center text-xs text-gray-500 space-x-4">
											<span>Source: {threat.source}</span>
											<span>Reported by: {threat.reportedBy}</span>
											<span>Discovered: {new Date(threat.discoveredDate).toLocaleDateString()}</span>
										</div>
									</div>
								</div>
								<div className="flex space-x-1">
									<button className="p-1 text-gray-600 hover:text-gray-800" title="View details">
										<Eye className="h-4 w-4" />
									</button>
									<button className="p-1 text-blue-600 hover:text-blue-800" title="Edit">
										<Edit className="h-4 w-4" />
									</button>
									<button className="p-1 text-red-600 hover:text-red-800" title="Delete">
										<Trash2 className="h-4 w-4" />
									</button>
								</div>
							</div>
						</li>
					))}
				</ul>
			</div>

			{/* Create Modal */}
			{showCreateModal && (
				<CreateThreatIntelModal
					onClose={() => setShowCreateModal(false)}
					onSubmit={createThreatIntelMutation.mutate}
					loading={createThreatIntelMutation.isLoading}
				/>
			)}
		</div>
	);
}

function CreateThreatIntelModal({ onClose, onSubmit, loading }) {
	const [formData, setFormData] = useState({
		source: '',
		threatType: '',
		ttp: '',
		description: '',
		severity: '',
		discoveredDate: new Date().toISOString().split('T')[0],
		reportedBy: '',
	});

	const handleSubmit = (e) => {
		e.preventDefault();
		onSubmit({
			...formData,
			discoveredDate: new Date(formData.discoveredDate).toISOString(),
		});
	};

	return (
		<div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
			<div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
				<div className="mt-3">
					<h3 className="text-lg font-medium text-gray-900 mb-4">Add Threat Intelligence</h3>
					<form onSubmit={handleSubmit} className="space-y-4">
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Source</label>
							<input
								type="text"
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.source}
								onChange={(e) => setFormData({ ...formData, source: e.target.value })}
							/>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Threat Type</label>
							<select
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.threatType}
								onChange={(e) => setFormData({ ...formData, threatType: e.target.value })}
							>
								<option value="">Select Type</option>
								<option value="APT">APT</option>
								<option value="MALWARE">Malware</option>
								<option value="PHISHING">Phishing</option>
								<option value="VULNERABILITY">Vulnerability</option>
								<option value="BOTNET">Botnet</option>
								<option value="INSIDER_THREAT">Insider Threat</option>
							</select>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">TTP</label>
							<textarea
								required
								rows={3}
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.ttp}
								onChange={(e) => setFormData({ ...formData, ttp: e.target.value })}
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
							<label className="block text-sm font-medium text-gray-700 mb-1">Severity</label>
							<select
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.severity}
								onChange={(e) => setFormData({ ...formData, severity: e.target.value })}
							>
								<option value="">Select Severity</option>
								<option value="LOW">Low</option>
								<option value="MEDIUM">Medium</option>
								<option value="HIGH">High</option>
								<option value="CRITICAL">Critical</option>
							</select>
						</div>
						<div>
							<label className="block text-sm font-medium text-gray-700 mb-1">Reported By</label>
							<input
								type="text"
								required
								className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
								value={formData.reportedBy}
								onChange={(e) => setFormData({ ...formData, reportedBy: e.target.value })}
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
								{loading ? 'Creating...' : 'Create'}
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
}

export default ThreatIntelligence;