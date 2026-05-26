import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { toast } from 'react-hot-toast';
import { Trash2, Plus, Edit } from 'lucide-react';

const AdminPortal = () => {
  const [users, setUsers] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [stats, setStats] = useState({ total_users: 0, total_expenses_count: 0, total_system_money: 0 });
  const [activeTab, setActiveTab] = useState('users');
  const [loading, setLoading] = useState(true);

  const [newCategoryName, setNewCategoryName] = useState('');
  const [newCategoryDesc, setNewCategoryDesc] = useState('');

  // Confirmation modal state
  const [showConfirm, setShowConfirm] = useState(false);
  const [confirmConfig, setConfirmConfig] = useState({ title: '', message: '', onConfirm: () => {} });

  useEffect(() => {
    fetchStats();
    fetchData();
  }, [activeTab]);

  const fetchStats = async () => {
    try {
      const { data } = await api.get('/admin/stats');
      setStats(data.data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchData = async () => {
    setLoading(true);
    try {
      if (activeTab === 'users') {
        const { data } = await api.get('/admin/users');
        setUsers(data.data);
      } else if (activeTab === 'expenses') {
        const { data } = await api.get('/admin/expenses');
        setExpenses(data.data);
      } else if (activeTab === 'categories') {
        const { data } = await api.get('/admin/categories');
        setCategories(data.data);
      }
    } catch (error) {
      console.error(error);
      toast.error('Failed to load admin data');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = (id) => {
    setConfirmConfig({
      title: 'Delete User?',
      message: 'This will permanently delete the user and all their expenses. This action cannot be undone.',
      onConfirm: async () => {
        try {
          await api.delete(`/admin/users/${id}`);
          toast.success('User and their expenses deleted');
          fetchData();
          fetchStats();
        } catch (error) {
          toast.error('Failed to delete user');
        }
        setShowConfirm(false);
      }
    });
    setShowConfirm(true);
  };

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/categories', {
        name: newCategoryName,
        description: newCategoryDesc
      });
      toast.success('Category created');
      setNewCategoryName('');
      setNewCategoryDesc('');
      fetchData();
    } catch (error) {
      toast.error('Failed to create category');
    }
  };

  const handleDeleteCategory = (id) => {
    setConfirmConfig({
      title: 'Delete Category?',
      message: 'Are you sure you want to delete this category?',
      onConfirm: async () => {
        try {
          await api.delete(`/admin/categories/${id}`);
          toast.success('Category deleted');
          fetchData();
        } catch (error) {
          toast.error('Failed to delete category');
        }
        setShowConfirm(false);
      }
    });
    setShowConfirm(true);
  };

  return (
    <div className="p-6">
      {/* Custom Confirmation Modal */}
      {showConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm animate-in fade-in duration-200">
          <div className="bg-white rounded-2xl shadow-2xl max-w-sm w-full p-6 transform animate-in zoom-in-95 duration-200">
            <div className="flex flex-col items-center text-center">
              <div className="bg-red-50 p-3 rounded-full mb-4">
                <Trash2 className="text-red-500" size={24} />
              </div>
              <h3 className="text-xl font-bold text-gray-900 mb-2">{confirmConfig.title}</h3>
              <p className="text-gray-500 text-sm mb-6">{confirmConfig.message}</p>
              
              <div className="flex gap-3 w-full">
                <button
                  onClick={() => setShowConfirm(false)}
                  className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-gray-700 font-semibold hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={confirmConfig.onConfirm}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-red-500 text-white font-semibold hover:bg-red-600 shadow-lg shadow-red-100 transition-all active:scale-95"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Admin Portal</h1>
        <div className="flex bg-gray-200 rounded-lg p-1">
          <button
            onClick={() => setActiveTab('users')}
            className={`px-4 py-2 rounded-md ${activeTab === 'users' ? 'bg-white shadow text-blue-600' : 'text-gray-600'}`}
          >
            Users
          </button>
          <button
            onClick={() => setActiveTab('expenses')}
            className={`px-4 py-2 rounded-md ${activeTab === 'expenses' ? 'bg-white shadow text-blue-600' : 'text-gray-600'}`}
          >
            Master Ledger
          </button>
          <button
            onClick={() => setActiveTab('categories')}
            className={`px-4 py-2 rounded-md ${activeTab === 'categories' ? 'bg-white shadow text-blue-600' : 'text-gray-600'}`}
          >
            Categories
          </button>
        </div>
      </div>

      {/* System Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-xl shadow p-6 border-l-4 border-blue-500">
          <h3 className="text-gray-500 text-sm font-medium">Total Users</h3>
          <p className="text-3xl font-bold text-gray-800 mt-2">{stats.total_users}</p>
        </div>
        <div className="bg-white rounded-xl shadow p-6 border-l-4 border-green-500">
          <h3 className="text-gray-500 text-sm font-medium">Total Expenses</h3>
          <p className="text-3xl font-bold text-gray-800 mt-2">{stats.total_expenses_count}</p>
        </div>
        <div className="bg-white rounded-xl shadow p-6 border-l-4 border-purple-500">
          <h3 className="text-gray-500 text-sm font-medium">System Volume Tracked</h3>
          <p className="text-3xl font-bold text-gray-800 mt-2">₱{parseFloat(stats.total_system_money || 0).toFixed(2)}</p>
        </div>
      </div>

      {loading ? (
        <div className="text-center py-10">Loading...</div>
      ) : activeTab === 'users' ? (
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="p-4 font-medium text-gray-600">ID</th>
                <th className="p-4 font-medium text-gray-600">Name</th>
                <th className="p-4 font-medium text-gray-600">Email</th>
                <th className="p-4 font-medium text-gray-600">Role</th>
                <th className="p-4 font-medium text-gray-600 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id} className="border-b hover:bg-gray-50">
                  <td className="p-4">{user.id}</td>
                  <td className="p-4">{user.firstName} {user.lastName}</td>
                  <td className="p-4">{user.email}</td>
                  <td className="p-4">
                    <span className={`px-2 py-1 rounded text-xs ${user.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'}`}>
                      {user.role}
                    </span>
                  </td>
                  <td className="p-4 text-right">
                    {user.role !== 'ADMIN' && (
                      <button 
                        onClick={() => handleDeleteUser(user.id)}
                        className="text-red-500 hover:text-red-700 p-2 rounded hover:bg-red-50"
                        title="Delete User & Expenses"
                      >
                        <Trash2 size={18} />
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr><td colSpan="5" className="p-4 text-center text-gray-500">No users found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      ) : activeTab === 'expenses' ? (
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="p-4 font-medium text-gray-600">ID</th>
                <th className="p-4 font-medium text-gray-600">User Email</th>
                <th className="p-4 font-medium text-gray-600">Title</th>
                <th className="p-4 font-medium text-gray-600">Amount</th>
                <th className="p-4 font-medium text-gray-600">Category</th>
                <th className="p-4 font-medium text-gray-600">Date</th>
              </tr>
            </thead>
            <tbody>
              {expenses.map(exp => (
                <tr key={exp.expense_id} className="border-b hover:bg-gray-50">
                  <td className="p-4">{exp.expense_id}</td>
                  <td className="p-4 text-blue-600">{exp.user_email}</td>
                  <td className="p-4">
                    <div>
                      <span>{exp.title}</span>
                      {exp.notes && <p className="text-xs text-gray-500 mt-0.5">{exp.notes}</p>}
                    </div>
                  </td>
                  <td className="p-4 font-semibold">₱{exp.amount}</td>
                  <td className="p-4">{exp.category}</td>
                  <td className="p-4">{exp.expense_date}</td>
                </tr>
              ))}
              {expenses.length === 0 && (
                <tr><td colSpan="6" className="p-4 text-center text-gray-500">No expenses found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="md:col-span-2 bg-white rounded-xl shadow overflow-hidden">
            <table className="w-full text-left">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="p-4 font-medium text-gray-600">ID</th>
                  <th className="p-4 font-medium text-gray-600">Name</th>
                  <th className="p-4 font-medium text-gray-600">Description</th>
                  <th className="p-4 font-medium text-gray-600 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {categories.map(cat => (
                  <tr key={cat.id} className="border-b hover:bg-gray-50">
                    <td className="p-4">{cat.id}</td>
                    <td className="p-4 font-medium">{cat.name}</td>
                    <td className="p-4 text-gray-500">{cat.description}</td>
                    <td className="p-4 text-right">
                      <button 
                        onClick={() => handleDeleteCategory(cat.id)}
                        className="text-red-500 hover:text-red-700 p-2 rounded hover:bg-red-50"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          
          <div className="bg-white rounded-xl shadow p-6 h-fit">
            <h3 className="text-xl font-bold mb-4">Add New Category</h3>
            <form onSubmit={handleCreateCategory}>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input 
                  type="text" 
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                  className="w-full p-2 border rounded-md"
                  required
                />
              </div>
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea 
                  value={newCategoryDesc}
                  onChange={(e) => setNewCategoryDesc(e.target.value)}
                  className="w-full p-2 border rounded-md"
                  rows="3"
                />
              </div>
              <button 
                type="submit" 
                className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 flex justify-center items-center gap-2"
              >
                <Plus size={18} /> Create Category
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPortal;
