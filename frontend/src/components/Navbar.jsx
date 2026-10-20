import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Award, BookOpen, CheckCircle2, LogOut, User, Search, ShieldCheck } from 'lucide-react';

export default function Navbar() {
  const { user, logout, isStudent, isFaculty } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-40 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo & Tagline */}
          <Link to="/" className="flex items-center space-x-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-700 via-blue-600 to-indigo-600 flex items-center justify-center text-white shadow-md shadow-blue-500/20 group-hover:scale-105 transition-transform">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xl font-bold tracking-tight text-slate-900 group-hover:text-blue-600 transition-colors">
                Certi<span className="text-blue-600">Folio</span>
              </span>
              <span className="hidden sm:inline-block ml-2 text-xs font-medium text-slate-500 bg-slate-100 px-2 py-0.5 rounded-full border border-slate-200">
                Verified Registry
              </span>
            </div>
          </Link>

          {/* Navigation Links */}
          <nav className="flex items-center space-x-1 sm:space-x-3">
            {isStudent && (
              <>
                <Link
                  to="/student/dashboard"
                  className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                    isActive('/student/dashboard')
                      ? 'bg-blue-50 text-blue-700 font-semibold'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                  }`}
                >
                  Skill Matrix
                </Link>
                <Link
                  to="/student/certificates"
                  className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                    isActive('/student/certificates')
                      ? 'bg-blue-50 text-blue-700 font-semibold'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                  }`}
                >
                  Certificates
                </Link>
                {user?.rollNumber && (
                  <Link
                    to={`/portfolio/${user.rollNumber}`}
                    target="_blank"
                    className="hidden md:inline-flex items-center px-3 py-2 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50"
                  >
                    Public View
                  </Link>
                )}
              </>
            )}

            {isFaculty && (
              <Link
                to="/faculty/review"
                className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActive('/faculty/review')
                    ? 'bg-blue-50 text-blue-700 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                Audit & Talent Search
              </Link>
            )}

            {user ? (
              <div className="flex items-center pl-3 border-l border-slate-200 space-x-3">
                <div className="hidden sm:block text-right">
                  <p className="text-xs font-semibold text-slate-800 leading-tight">
                    {user.fullName || user.email}
                  </p>
                  <p className="text-[11px] font-medium text-blue-600">
                    {user.role === 'ROLE_STUDENT'
                      ? `Student (${user.rollNumber || 'Active'})`
                      : 'Faculty Reviewer'}
                  </p>
                </div>
                <button
                  onClick={handleLogout}
                  title="Sign out"
                  className="p-2 text-slate-500 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <div className="flex items-center space-x-2">
                <Link
                  to="/login"
                  className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-slate-900 hover:bg-slate-50 rounded-lg transition-colors"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg shadow-sm transition-colors"
                >
                  Register
                </Link>
              </div>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
}
