import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import StudentDashboard from './pages/StudentDashboard';
import StudentCertificates from './pages/StudentCertificates';
import FacultyReview from './pages/FacultyReview';
import PublicPortfolio from './pages/PublicPortfolio';

function ProtectedRoute({ children, requiredRole }) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center text-slate-500">
        <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user.role !== requiredRole && user.role !== 'ROLE_ADMIN') {
    return <Navigate to="/" replace />;
  }

  return children;
}

function HomeRedirect() {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'ROLE_STUDENT') return <Navigate to="/student/dashboard" replace />;
  if (user.role === 'ROLE_FACULTY' || user.role === 'ROLE_ADMIN') return <Navigate to="/faculty/review" replace />;
  return <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen flex flex-col bg-slate-50">
          <Navbar />
          <div className="flex-1">
            <Routes>
              {/* Home & Auth */}
              <Route path="/" element={<HomeRedirect />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Student Routes */}
              <Route
                path="/student/dashboard"
                element={
                  <ProtectedRoute requiredRole="ROLE_STUDENT">
                    <StudentDashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/student/certificates"
                element={
                  <ProtectedRoute requiredRole="ROLE_STUDENT">
                    <StudentCertificates />
                  </ProtectedRoute>
                }
              />

              {/* Faculty Routes */}
              <Route
                path="/faculty/review"
                element={
                  <ProtectedRoute requiredRole="ROLE_FACULTY">
                    <FacultyReview />
                  </ProtectedRoute>
                }
              />

              {/* Public Portfolio Route */}
              <Route path="/portfolio/:rollNo" element={<PublicPortfolio />} />

              {/* Fallback */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </div>
        </div>
      </Router>
    </AuthProvider>
  );
}
