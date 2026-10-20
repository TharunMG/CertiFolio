import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldCheck, GraduationCap, UserCheck, AlertCircle, ArrowRight } from 'lucide-react';

export default function Register() {
  const [role, setRole] = useState('STUDENT'); // STUDENT or FACULTY
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [rollNumber, setRollNumber] = useState('');
  const [department, setDepartment] = useState('CSE');
  const [yearOfStudy, setYearOfStudy] = useState(2);
  const [cgpa, setCgpa] = useState(8.5);
  const [githubUrl, setGithubUrl] = useState('');
  const [linkedinUrl, setLinkedinUrl] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const payload = {
        email: email.trim(),
        password,
        role,
        fullName: fullName.trim(),
        rollNumber: role === 'STUDENT' ? rollNumber.trim().toUpperCase() : null,
        department: role === 'STUDENT' ? department : null,
        yearOfStudy: role === 'STUDENT' ? Number(yearOfStudy) : null,
        cgpa: role === 'STUDENT' ? Number(cgpa) : null,
        githubUrl: role === 'STUDENT' ? githubUrl.trim() : null,
        linkedinUrl: role === 'STUDENT' ? linkedinUrl.trim() : null,
      };

      const user = await register(payload);
      if (user.role === 'ROLE_STUDENT') {
        navigate('/student/dashboard');
      } else {
        navigate('/faculty/review');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please check inputs.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center p-4 bg-gradient-to-b from-slate-50 to-blue-50/30">
      <div className="w-full max-w-lg">
        <div className="bg-white rounded-2xl shadow-xl border border-slate-200/80 p-8 space-y-6">
          <div className="text-center space-y-1">
            <h2 className="text-2xl font-bold tracking-tight text-slate-900">
              Create an Account
            </h2>
            <p className="text-xs text-slate-500">
              Join CertiFolio to register credentials or audit student submissions
            </p>
          </div>

          {/* Role Toggle Selector */}
          <div className="grid grid-cols-2 p-1 bg-slate-100 rounded-xl">
            <button
              type="button"
              onClick={() => setRole('STUDENT')}
              className={`flex items-center justify-center space-x-2 py-2 text-xs font-bold rounded-lg transition-all ${
                role === 'STUDENT'
                  ? 'bg-white text-blue-700 shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <GraduationCap className="w-4 h-4" />
              <span>Student Account</span>
            </button>
            <button
              type="button"
              onClick={() => setRole('FACULTY')}
              className={`flex items-center justify-center space-x-2 py-2 text-xs font-bold rounded-lg transition-all ${
                role === 'FACULTY'
                  ? 'bg-white text-indigo-700 shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <UserCheck className="w-4 h-4" />
              <span>Faculty Reviewer</span>
            </button>
          </div>

          {error && (
            <div className="flex items-center space-x-2 p-3 text-sm text-rose-700 bg-rose-50 border border-rose-200 rounded-xl">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  Email Address *
                </label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="e.g. name@college.edu"
                  className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                  Password *
                </label>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                Full Name *
              </label>
              <input
                type="text"
                required
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="e.g. Arun Kumar"
                className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {role === 'STUDENT' && (
              <>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                      Roll Number *
                    </label>
                    <input
                      type="text"
                      required
                      value={rollNumber}
                      onChange={(e) => setRollNumber(e.target.value)}
                      placeholder="e.g. 23CS042"
                      className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                      Department *
                    </label>
                    <select
                      value={department}
                      onChange={(e) => setDepartment(e.target.value)}
                      className="w-full px-3 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                    >
                      <option value="CSE">CSE</option>
                      <option value="IT">IT</option>
                      <option value="ECE">ECE</option>
                      <option value="AI&DS">AI & DS</option>
                      <option value="EEE">EEE</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                      CGPA *
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      max="10"
                      required
                      value={cgpa}
                      onChange={(e) => setCgpa(e.target.value)}
                      className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                      GitHub Profile URL
                    </label>
                    <input
                      type="url"
                      value={githubUrl}
                      onChange={(e) => setGithubUrl(e.target.value)}
                      placeholder="https://github.com/username"
                      className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider mb-1">
                      LinkedIn Profile URL
                    </label>
                    <input
                      type="url"
                      value={linkedinUrl}
                      onChange={(e) => setLinkedinUrl(e.target.value)}
                      placeholder="https://linkedin.com/in/username"
                      className="w-full px-3.5 py-2 text-sm rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
              </>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center space-x-2 py-2.5 px-4 rounded-xl text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 disabled:opacity-50 transition-all mt-4"
            >
              <span>{loading ? 'Creating Account...' : 'Complete Registration'}</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </form>

          <div className="text-center text-xs text-slate-500">
            Already registered?{' '}
            <Link to="/login" className="font-semibold text-blue-600 hover:text-blue-700">
              Sign In
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
