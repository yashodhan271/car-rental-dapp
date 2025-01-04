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
  TextField,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

interface Car {
  id: string;
  vinNumber: string;
  make: string;
  model: string;
  year: number;
  rentalPrice: number;
  imageUrl: string;
  isAvailable: boolean;
}

const CarList: React.FC = () => {
  const [cars, setCars] = useState<Car[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCars = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/cars/available');
        setCars(response.data);
      } catch (error) {
        console.error('Error fetching cars:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchCars();
  }, []);

  const filteredCars = cars.filter(car => 
    `${car.make} ${car.model}`.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Available Cars
      </Typography>

      <TextField
        fullWidth
        variant="outlined"
        placeholder="Search cars by make or model..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        sx={{ mb: 4 }}
      />

      <Grid container spacing={4}>
        {filteredCars.map((car) => (
          <Grid item key={car.id} xs={12} sm={6} md={4}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardMedia
                component="img"
                height="200"
                image={car.imageUrl || 'https://via.placeholder.com/400x200?text=Car+Image'}
                alt={`${car.make} ${car.model}`}
              />
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  {car.make} {car.model}
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  Year: {car.year}
                </Typography>
                <Typography variant="h6" color="primary">
                  {car.rentalPrice} ETH/day
                </Typography>
                <Button
                  variant="contained"
                  fullWidth
                  sx={{ mt: 2 }}
                  onClick={() => navigate(`/cars/${car.vinNumber}`)}
                >
                  View Details
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {filteredCars.length === 0 && (
        <Box textAlign="center" mt={4}>
          <Typography variant="h6" color="text.secondary">
            No cars found matching your search.
          </Typography>
        </Box>
      )}
    </Container>
  );
};

export default CarList;
