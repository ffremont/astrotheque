import { AppBar, Badge, Box, Divider, IconButton, ListItemIcon, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import MoreVertIcon from '@mui/icons-material/MoreVert';
import UploadIcon from '@mui/icons-material/Upload';
import { AccountCircle, Logout, Settings } from "@mui/icons-material";
import { useAstrotheque } from "../../hooks/useAstrotheque";
import { useFetch } from "../../hooks/useFetch";
import { useNavigate } from "react-router-dom";

type HeaderProps = {
    onClickImport: () => void
}

type BadgeState = 'info' | 'warning' | 'error'

export const Header = ({ onClickImport }: HeaderProps) => {
    const { username, pictures } = useAstrotheque();
    const myFetch = useFetch();
    const navigate = useNavigate();
    const [state, setState] = useState<BadgeState> ('info');

    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    useEffect(() => {
        if(pictures.some(p => p.state==='FAILED')){
            setState('error');
        }else if(pictures.some(p => p.state==='PENDING')){
            setState('warning');
        }else{
            setState('info');
        }
        
    }, [pictures]);

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
                    onClick={onClickImport}
                    size="large"
                    aria-label="show 17 new notifications"
                >
                    <Badge variant="dot" color={state}>
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