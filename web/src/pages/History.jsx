import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import toast from 'react-hot-toast';
import { Download, Search, Trash2, Edit2, ShoppingBag, Coffee, Bus, GraduationCap } from 'lucide-react';

const History = () => {
    const [expenses, setExpenses] = useState([]);
    const [filtered, setFiltered] = useState([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [categoryFilter, setCategoryFilter] = useState('All');
    const [editing, setEditing] = useState(null);

    const fetchExpenses = async () => {
        try {
            setLoading(true);
            const res = await api.get('/expenses');
            setExpenses(res.data.data);
            setFiltered(res.data.data);
        } catch (e) {
            toast.error('Failed to load history');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchExpenses();
    }, []);

    useEffect(() => {
        let f = expenses;
        if (search) f = f.filter(e => e.title.toLowerCase().includes(search.toLowerCase()));
        if (categoryFilter !== 'All') f = f.filter(e => e.category === categoryFilter);
        setFiltered(f);
    }, [search, categoryFilter, expenses]);

    const handleDelete = async (id) => {
        if (!window.confirm('Delete expense? This action cannot be undone.')) return;
        try {
            await api.delete(`/expenses/${id}`);
            toast.success('Expense deleted');
            fetchExpenses();
        } catch (e) {
            toast.error('Failed to delete expense');
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/expenses/${editing.expense_id}`, {
                title: editing.title,
                amount: parseFloat(editing.amount),
                category: editing.category,
                category_id: editing.category === 'Food' ? 1 : editing.category === 'Transport' ? 2 : 5
            });
            toast.success('Expense updated');
            setEditing(null);
            fetchExpenses();
        } catch (err) {
            toast.error('Failed to update expense');
        }
    };

    const handleExport = () => {
        if (filtered.length === 0) return toast.error('No data to export');
        const header = "ID,Title,Amount,Category,Date\n";
        const csv = filtered.map(e => `${e.expense_id},${e.title},${e.amount},${e.category},${e.expense_date}`).join('\n');
        const blob = new Blob([header + csv], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'expenses_export.csv';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    };

    const getIcon = (cat) => {
        if (cat === 'Food') return <Coffee size={18} className="text-[#f87171]" />;
        if (cat === 'Transport') return <Bus size={18} className="text-[#60a5fa]" />;
        if (cat === 'School') return <GraduationCap size={18} className="text-[#a78bfa]" />;
        return <ShoppingBag size={18} className="text-[#fbbf24]" />;
    };

    return (
        <div className="p-8 max-w-5xl mx-auto space-y-6 pt-10">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-3xl font-bold text-gray-800">History</h1>
                <button onClick={handleExport} className="flex items-center gap-2 bg-white border border-gray-200 px-5 py-2.5 rounded-xl hover:bg-gray-50 text-gray-700 font-bold transition-colors shadow-sm focus:ring-2 focus:ring-[#2da57f] active:scale-95">
                    <Download size={18} /> Export
                </button>
            </div>

            <div className="flex flex-col md:flex-row gap-4">
                <div className="flex-1 relative">
                    <Search className="absolute left-4 top-3 text-gray-400" size={20} />
                    <input type="text" placeholder="Search expenses..." value={search} onChange={e => setSearch(e.target.value)} className="w-full pl-12 pr-4 py-3 rounded-xl border border-gray-200 outline-none focus:ring-2 focus:ring-[#2da57f] bg-white transition-all shadow-sm font-medium" />
                </div>
                <select value={categoryFilter} onChange={e => setCategoryFilter(e.target.value)} className="py-3 px-4 rounded-xl border border-gray-200 outline-none focus:ring-2 focus:ring-[#2da57f] bg-white transition-all shadow-sm font-bold text-gray-700 min-w-40">
                    <option>All</option>
                    <option>Food</option>
                    <option>Transport</option>
                    <option>School</option>
                    <option>Personal</option>
                    <option>Other</option>
                </select>
            </div>

            <p className="text-sm font-bold text-gray-400">{filtered.length} expenses</p>

            <div className="space-y-3 pb-20">
                {loading && <p className="text-gray-400 font-medium italic">Loading expenses...</p>}
                {!loading && filtered.map(e => (
                    <div key={e.expense_id} className="bg-white rounded-2xl p-4 border border-gray-100 shadow-sm flex justify-between items-center hover:border-[#2da57f] hover:shadow-md transition-all group">
                        <div className="flex items-center gap-4">
                            <div className="bg-gray-50 p-3 rounded-xl border border-gray-100 scale-100 group-hover:scale-110 transition-transform">
                                {getIcon(e.category)}
                            </div>
                            <div>
                                <h3 className="font-bold text-gray-800 text-lg">{e.title}</h3>
                                <p className="text-xs text-gray-400 font-bold">{e.expense_date}</p>
                            </div>
                        </div>
                        <div className="flex items-center gap-6">
                            <span className="font-bold text-gray-800 text-xl">-₱{parseFloat(e.amount).toFixed(2)}</span>
                            <div className="flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                <button onClick={() => setEditing(e)} className="p-2 bg-gray-50 rounded-lg text-gray-400 hover:text-[#2da57f] hover:bg-[#eaf6f2] transition-colors"><Edit2 size={16} /></button>
                                <button onClick={() => handleDelete(e.expense_id)} className="p-2 bg-gray-50 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors"><Trash2 size={16} /></button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {editing && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex justify-center items-center p-4 z-50">
                    <div className="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl transform scale-100 transition-transform">
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-2xl font-bold text-gray-800">Edit Expense</h2>
                            <button onClick={() => setEditing(null)} className="text-gray-400 hover:text-gray-800 text-2xl font-bold">&times;</button>
                        </div>
                        <form onSubmit={handleUpdate} className="space-y-5">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Title</label>
                                <input value={editing.title} onChange={e => setEditing({ ...editing, title: e.target.value })} className="w-full border border-gray-200 rounded-xl p-3 outline-none focus:ring-2 focus:ring-[#2da57f] bg-gray-50 focus:bg-white transition-all font-medium" />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Amount (₱)</label>
                                <input type="number" step="0.01" value={editing.amount} onChange={e => setEditing({ ...editing, amount: e.target.value })} className="w-full border border-gray-200 rounded-xl p-3 outline-none focus:ring-2 focus:ring-[#2da57f] bg-gray-50 focus:bg-white transition-all font-medium" />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Category</label>
                                <select value={editing.category} onChange={e => setEditing({ ...editing, category: e.target.value })} className="w-full border border-gray-200 rounded-xl p-3 outline-none focus:ring-2 focus:ring-[#2da57f] bg-gray-50 focus:bg-white transition-all font-medium">
                                    <option>Food</option>
                                    <option>Transport</option>
                                    <option>School</option>
                                    <option>Personal</option>
                                    <option>Other</option>
                                </select>
                            </div>
                            <div className="pt-4 flex gap-4">
                                <button type="button" onClick={() => setEditing(null)} className="flex-1 py-3.5 rounded-xl border border-gray-200 font-bold text-gray-700 hover:bg-gray-50 transition-colors">Cancel</button>
                                <button type="submit" className="flex-1 py-3.5 rounded-xl bg-[#2da57f] font-bold text-white shadow-lg shadow-teal-200 hover:bg-[#258d6b] transition-all active:scale-95">Save Changes</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};
export default History;
