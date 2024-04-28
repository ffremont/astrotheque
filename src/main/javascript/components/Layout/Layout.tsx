import { Alert,  Container,  Snackbar } from "@mui/material"
import { Outlet, useLocation, useNavigate } from "react-router-dom"
import { Header } from "./Header";
import { useEffect, useState } from "react";

import { useFetch } from "../../hooks/useFetch";
import { HttpError } from "../../types/HttpError";
import { Me } from "../../types/Me";
import { useAstrotheque } from "../../hooks/useAstrotheque";
import { ImporationDrawer } from "./ImportationDrawer";
import { ConfigurationDialog } from "./ConfigurationDialog";
import { ProfilDialog } from "./ProfilDialog";

export const Layout = () => {
    const { username, setUsername, notification } = useAstrotheque();
    const [openNotification, setOpenNotification] = useState(false);
    
    const [configDialog, setConfigDialog] = useState(false);
    const [profilDialog, setProfilDialog] = useState(false);
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
                navigate('/album')
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
        {username && <Header 
        onClickImport={() => toggleDrawer(true)} 
        onClickConfig={() => setConfigDialog(true)}
        onClickProfil={() => setProfilDialog(true)}
         />}

        <ImporationDrawer open={drawer} onClose={() => toggleDrawer(false)}/>

        <ConfigurationDialog open={configDialog} onClose={() => setConfigDialog(false)}/>
        <ProfilDialog open={profilDialog} onClose={() => setProfilDialog(false)}/>

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