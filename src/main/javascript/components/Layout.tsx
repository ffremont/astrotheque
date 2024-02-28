import { AppBar, Badge, Box, Container, Divider, IconButton, ListItemIcon, Menu, MenuItem, Toolbar, Typography } from "@mui/material"
import { Outlet } from "react-router-dom"
import NotificationsIcon from '@mui/icons-material/Notifications';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { useState } from "react";
import { AccountCircle, Logout, Settings } from "@mui/icons-material";

export const Layout = () => {
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    return ( <Container maxWidth="md" sx={{height:'100%'}}>
        <AppBar position="fixed">
            <Toolbar>
                <Typography variant="h6" className="main-title" noWrap component="div" >
                    Astrothèque
                </Typography>
                <Box sx={{ flexGrow: 1 }} />

                <Box>
                    <IconButton
                        size="large"
                        aria-label="show 17 new notifications"
                    >
                        <Badge badgeContent={17} color="error">
                            <NotificationsIcon />
                        </Badge>
                    </IconButton>
                    <IconButton size="large" onClick={handleMenu}>
                        <MoreVertIcon />
                    </IconButton>



                    <Menu
                        id="menu-appbar"
                        anchorEl={anchorEl}
                        anchorOrigin={{
                            vertical: 'top',
                            horizontal: 'right',
                        }}
                        keepMounted
                        transformOrigin={{
                            vertical: 'top',
                            horizontal: 'right',
                        }}
                        open={Boolean(anchorEl)}
                        onClose={handleClose}
                    >
                        <MenuItem onClick={handleClose}>
                        <ListItemIcon>
                                <AccountCircle fontSize="small" />
                            </ListItemIcon>
                            Admin
                        </MenuItem>
                        <Divider />
                        <MenuItem onClick={handleClose}>
                            <ListItemIcon>
                                <Settings fontSize="small" />
                            </ListItemIcon>
                            Réglages
                        </MenuItem>
                        <MenuItem onClick={handleClose}>
                            <ListItemIcon>
                                <Logout fontSize="small" />
                            </ListItemIcon>
                            Se déconnecter
                        </MenuItem>
                    </Menu>
                </Box>

            </Toolbar>
        </AppBar>

        <Outlet />
    </Container>)
}