import React, { createContext, useState, useContext, useEffect } from 'react';

const UserContext = createContext();

export function UserProvider({ children }) {
  const [userId, setUserId] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check if user is already logged in on app load
  useEffect(() => {
    const savedUserId = localStorage.getItem('userId');
    if (savedUserId) {
      try {
        setUserId(JSON.parse(savedUserId));
      } catch (err) {
        console.error('Error parsing user ID:', err);
      }
    }
    setLoading(false);
  }, []);

  const signup = async (name, email, password) => {
    try {
      const response = await fetch('http://localhost:8080/api/users/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, email, password }),
      });

      if (!response.ok) {
        throw new Error('Signup failed');
      }

      const userId = await response.json();

      // Store user ID in localStorage
      localStorage.setItem('userId', JSON.stringify(userId));
      setUserId(userId);

      return userId;
    } catch (error) {
      console.error('Signup error:', error);
      throw error;
    }
  };

  const login = async (email, password) => {
    try {
      const response = await fetch('http://localhost:8080/api/users/signin', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const userId = await response.json();

      // Store user ID in localStorage
      localStorage.setItem('userId', JSON.stringify(userId));
      setUserId(userId);

      return userId;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('userId');
    setUserId(null);
  };

  const value = {
    userId,
    loading,
    signup,
    login,
    logout,
    isAuthenticated: !!userId,
  };

  return <UserContext.Provider value={value}>{children}</UserContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components -- hook is tightly coupled to UserProvider above
export function useUser() {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error('useUser must be used within UserProvider');
  }
  return context;
}
