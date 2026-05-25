import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-hot-toast';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Assuming this endpoint exists on the backend
      await axios.post('http://localhost:8080/api/auth/forgot-password', { email });
      toast.success('Password reset link sent to your email');
      navigate('/login');
    } catch (err) {
      toast.error(err.response?.data?.error || 'Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#f8f9fa] py-12 px-4 sm:px-6 lg:px-8 font-sans">
      <div className="w-full max-w-[400px] bg-white p-8 rounded-2xl shadow-sm border border-gray-100">
        
        {/* Logo and Header */}
        <div className="flex flex-col items-center mb-8">
          <div className="bg-[#2da57f] p-3 rounded-xl mb-4 shadow-sm">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.8} stroke="currentColor" className="w-6 h-6 text-white">
              <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 5.25a3 3 0 013 3m3 0a6 6 0 01-7.029 5.912c-.563-.097-1.159.026-1.563.43L10.5 17.25H8.25v2.25H6v2.25H2.25v-2.818c0-.597.237-1.17.659-1.591l6.499-6.499c.404-.404.527-1 .43-1.563A6 6 0 1121.75 8.25z" />
            </svg>
          </div>
          <h2 className="text-xl font-bold text-gray-900">Forgot Password</h2>
          <p className="text-sm text-gray-500 mt-1 text-center">Enter your email and we'll send you a link to reset your password</p>
        </div>

        <form className="space-y-5" onSubmit={handleSubmit}>
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
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-lg text-sm font-medium text-white bg-[#2da57f] hover:bg-[#238264] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#2da57f] transition-colors disabled:opacity-70 mt-6"
          >
            {loading ? 'Sending...' : 'Send Reset Link'}
          </button>
        </form>

        <p className="mt-8 text-center text-xs text-gray-500">
          Remembered your password?{' '}
          <Link to="/login" className="font-semibold text-[#2da57f] hover:text-[#238264] transition-colors">
            Back to Login
          </Link>
        </p>
      </div>
    </div>
  );
};

export default ForgotPassword;
