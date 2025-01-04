import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  useTheme,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useWeb3React } from '@web3-react/core';
import { injectedConnector, shortenAddress } from '../utils/web3';
import NotificationBell from './NotificationBell';

const Navbar: React.FC = () => {
  const theme = useTheme();
  const navigate = useNavigate();
  const { activate, active, account, deactivate } = useWeb3React();

  const connectWallet = async () => {
    try {
      await activate(injectedConnector);
    } catch (error) {
      console.error('Failed to connect wallet:', error);
    }
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography
          variant="h6"
          component="div"
          sx={{ cursor: 'pointer' }}
          onClick={() => navigate('/')}
        >
          CarChain
        </Typography>
        
        <Box sx={{ flexGrow: 1, display: 'flex', gap: 2, ml: 4 }}>
          <Button color="inherit" onClick={() => navigate('/cars')}>
            Available Cars
          </Button>
          {active && (
            <>
              <Button color="inherit" onClick={() => navigate('/my-rentals')}>
                My Rentals
              </Button>
              <Button color="inherit" onClick={() => navigate('/register-car')}>
                Register Car
              </Button>
            </>
          )}
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          {active && <NotificationBell />}
          {!active ? (
            <Button
              color="inherit"
              variant="outlined"
              onClick={connectWallet}
              sx={{ borderColor: 'white', '&:hover': { borderColor: 'white' } }}
            >
              Connect Wallet
            </Button>
          ) : (
            <Button
              color="inherit"
              variant="outlined"
              onClick={() => deactivate()}
              sx={{ borderColor: 'white', '&:hover': { borderColor: 'white' } }}
            >
              {shortenAddress(account!)}
            </Button>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
