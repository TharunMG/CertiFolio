import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('certifolio_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem('certifolio_user');
    const savedToken = localStorage.getItem('certifolio_token');
    if (savedUser && savedToken) {
      try {
        setUser(JSON.parse(savedUser));
        setToken(savedToken);
      } catch (e) {
        localStorage.removeItem('certifolio_user');
        localStorage.removeItem('certifolio_token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const res = await api.post('/api/auth/login', { email, password });
    const data = res.data;
    const userData = {
      userId: data.userId,
      email: data.email,
      role: data.role,
      studentProfileId: data.studentProfileId,
      rollNumber: data.rollNumber,
      fullName: data.fullName,
    };
    setUser(userData);
    setToken(data.token);
    localStorage.setItem('certifolio_token', data.token);
    localStorage.setItem('certifolio_user', JSON.stringify(userData));
    return userData;
  };

  const register = async (registerPayload) => {
    const res = await api.post('/api/auth/register', registerPayload);
    const data = res.data;
    const userData = {
      userId: data.userId,
      email: data.email,
      role: data.role,
      studentProfileId: data.studentProfileId,
      rollNumber: data.rollNumber,
      fullName: data.fullName,
    };
    setUser(userData);
    setToken(data.token);
    localStorage.setItem('certifolio_token', data.token);
    localStorage.setItem('certifolio_user', JSON.stringify(userData));
    return userData;
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('certifolio_token');
    localStorage.removeItem('certifolio_user');
  };

  const isStudent = user?.role === 'ROLE_STUDENT';
  const isFaculty = user?.role === 'ROLE_FACULTY' || user?.role === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, isStudent, isFaculty }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
