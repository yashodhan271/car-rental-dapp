import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  CardMedia,
  Typography,
  Button,
  Box,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { useWeb3React } from '@web3-react/core';
import axios from 'axios';
import { ethers } from 'ethers';
import { shortenAddress } from '../utils/web3';

interface Car {
  id: string;
  vinNumber: string;
  make: string;
  model: string;
  year: number;
  rentalPrice: number;
  imageUrl: string;
  isAvailable: boolean;
  ownerAddress: string;
  ipfsDocumentHash: string;
}

const CarDetails: React.FC = () => {
  const { vinNumber } = useParams<{ vinNumber: string }>();
  const [car, setCar] = useState<Car | null>(null);
  const [loading, setLoading] = useState(true);
  const [openRentDialog, setOpenRentDialog] = useState(false);
  const [rentalDays, setRentalDays] = useState('1');
  const [processing, setProcessing] = useState(false);
  const { account, library } = useWeb3React();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCarDetails = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/cars/${vinNumber}`);
        setCar(response.data);
      } catch (error) {
        console.error('Error fetching car details:', error);
      } finally {
        setLoading(false);
      }
    };

    if (vinNumber) {
      fetchCarDetails();
    }
  }, [vinNumber]);

  const handleRent = async () => {
    if (!car || !account || !library) return;

    setProcessing(true);
    try {
      const days = parseInt(rentalDays);
      const totalAmount = ethers.utils.parseEther((car.rentalPrice * days).toString());

      // Create rental on blockchain and in backend
      const rental = {
        vinNumber: car.vinNumber,
        renterAddress: account,
        startTime: new Date(),
        endTime: new Date(Date.now() + days * 24 * 60 * 60 * 1000),
        totalAmount: totalAmount.toString(),
        isActive: true,
      };

      await axios.post('http://localhost:8080/api/rentals', rental);
      navigate('/my-rentals');
    } catch (error) {
      console.error('Error processing rental:', error);
    } finally {
      setProcessing(false);
      setOpenRentDialog(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  if (!car) {
    return (
      <Container>
        <Typography variant="h5">Car not found</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Grid container spacing={4}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardMedia
              component="img"
              height="400"
              image={car.imageUrl || 'https://via.placeholder.com/800x400?text=Car+Image'}
              alt={`${car.make} ${car.model}`}
            />
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h4" gutterBottom>
                {car.make} {car.model}
              </Typography>
              <Typography variant="body1" paragraph>
                Year: {car.year}
              </Typography>
              <Typography variant="body1" paragraph>
                VIN: {car.vinNumber}
              </Typography>
              <Typography variant="body1" paragraph>
                Owner: {shortenAddress(car.ownerAddress)}
              </Typography>
              <Typography variant="h5" color="primary" paragraph>
                {car.rentalPrice} ETH/day
              </Typography>
              
              {car.isAvailable ? (
                <Button
                  variant="contained"
                  color="primary"
                  fullWidth
                  disabled={!account || car.ownerAddress === account}
                  onClick={() => setOpenRentDialog(true)}
                >
                  Rent Now
                </Button>
              ) : (
                <Button variant="contained" disabled fullWidth>
                  Currently Rented
                </Button>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Dialog open={openRentDialog} onClose={() => setOpenRentDialog(false)}>
        <DialogTitle>Rent {car.make} {car.model}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            margin="dense"
            label="Number of Days"
            type="number"
            fullWidth
            value={rentalDays}
            onChange={(e) => setRentalDays(e.target.value)}
            inputProps={{ min: 1 }}
          />
          <Typography variant="body1" sx={{ mt: 2 }}>
            Total Cost: {(car.rentalPrice * parseInt(rentalDays || '0')).toFixed(4)} ETH
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenRentDialog(false)}>Cancel</Button>
          <Button onClick={handleRent} disabled={processing}>
            {processing ? 'Processing...' : 'Confirm Rental'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default CarDetails;
