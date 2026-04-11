import { useState } from 'react';
import api from '../api/axiosConfig';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';

const Profile = () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const [budget, setBudget] = useState('');
    const navigate = useNavigate();

    const handleSave = async (e) => {
        e.preventDefault();
        try {
            const now = new Date();
            await api.post('/budgets', {
                month: now.getMonth() + 1,
                year: now.getFullYear(),
                budget_limit: parseFloat(budget)
            });
            toast.success('Budget limit correctly applied!');
        } catch (err) {
            toast.error('Failed to set budget');
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/login');
    };

    return (
        <div className="p-8 max-w-2xl mx-auto mt-10 space-y-6">
            <h1 className="text-3xl font-bold text-gray-800 mb-6">Profile Settings</h1>

            <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm flex items-center gap-6">
                <div className="w-20 h-20 rounded-full bg-[#2da57f] text-white flex items-center justify-center text-3xl font-bold">
                    {user.firstName ? user.firstName[0].toUpperCase() : 'U'}
                </div>
                <div>
                    <h2 className="text-xl font-bold text-gray-800">{user.firstName} {user.lastName}</h2>
                    <p className="text-gray-500 font-medium">{user.email}</p>
                </div>
            </div>

            <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                <h2 className="text-lg font-bold text-gray-800 mb-4">Budget Management</h2>
                <form onSubmit={handleSave} className="space-y-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">Monthly Budget Limit (₱)</label>
                        <input required type="number" step="0.01" min="1" value={budget} onChange={e => setBudget(e.target.value)} className="w-full outline-none border border-gray-200 rounded-xl p-3 focus:ring-2 focus:ring-[#2da57f] transition-all bg-gray-50 focus:bg-white" placeholder="e.g. 5000.00" />
                    </div>
                    <button type="submit" className="w-full py-3.5 rounded-xl bg-[#2da57f] shadow-lg shadow-teal-200 text-white font-bold hover:bg-[#258d6b] transition-all active:scale-95">Save Changes</button>
                    <button type="button" className="w-full py-3.5 rounded-xl border border-gray-200 text-gray-700 font-bold hover:bg-gray-50 transition-colors">Reset Monthly Budget</button>
                </form>
            </div>

            <button onClick={handleLogout} className="mt-8 w-full py-3.5 rounded-xl bg-red-50 text-red-600 font-bold hover:bg-red-100 transition-colors">
                Logout
            </button>
        </div>
    );
};
export default Profile;
