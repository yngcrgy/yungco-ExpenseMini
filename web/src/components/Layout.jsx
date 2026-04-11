import { Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, PlusCircle, History, User as UserIcon } from 'lucide-react';

const Layout = ({ children }) => {
    const location = useLocation();
    const menuItems = [
        { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
        { path: '/add-expense', label: 'Add Expense', icon: PlusCircle },
        { path: '/history', label: 'History', icon: History },
        { path: '/profile', label: 'Profile', icon: UserIcon },
    ];

    return (
        <div className="flex h-screen bg-gray-50 font-sans text-gray-800">
            <aside className="w-64 bg-white border-r flex flex-col">
                <div className="p-6 flex items-center mb-6">
                    <div className="w-8 h-8 rounded bg-[#2da57f] flex items-center justify-center mr-3 hidden sm:flex">
                        <span className="text-white font-bold">E</span>
                    </div>
                    <h1 className="text-xl font-bold">ExpenseMini</h1>
                </div>
                <nav className="flex-1 space-y-2 px-4">
                    {menuItems.map((item) => {
                        const Icon = item.icon;
                        const isActive = location.pathname === item.path;
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`flex items-center px-4 py-3 rounded-lg transition-colors ${isActive
                                        ? 'bg-[#eaf6f2] text-[#2da57f] font-medium'
                                        : 'text-gray-500 hover:bg-gray-50 hover:text-gray-800'
                                    }`}
                            >
                                <Icon className="w-5 h-5 mr-3 flex-shrink-0" />
                                <span>{item.label}</span>
                            </Link>
                        );
                    })}
                </nav>
            </aside>
            <main className="flex-1 overflow-auto bg-[#fafafa]">
                {children}
            </main>
        </div>
    );
};

export default Layout;
