import { Alert, Box, CircularProgress, Container, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, ListSubheader, Snackbar, Typography } from "@mui/material"
import { Outlet, useLocation, useNavigate } from "react-router-dom"
import { Header } from "./Header";
import { useEffect, useState } from "react";
import { useTheme } from '@mui/material/styles';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import { useFetch } from "../../hooks/useFetch";
import { HttpError } from "../../types/HttpError";
import { Me } from "../../types/Me";
import { useAstrotheque } from "../../hooks/useAstrotheque";

export const Layout = () => {
    const { username, setUsername, notification, pictures } = useAstrotheque();
    const [openNotification, setOpenNotification] = useState(false);
    const theme = useTheme();

    const [drawer, toggleDrawer] = useState(false);
    let location = useLocation();
    const navigate = useNavigate();

    const myFetch = useFetch();

    useEffect(() => {
        if (location.pathname.endsWith('/installation')) {
            return;
        }

        myFetch.get<String>('/install')
            .then(resp => {
                if (!resp) {
                    navigate('/installation');
                } else {
                    return myFetch.get<Me>('/api/me');
                }
            })
            .then(me => {
                if (me && me.username) {
                    setUsername(me.username);
                }
            })
            .catch(e => {
                if (e instanceof HttpError && e.status === 401 && !location.pathname.endsWith('/login')) {
                    navigate('/login');
                }
            });

    }, [location]);

    const handleCloseNotification = () => {
        setOpenNotification(false);
    };

    useEffect(() => {
        setOpenNotification(!!notification);
    }, [notification])

    return (<Container maxWidth="md" sx={{ height: '100%' }}>
        {username && <Header onClickImport={() => toggleDrawer(true)} />}

        <Drawer open={drawer} anchor="right" onClose={() => toggleDrawer(false)}>
            <Box sx={{ width: 250 }} role="presentation">
                <List
                    subheader={
                        <ListSubheader sx={{
                            background: theme.palette.secondary.main
                        }} component="div">
                            Importation des fichiers
                        </ListSubheader>
                    }>

                    {pictures.filter(p => p.filename).map(p =>
                        <ListItem key={p.id} disablePadding>
                            <ListItemButton>
                                <ListItemIcon>
                                    {p.state === 'PENDING' && <CircularProgress />}
                                    {p.state === 'DONE' && <DoneIcon />}
                                    {p.state === 'FAILED' && <CloseIcon />}
                                </ListItemIcon>

                                <ListItemText primary={`${p.filename}`} />
                            </ListItemButton>
                        </ListItem>)}

                    {!pictures.some(p => p.filename) && <p>
                        <Typography fontStyle="italic" marginLeft={"1rem"} variant="caption" display="block" gutterBottom>
                            Aucune image importée
                        </Typography>
                    </p>}


                </List>
            </Box>
        </Drawer>

        <Snackbar open={openNotification} autoHideDuration={6000} onClose={handleCloseNotification}>
            <Alert
                onClose={handleCloseNotification}
                severity={notification?.type}
                variant="filled"
                sx={{ width: '100%' }}
            >
                {notification?.message}
            </Alert>
        </Snackbar>

        <Outlet />
    </Container>)
}