import { useEffect, useState } from 'react';
import { PieChart, Pie, Cell, Tooltip, BarChart, Bar, XAxis, ResponsiveContainer } from 'recharts';
import { ShoppingBag, Coffee, Bus, TrendingUp } from 'lucide-react';
import api from '../api/axiosConfig';
import toast from 'react-hot-toast';
import { Link, useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const [summary, setSummary] = useState(null);
    const [recent, setRecent] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const fetchDashboard = async () => {
        try {
            setLoading(true);
            const [sumRes, expRes] = await Promise.all([
                api.get('/dashboard/summary'),
                api.get('/expenses')
            ]);
            setSummary(sumRes.data.data);
            setRecent(expRes.data.data.slice(0, 5)); // First 5
        } catch (e) {
            toast.error('Failed to load dashboard data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboard();
    }, []);

    const handleQuickAdd = async (category, amount, title) => {
        if (summary?.monthly_budget <= 0) {
            toast.error('You need to set a monthly budget first!');
            navigate('/profile');
            return;
        }

        try {
            await api.post('/expenses', {
                title,
                amount,
                category_id: category === 'Food' ? 1 : category === 'Transport' ? 2 : 5,
                category: category,
                expense_date: new Date().toISOString().split('T')[0]
            });
            toast.success(`Added ${title} (₱${amount})`);
            fetchDashboard();
        } catch (e) {
            toast.error('Quick add failed');
        }
    };

    if (loading) return <div className="p-8 text-gray-500">Loading dashboard...</div>;

    const budget = summary?.monthly_budget || 0;
    const spent = summary?.total_expenses || 0;
    const overBudget = spent > budget;
    const progress = budget > 0 ? Math.min((spent / budget) * 100, 100) : 0;

    // Dynamic Chart Data based on actual expenses
    const categoryMap = {};
    recent.forEach(exp => {
        const c = exp.category || 'Other';
        categoryMap[c] = (categoryMap[c] || 0) + exp.amount;
    });
    const pieData = Object.keys(categoryMap).map(k => ({ name: k, value: categoryMap[k] }));
    if (pieData.length === 0) pieData.push({ name: 'None', value: 1, color: '#e5e7eb' });

    const COLORS = ['#f87171', '#fbbf24', '#60a5fa', '#34d399', '#a78bfa'];

    const barData = [{ name: 'Current', amount: spent || 0 }];

    return (
        <div className="p-8 max-w-5xl mx-auto space-y-6 flex flex-col pt-10">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold text-gray-800">Dashboard</h1>
                <Link to="/add-expense" className="bg-[#2da57f] text-white px-5 py-2 rounded-lg font-medium flex items-center shadow-lg shadow-teal-200 transition hover:bg-[#258d6b]">
                    <span className="mr-2 text-lg leading-none">+</span> Add
                </Link>
            </div>

            <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
                <div className="flex justify-between mb-2">
                    <span className="text-gray-500 font-medium">Monthly Budget</span>
                    {overBudget && <span className="text-red-500 text-sm font-semibold">₱{(spent - budget).toFixed(2)} over budget</span>}
                </div>
                <div className="flex items-baseline mb-5 text-gray-800">
                    <span className="text-4xl font-bold tracking-tight">₱{spent.toFixed(2)}</span>
                    <span className="text-gray-400 ml-2 font-medium">/ ₱{budget.toFixed(2)}</span>
                </div>
                <div className="h-3 w-full bg-gray-100 rounded-full overflow-hidden">
                    <div className={`h-full transition-all duration-500 ${overBudget ? 'bg-red-500' : 'bg-[#e43e3e]'}`} style={{ width: `${progress}%`, backgroundColor: overBudget ? '' : '#ef4444' }} />
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-center">
                    <h3 className="text-md font-semibold mb-2 text-gray-800">By Category</h3>
                    <div className="h-56">
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie data={pieData} innerRadius={60} outerRadius={85} dataKey="value" stroke="none" paddingAngle={3}>
                                    {pieData.map((e, index) => <Cell key={index} fill={e.color || COLORS[index % COLORS.length]} />)}
                                </Pie>
                                <Tooltip cursor={{ fill: 'transparent' }} />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex flex-col justify-center">
                    <h3 className="text-md font-semibold mb-2 text-gray-800">Weekly Trend</h3>
                    <div className="h-56">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={barData}>
                                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#9ca3af', fontSize: 12 }} />
                                <Tooltip cursor={{ fill: '#f3f4f6' }} />
                                <Bar dataKey="amount" fill="#2da57f" radius={[4, 4, 0, 0]} barSize={80} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </div>

            <div className="grid grid-cols-3 gap-6">
                <div className="bg-white p-5 rounded-2xl border border-gray-100 flex flex-col items-center justify-center shadow-sm">
                    <p className="text-gray-400 text-sm mb-1 font-medium">Avg/Day</p>
                    <p className="font-bold text-xl text-gray-800">₱{summary?.avg_daily_spending || 0}</p>
                </div>
                <div className="bg-white p-5 rounded-2xl border border-gray-100 flex flex-col items-center justify-center shadow-sm">
                    <p className="text-gray-400 text-sm mb-1 font-medium">Highest</p>
                    <p className="font-bold text-xl text-gray-800">₱{recent.length ? Math.max(...recent.map(e => e.amount)) : 0}</p>
                </div>
                <div className="bg-white p-5 rounded-2xl border border-gray-100 flex flex-col items-center justify-center shadow-sm">
                    <p className="text-gray-400 text-sm mb-1 font-medium">Top</p>
                    <p className="font-bold text-xl text-gray-800">{summary?.top_category || '-'}</p>
                </div>
            </div>

            <div>
                <h3 className="font-bold text-gray-800 mb-4 text-lg">Quick Add</h3>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <button onClick={() => handleQuickAdd('Food', 150, 'Coffee')} className="bg-white border border-gray-200 rounded-2xl p-4 flex flex-col items-center justify-center hover:border-[#2da57f] hover:shadow-md transition-all active:scale-95 group">
                        <Coffee className="text-[#2da57f] mb-2 group-hover:scale-110 transition-transform" size={24} />
                        <span className="text-sm font-semibold text-gray-700">Coffee</span>
                        <span className="text-xs text-gray-400 font-medium">₱150</span>
                    </button>
                    <button onClick={() => handleQuickAdd('Transport', 50, 'Transpo')} className="bg-white border border-gray-200 rounded-2xl p-4 flex flex-col items-center justify-center hover:border-[#2da57f] hover:shadow-md transition-all active:scale-95 group">
                        <Bus className="text-[#2da57f] mb-2 group-hover:scale-110 transition-transform" size={24} />
                        <span className="text-sm font-semibold text-gray-700">Transpo</span>
                        <span className="text-xs text-gray-400 font-medium">₱50</span>
                    </button>
                    <button onClick={() => handleQuickAdd('Food', 120, 'Lunch')} className="bg-white border border-gray-200 rounded-2xl p-4 flex flex-col items-center justify-center hover:border-[#2da57f] hover:shadow-md transition-all active:scale-95 group">
                        <ShoppingBag className="text-[#2da57f] mb-2 group-hover:scale-110 transition-transform" size={24} />
                        <span className="text-sm font-semibold text-gray-700">Lunch</span>
                        <span className="text-xs text-gray-400 font-medium">₱120</span>
                    </button>
                    <button onClick={() => handleQuickAdd('Food', 80, 'Snacks')} className="bg-white border border-gray-200 rounded-2xl p-4 flex flex-col items-center justify-center hover:border-[#2da57f] hover:shadow-md transition-all active:scale-95 group">
                        <ShoppingBag className="text-[#2da57f] mb-2 group-hover:scale-110 transition-transform" size={24} />
                        <span className="text-sm font-semibold text-gray-700">Snacks</span>
                        <span className="text-xs text-gray-400 font-medium">₱80</span>
                    </button>
                </div>
            </div>

            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm mb-10">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="font-bold text-gray-800 text-lg">Recent Expenses</h3>
                    <Link to="/history" className="text-[#2da57f] text-sm font-semibold hover:underline">View all &rarr;</Link>
                </div>
                <div className="space-y-1">
                    {recent.map(e => (
                        <div key={e.expense_id} className="flex justify-between items-center py-3 border-b border-gray-50 last:border-0 hover:bg-gray-50 rounded-lg px-2 transition-colors">
                            <div className="flex items-center gap-3">
                                <div className="bg-gray-100 p-2 rounded-lg">
                                    <ShoppingBag size={18} className="text-gray-500" />
                                </div>
                                <div className="flex flex-col">
                                    <span className="font-semibold text-gray-800">{e.title}</span>
                                    <span className="text-xs text-gray-400 font-medium">{e.expense_date}</span>
                                </div>
                            </div>
                            <span className="font-bold text-gray-800">-₱{parseFloat(e.amount).toFixed(2)}</span>
                        </div>
                    ))}
                    {recent.length === 0 && <p className="text-gray-400 text-sm italic">No expenses recorded yet.</p>}
                </div>
            </div>
        </div>
    )
};

export default Dashboard;
