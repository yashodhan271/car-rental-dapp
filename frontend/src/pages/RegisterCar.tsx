import React, { useState } from 'react';
import {
  Container,
  Typography,
  TextField,
  Button,
  Card,
  CardContent,
  Grid,
  Box,
  Alert,
} from '@mui/material';
import { useWeb3React } from '@web3-react/core';
import axios from 'axios';
import { ethers } from 'ethers';

interface CarFormData {
  vinNumber: string;
  make: string;
  model: string;
  year: string;
  rentalPrice: string;
  imageUrl: string;
}

const RegisterCar: React.FC = () => {
  const { account, library } = useWeb3React();
  const [formData, setFormData] = useState<CarFormData>({
    vinNumber: '',
    make: '',
    model: '',
    year: '',
    rentalPrice: '',
    imageUrl: '',
  });
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!account || !library) {
      setError('Please connect your wallet first');
      return;
    }

    setProcessing(true);
    setError(null);
    setSuccess(false);

    try {
      // Convert rental price to Wei
      const priceInWei = ethers.utils.parseEther(formData.rentalPrice);

      // Create car object for backend
      const carData = {
        vinNumber: formData.vinNumber,
        make: formData.make,
        model: formData.model,
        year: parseInt(formData.year),
        rentalPrice: formData.rentalPrice,
        imageUrl: formData.imageUrl,
        ownerAddress: account,
        isAvailable: true,
      };

      // Register car in backend
      await axios.post('http://localhost:8080/api/cars', carData);
      setSuccess(true);
      
      // Reset form
      setFormData({
        vinNumber: '',
        make: '',
        model: '',
        year: '',
        rentalPrice: '',
        imageUrl: '',
      });
    } catch (error) {
      console.error('Error registering car:', error);
      setError('Failed to register car. Please try again.');
    } finally {
      setProcessing(false);
    }
  };

  if (!account) {
    return (
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Alert severity="warning">
          Please connect your wallet to register a car.
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Typography variant="h4" gutterBottom>
        Register Your Car
      </Typography>

      <Card>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  label="VIN Number"
                  name="vinNumber"
                  value={formData.vinNumber}
                  onChange={handleInputChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  label="Make"
                  name="make"
                  value={formData.make}
                  onChange={handleInputChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  label="Model"
                  name="model"
                  value={formData.model}
                  onChange={handleInputChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  label="Year"
                  name="year"
                  type="number"
                  value={formData.year}
                  onChange={handleInputChange}
                  inputProps={{ min: 1900, max: new Date().getFullYear() + 1 }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  label="Rental Price (ETH/day)"
                  name="rentalPrice"
                  type="number"
                  value={formData.rentalPrice}
                  onChange={handleInputChange}
                  inputProps={{ step: '0.001' }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Image URL"
                  name="imageUrl"
                  value={formData.imageUrl}
                  onChange={handleInputChange}
                  helperText="Provide a URL to an image of your car"
                />
              </Grid>
            </Grid>

            {error && (
              <Alert severity="error" sx={{ mt: 2 }}>
                {error}
              </Alert>
            )}

            {success && (
              <Alert severity="success" sx={{ mt: 2 }}>
                Car registered successfully!
              </Alert>
            )}

            <Box sx={{ mt: 3 }}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                fullWidth
                disabled={processing}
              >
                {processing ? 'Processing...' : 'Register Car'}
              </Button>
            </Box>
          </form>
        </CardContent>
      </Card>
    </Container>
  );
};

export default RegisterCar;
