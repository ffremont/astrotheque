import { AppBar, Badge, Box, Divider, IconButton, ListItemIcon, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import { useState } from "react";
import MoreVertIcon from '@mui/icons-material/MoreVert';
import UploadIcon from '@mui/icons-material/Upload';
import { AccountCircle, Logout, Settings } from "@mui/icons-material";
import { useAstrotheque } from "../../hooks/useAstrotheque";
import { useFetch } from "../../hooks/useFetch";
import { useNavigate } from "react-router-dom";

export const Header = () => {
    const { username } = useAstrotheque();
    const myFetch = useFetch();
    const navigate = useNavigate();

    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleLogout = () => {
        if (window.confirm(`Confirmez-vous la déconnexion ?`)) {
            myFetch.post('/logout', {})
                .then(() => window.location.reload())
                .finally(() => {
                    setAnchorEl(null);
                });
        }
    }


    return (<AppBar position="fixed">
        <Toolbar>
            <Typography onClick={() => navigate('/')} variant="h6" className="main-title" noWrap component="div" >
                Astrothèque
            </Typography>
            <Box sx={{ flexGrow: 1 }} />

            <Box>
                <IconButton
                    size="large"
                    aria-label="show 17 new notifications"
                >
                    <Badge variant="dot" color="error">
                        <UploadIcon />
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
                        {username}
                    </MenuItem>
                    <Divider />
                    <MenuItem onClick={handleClose}>
                        <ListItemIcon>
                            <Settings fontSize="small" />
                        </ListItemIcon>
                        Réglages
                    </MenuItem>
                    <MenuItem onClick={handleLogout}>
                        <ListItemIcon>
                            <Logout fontSize="small" />
                        </ListItemIcon>
                        Se déconnecter
                    </MenuItem>
                </Menu>
            </Box>

        </Toolbar>
    </AppBar>);
}