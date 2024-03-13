import { AppBar, Badge, Box, Divider, IconButton, ListItemIcon, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import MoreVertIcon from '@mui/icons-material/MoreVert';
import UploadIcon from '@mui/icons-material/Upload';
import { AccountCircle, Logout, Settings } from "@mui/icons-material";
import { useAstrotheque } from "../../hooks/useAstrotheque";
import { useFetch } from "../../hooks/useFetch";
import { useNavigate } from "react-router-dom";
import { Picture } from "../../types/Picture";
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import logo from '../../assets/icon512_rounded.png';
import { REFRESH_PICTURES_INTERVAL } from "../../constant";
import { recentPictures } from "../../utils/picturesFilters";

type HeaderProps = {
    onClickImport: () => void
    onClickConfig: () => void
    onClickProfil: () => void
}

type BadgeState = 'info' | 'warning' | 'error'

export const Header = ({ onClickImport, onClickConfig, onClickProfil }: HeaderProps) => {
    const { username, pictures, setPictures } = useAstrotheque();
    const myFetch = useFetch();
    const navigate = useNavigate();
    const [state, setState] = useState<BadgeState>('info');

    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    useEffect(() => {
        const recent = recentPictures(pictures);
        const haveFailed = recent.some(p => p.state === 'FAILED');
        const havePending = recent.some(p => p.state === 'PENDING');

        if (haveFailed && havePending) {
            setState('warning');
        } else if (haveFailed && !havePending) {
            setState('error');
        } else if (!haveFailed && havePending) {
            setState('warning');
        } else {
            setState('info');
        }
    }, [pictures]);

    useEffect(() => {
        const intervalId = setInterval(() => {
            myFetch.get<Picture[]>('/api/pictures')
                .then(pictures => {
                    setPictures(pictures);
                });
        }, REFRESH_PICTURES_INTERVAL);

        return () => {
            clearInterval(intervalId);
        }
    }, []);

    const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleConfig = () => {
        setAnchorEl(null);
        onClickConfig();
    }

    const handleProfil = () => {
        setAnchorEl(null);
        onClickProfil();
    }

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
            <Box alignContent={"center"}  display={"flex"} sx={{marginRight:'0.3rem'}}>
                <img src={logo} alt="logo" className="app-logo"/>
            </Box>
            <Typography onClick={() => navigate('/')} variant="h6" className="main-title" noWrap component="div" >
                Astrothèque
            </Typography>
            <Box sx={{ flexGrow: 1 }} />

            <Box>
                <IconButton
                    onClick={onClickImport}
                    size="large"
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
                    <MenuItem onClick={handleConfig}>
                        <ListItemIcon>
                            <Settings fontSize="small" />
                        </ListItemIcon>
                        Réglages
                    </MenuItem>
                    <MenuItem onClick={handleProfil}>
                        <ListItemIcon>
                            <AccountCircleIcon fontSize="small" />
                        </ListItemIcon>
                        Profil
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