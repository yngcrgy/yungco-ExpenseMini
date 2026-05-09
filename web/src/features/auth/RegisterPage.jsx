import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        setLoading(true);

        try {
            await axios.post('http://localhost:8080/api/auth/register', formData);
            setSuccess(true);
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        } catch (err) {
            setError(err.response?.data?.error || 'Registration failed. Email might already be in use.');
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#f8f9fa] py-12 px-4 sm:px-6 lg:px-8 font-sans">
                <div className="w-full max-w-[420px] bg-white p-8 rounded-2xl shadow-sm border border-[#2da57f] text-center">
                    <div className="mx-auto flex items-center justify-center h-14 w-14 rounded-full bg-[#e8f5f0] mb-4">
                        <svg className="h-7 w-7 text-[#2da57f]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
                        </svg>
                    </div>
                    <h2 className="text-xl font-bold text-gray-900">Account Created!</h2>
                    <p className="mt-2 text-sm text-gray-500">Redirecting you to login...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#f8f9fa] py-12 px-4 sm:px-6 lg:px-8 font-sans">
            <div className="w-full max-w-[420px] bg-white p-8 rounded-2xl shadow-sm border border-gray-100">

                {/* Logo and Header */}
                <div className="flex flex-col items-center mb-8">
                    <div className="bg-[#2da57f] p-3 rounded-xl mb-4 shadow-sm">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.8} stroke="currentColor" className="w-6 h-6 text-white">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M21 12a2.25 2.25 0 00-2.25-2.25H15a3 3 0 11-6 0H5.25A2.25 2.25 0 003 12m18 0v6a2.25 2.25 0 01-2.25 2.25H5.25A2.25 2.25 0 013 18v-6m18 0V9M3 12V9m18 0a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 9m18 0V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v3" />
                        </svg>
                    </div>
                    <h2 className="text-xl font-bold text-gray-900">Create Account</h2>
                    <p className="text-sm text-gray-500 mt-1">Start tracking your expenses today</p>
                </div>

                <form className="space-y-5" onSubmit={handleRegister}>
                    {error && (
                        <div className="bg-red-50 text-red-500 p-3 rounded-lg text-sm text-center border border-red-100">
                            {error}
                        </div>
                    )}

                    <div>
                        <label htmlFor="email" className="block text-xs font-semibold text-gray-700 mb-1.5">
                            Email
                        </label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            required
                            className="appearance-none block w-full px-3 py-2.5 bg-[#f8fafa] border border-gray-200 rounded-lg text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-[#2da57f] focus:border-[#2da57f] transition-colors"
                            placeholder="you@email.com"
                            value={formData.email}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label htmlFor="firstName" className="block text-xs font-semibold text-gray-700 mb-1.5">
                                First Name
                            </label>
                            <input
                                id="firstName"
                                name="firstName"
                                type="text"
                                required
                                className="appearance-none block w-full px-3 py-2.5 bg-[#f8fafa] border border-gray-200 rounded-lg text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-[#2da57f] focus:border-[#2da57f] transition-colors"
                                placeholder="John"
                                value={formData.firstName}
                                onChange={handleChange}
                            />
                        </div>
                        <div>
                            <label htmlFor="lastName" className="block text-xs font-semibold text-gray-700 mb-1.5">
                                Last Name
                            </label>
                            <input
                                id="lastName"
                                name="lastName"
                                type="text"
                                required
                                className="appearance-none block w-full px-3 py-2.5 bg-[#f8fafa] border border-gray-200 rounded-lg text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-[#2da57f] focus:border-[#2da57f] transition-colors"
                                placeholder="Doe"
                                value={formData.lastName}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-xs font-semibold text-gray-700 mb-1.5">
                            Password
                        </label>
                        <input
                            id="password"
                            name="password"
                            type="password"
                            required
                            minLength={6}
                            className="appearance-none block w-full px-3 py-2.5 bg-[#f8fafa] border border-gray-200 rounded-lg text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-[#2da57f] focus:border-[#2da57f] transition-colors"
                            placeholder="Min 6 characters"
                            value={formData.password}
                            onChange={handleChange}
                        />
                    </div>

                    <div>
                        <label htmlFor="confirmPassword" className="block text-xs font-semibold text-gray-700 mb-1.5">
                            Confirm Password
                        </label>
                        <input
                            id="confirmPassword"
                            name="confirmPassword"
                            type="password"
                            required
                            minLength={6}
                            className="appearance-none block w-full px-3 py-2.5 bg-[#f8fafa] border border-gray-200 rounded-lg text-sm text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-[#2da57f] focus:border-[#2da57f] transition-colors"
                            placeholder="Re-enter password"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg text-sm font-medium text-white bg-[#2da57f] hover:bg-[#238264] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#2da57f] transition-colors disabled:opacity-70 mt-6"
                    >
                        {loading ? 'Creating...' : 'Create Account'}
                    </button>
                </form>

                <p className="mt-6 text-center text-xs text-gray-500">
                    Already have an account?{' '}
                    <Link to="/login" className="font-semibold text-[#2da57f] hover:text-[#238264] transition-colors">
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;
