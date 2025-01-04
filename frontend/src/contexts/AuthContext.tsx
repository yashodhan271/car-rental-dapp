import React, { createContext, useContext, useState, useEffect } from 'react';
import { useWeb3React } from '@web3-react/core';
import axios from 'axios';

interface AuthContextType {
  token: string | null;
  login: () => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { account, library, active } = useWeb3React();
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!token);

  useEffect(() => {
    if (!active || !account) {
      logout();
    }
  }, [active, account]);

  const login = async () => {
    if (!account || !library) {
      throw new Error('Wallet not connected');
    }

    try {
      // Get nonce
      const nonceResponse = await axios.post(
        'http://localhost:8080/api/auth/nonce',
        null,
        { params: { walletAddress: account } }
      );
      const nonce = nonceResponse.data.nonce;

      // Create message to sign
      const message = `Please sign this nonce: ${nonce}`;

      // Request signature from wallet
      const signature = await library.getSigner(account).signMessage(message);

      // Verify signature and get token
      const verifyResponse = await axios.post(
        'http://localhost:8080/api/auth/verify',
        null,
        { 
          params: { 
            walletAddress: account,
            signature: signature
          }
        }
      );

      const { token: newToken } = verifyResponse.data;
      setToken(newToken);
      localStorage.setItem('token', newToken);
      setIsAuthenticated(true);

      // Set default authorization header for all future requests
      axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;

    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const logout = () => {
    setToken(null);
    setIsAuthenticated(false);
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ token, login, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
