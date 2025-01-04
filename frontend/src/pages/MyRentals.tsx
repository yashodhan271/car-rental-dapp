import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  Box,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
  Chip,
} from '@mui/material';
import { useWeb3React } from '@web3-react/core';
import axios from 'axios';
import { format } from 'date-fns';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import LocationOnIcon from '@mui/icons-material/LocationOn';

interface Rental {
  id: string;
  vinNumber: string;
  startTime: string;
  endTime: string;
  totalAmount: string;
  isActive: boolean;
  gpsTrackingId: string;
  car: {
    make: string;
    model: string;
    year: number;
    imageUrl: string;
  };
}

interface GpsLocation {
  latitude: number;
  longitude: number;
  timestamp: string;
}

const MyRentals: React.FC = () => {
  const { account } = useWeb3React();
  const [activeTab, setActiveTab] = useState(0);
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [gpsLocations, setGpsLocations] = useState<Record<string, GpsLocation>>({});

  useEffect(() => {
    if (account) {
      fetchRentals();
      // Set up polling for GPS locations of active rentals
      const interval = setInterval(() => {
        rentals.forEach((rental: Rental) => {
          if (rental.isActive && rental.gpsTrackingId) {
            fetchGpsLocation(rental.gpsTrackingId);
          }
        });
      }, 30000); // Update every 30 seconds

      return () => clearInterval(interval);
    }
  }, [account, rentals]);

  const fetchRentals = async () => {
    setError(null);
    setLoading(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/rentals/renter/${account}`);
      setRentals(response.data);
      
      // Fetch initial GPS locations for active rentals
      response.data.forEach((rental: Rental) => {
        if (rental.isActive && rental.gpsTrackingId) {
          fetchGpsLocation(rental.gpsTrackingId);
        }
      });
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to fetch rentals';
      console.error('Error fetching rentals:', err);
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const fetchGpsLocation = async (gpsTrackingId: string) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/gps/${gpsTrackingId}`);
      setGpsLocations(prev => ({
        ...prev,
        [gpsTrackingId]: response.data
      }));
    } catch (err: any) {
      console.error(`Error fetching GPS location for ID ${gpsTrackingId}:`, err);
      // Don't set global error for GPS failures
    }
  };

  const handleCompleteRental = async (rentalId: string) => {
    try {
      setError(null);
      await axios.post(`http://localhost:8080/api/rentals/${rentalId}/complete`);
      // Show success message
      setRentals(prev => prev.map(rental => 
        rental.id === rentalId ? { ...rental, isActive: false } : rental
      ));
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to complete rental';
      console.error('Error completing rental:', err);
      setError(errorMessage);
    }
  };

  const openGpsLocation = (latitude: number, longitude: number) => {
    window.open(`https://www.google.com/maps?q=${latitude},${longitude}`, '_blank');
  };

  if (!account) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Alert severity="warning">
          Please connect your wallet to view your rentals.
        </Alert>
      </Container>
    );
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  const activeRentals = rentals.filter(rental => rental.isActive);
  const pastRentals = rentals.filter(rental => !rental.isActive);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        My Rentals
      </Typography>

      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={activeTab} onChange={(_, newValue) => setActiveTab(newValue)}>
          <Tab label={`Active Rentals (${activeRentals.length})`} />
          <Tab label={`Past Rentals (${pastRentals.length})`} />
        </Tabs>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3}>
        {(activeTab === 0 ? activeRentals : pastRentals).map((rental) => (
          <Grid item xs={12} md={6} key={rental.id}>
            <Card 
              sx={{ 
                height: '100%', 
                display: 'flex', 
                flexDirection: 'column',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'scale(1.02)'
                }
              }}
            >
              <CardContent>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <Typography variant="h6">
                      {rental.car.make} {rental.car.model} ({rental.car.year})
                    </Typography>
                    <Typography color="textSecondary" gutterBottom>
                      VIN: {rental.vinNumber}
                    </Typography>
                  </Grid>

                  <Grid item xs={12}>
                    <Box display="flex" gap={1} flexWrap="wrap" mb={2}>
                      <Chip
                        icon={<DirectionsCarIcon />}
                        label={rental.isActive ? 'Active' : 'Completed'}
                        color={rental.isActive ? 'success' : 'default'}
                        sx={{ fontWeight: 'medium' }}
                      />
                      {rental.isActive && rental.gpsTrackingId && gpsLocations[rental.gpsTrackingId] && (
                        <Chip
                          icon={<LocationOnIcon />}
                          label={`Last Updated: ${format(new Date(gpsLocations[rental.gpsTrackingId].timestamp), 'pp')}`}
                          color="primary"
                          onClick={() => openGpsLocation(
                            gpsLocations[rental.gpsTrackingId].latitude,
                            gpsLocations[rental.gpsTrackingId].longitude
                          )}
                          clickable
                        />
                      )}
                    </Box>
                  </Grid>

                  <Grid item xs={12}>
                    <Typography>
                      Start: {format(new Date(rental.startTime), 'PPP')}
                    </Typography>
                    <Typography>
                      End: {format(new Date(rental.endTime), 'PPP')}
                    </Typography>
                    <Typography variant="h6" color="primary" sx={{ mt: 1 }}>
                      Total: {rental.totalAmount} ETH
                    </Typography>
                  </Grid>

                  {rental.isActive && (
                    <Grid item xs={12}>
                      <Button
                        variant="contained"
                        color="primary"
                        fullWidth
                        onClick={() => handleCompleteRental(rental.id)}
                      >
                        Complete Rental
                      </Button>
                    </Grid>
                  )}
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        ))}

        {(activeTab === 0 ? activeRentals : pastRentals).length === 0 && (
          <Grid item xs={12}>
            <Alert severity="info">
              {activeTab === 0 ? 'No active rentals found.' : 'No past rentals found.'}
            </Alert>
          </Grid>
        )}
      </Grid>
    </Container>
  );
};

export default MyRentals;
