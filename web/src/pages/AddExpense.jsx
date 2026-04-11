import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';

const AddExpense = () => {
    const [formData, setFormData] = useState({
        title: '',
        amount: '',
        category: 'Food',
        expense_date: new Date().toISOString().split('T')[0],
        notes: '',
        recurring: 'None'
    });
    const navigate = useNavigate();

    useEffect(() => {
        api.get('/dashboard/summary').then(res => {
            if (res.data.data.monthly_budget <= 0) {
                toast.error('Please set your monthly budget limit first!');
                navigate('/profile');
            }
        }).catch(() => { });
    }, [navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!formData.title || !formData.amount || formData.amount <= 0) {
            toast.error('Please enter a valid title and amount > 0');
            return;
        }

        try {
            await api.post('/expenses', {
                title: formData.title,
                amount: parseFloat(formData.amount),
                category: formData.category,
                category_id: formData.category === 'Food' ? 1 : formData.category === 'Transport' ? 2 : formData.category === 'School' ? 3 : formData.category === 'Personal' ? 4 : 5,
                expense_date: formData.expense_date,
                notes: formData.notes
            });
            toast.success('Expense saved!');
            navigate('/dashboard');
        } catch (err) {
            toast.error('Failed to save expense');
        }
    };

    const categories = ['Food', 'Transport', 'School', 'Personal', 'Other'];

    return (
        <div className="p-8 max-w-2xl mx-auto mt-10 bg-white rounded-2xl border border-gray-100 shadow-sm">
            <h1 className="text-2xl font-bold mb-8 text-gray-800 text-center">Add Expense</h1>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Title</label>
                    <input required type="text" value={formData.title} onChange={e => setFormData({ ...formData, title: e.target.value })} className="w-full outline-none border border-gray-200 rounded-xl p-3 focus:ring-2 focus:ring-[#2da57f] transition-all bg-gray-50 focus:bg-white" placeholder="e.g. Lunch" />
                </div>
                <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Amount (₱)</label>
                    <input required type="number" step="0.01" min="0.01" value={formData.amount} onChange={e => setFormData({ ...formData, amount: e.target.value })} className="w-full outline-none border border-gray-200 rounded-xl p-3 focus:ring-2 focus:ring-[#2da57f] transition-all bg-gray-50 focus:bg-white" placeholder="0.00" />
                </div>
                <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-3">Category</label>
                    <div className="flex gap-3 overflow-x-auto pb-2">
                        {categories.map(c => (
                            <button type="button" key={c} onClick={() => setFormData({ ...formData, category: c })}
                                className={`px-5 py-2.5 rounded-xl border text-sm font-medium transition-all whitespace-nowrap ${formData.category === c ? 'border-[#2da57f] bg-[#eaf6f2] text-[#2da57f] shadow-sm' : 'border-gray-200 text-gray-600 hover:bg-gray-50'}`}
                            >
                                {c}
                            </button>
                        ))}
                    </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">Date</label>
                        <input type="date" required value={formData.expense_date} onChange={e => setFormData({ ...formData, expense_date: e.target.value })} className="w-full outline-none border border-gray-200 rounded-xl p-3 focus:ring-2 focus:ring-[#2da57f] transition-all bg-gray-50 focus:bg-white" />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">Recurring</label>
                        <select value={formData.recurring} onChange={e => setFormData({ ...formData, recurring: e.target.value })} className="w-full outline-none border border-gray-200 rounded-xl p-3 focus:ring-2 focus:ring-[#2da57f] transition-all bg-gray-50 focus:bg-white">
                            <option>None</option>
                            <option>Daily</option>
                            <option>Weekly</option>
                            <option>Monthly</option>
                        </select>
                    </div>
                </div>
                <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-2">Notes (optional)</label>
                    <textarea rows="3" value={formData.notes} onChange={e => setFormData({ ...formData, notes: e.target.value })} className="w-full outline-none border border-gray-200 rounded-xl p-3 focus:ring-2 focus:ring-[#2da57f] transition-all bg-gray-50 focus:bg-white" placeholder="Add a note..."></textarea>
                </div>
                <div className="pt-4 flex gap-4">
                    <button type="button" onClick={() => navigate('/dashboard')} className="flex-1 px-4 py-3.5 rounded-xl border border-gray-200 text-gray-700 font-bold hover:bg-gray-50 transition-colors">Cancel</button>
                    <button type="submit" className="flex-1 px-4 py-3.5 rounded-xl bg-[#2da57f] text-white font-bold hover:bg-[#258d6b] hover:shadow-lg transition-all active:scale-95">Save Expense</button>
                </div>
            </form>
        </div>
    );
};
export default AddExpense;
