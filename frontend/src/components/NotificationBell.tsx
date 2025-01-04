import React, { useState, useEffect } from 'react';
import {
  Badge,
  IconButton,
  Menu,
  MenuItem,
  Typography,
  Box,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import PaymentIcon from '@mui/icons-material/Payment';
import StarIcon from '@mui/icons-material/Star';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import { useWeb3React } from '@web3-react/core';
import axios from 'axios';
import { format } from 'date-fns';

interface Notification {
  id: string;
  title: string;
  message: string;
  type: string;
  timestamp: string;
  read: boolean;
}

const NotificationBell: React.FC = () => {
  const { account } = useWeb3React();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (account) {
      fetchNotifications();
      const interval = setInterval(fetchNotifications, 30000); // Poll every 30 seconds
      return () => clearInterval(interval);
    }
  }, [account]);

  const fetchNotifications = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/notifications/${account}`);
      setNotifications(response.data);
      const unreadResponse = await axios.get(
        `http://localhost:8080/api/notifications/${account}/unread/count`
      );
      setUnreadCount(unreadResponse.data);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  };

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleMarkAsRead = async (notificationId: string) => {
    try {
      await axios.post(`http://localhost:8080/api/notifications/${notificationId}/read`);
      fetchNotifications();
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await axios.post(`http://localhost:8080/api/notifications/${account}/read/all`);
      fetchNotifications();
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'RENTAL_CREATED':
      case 'RENTAL_COMPLETED':
        return <DirectionsCarIcon />;
      case 'PAYMENT_RECEIVED':
      case 'PAYMENT_SENT':
        return <PaymentIcon />;
      case 'NEW_REVIEW':
        return <StarIcon />;
      case 'GPS_ALERT':
        return <LocationOnIcon />;
      default:
        return <NotificationsIcon />;
    }
  };

  return (
    <>
      <IconButton color="inherit" onClick={handleClick}>
        <Badge badgeContent={unreadCount} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>

      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        PaperProps={{
          style: {
            maxHeight: 400,
            width: '350px',
          },
        }}
      >
        <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">Notifications</Typography>
          {unreadCount > 0 && (
            <Typography
              variant="body2"
              sx={{ cursor: 'pointer', color: 'primary.main' }}
              onClick={handleMarkAllAsRead}
            >
              Mark all as read
            </Typography>
          )}
        </Box>
        <Divider />
        
        <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
          {notifications.length === 0 ? (
            <ListItem>
              <ListItemText primary="No notifications" />
            </ListItem>
          ) : (
            notifications.map((notification) => (
              <ListItem
                key={notification.id}
                onClick={() => handleMarkAsRead(notification.id)}
                sx={{
                  bgcolor: notification.read ? 'inherit' : 'action.hover',
                  cursor: 'pointer',
                }}
              >
                <ListItemIcon>{getNotificationIcon(notification.type)}</ListItemIcon>
                <ListItemText
                  primary={notification.title}
                  secondary={
                    <>
                      {notification.message}
                      <Typography
                        variant="caption"
                        display="block"
                        color="text.secondary"
                      >
                        {format(new Date(notification.timestamp), 'PPp')}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
            ))
          )}
        </List>
      </Menu>
    </>
  );
};

export default NotificationBell;
