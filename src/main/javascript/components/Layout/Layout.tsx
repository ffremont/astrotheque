import { Container } from "@mui/material"
import { Outlet, useLocation, useNavigate } from "react-router-dom"
import { Header } from "./Header";
import { useEffect } from "react";
import { useFetch } from "../../hooks/useFetch";
import { HttpError } from "../../types/HttpError";
import { Me } from "../../types/Me";
import { useAstrotheque } from "../../hooks/useAstrotheque";

export const Layout = () => {
    const {username, setUsername} = useAstrotheque();
    let location = useLocation();
    const navigate = useNavigate();

    const myFetch = useFetch();

    useEffect(() => {
        if(location.pathname.endsWith('/installation')){
            return;
        }

        myFetch.get<String>('/install')
        .then(resp => {
            if(!resp){
                navigate('/installation');
            }else{
                return myFetch.get<Me>('/api/me');
            }
        })
        .then(me => {
            if(me && me.username){
                setUsername(me.username);
            }
        })
        .catch(e => {
            if(e instanceof HttpError && e.status === 401 && !location.pathname.endsWith('/login')){
                navigate('/login');
            } 
        });

    }, [location]);

    return (<Container maxWidth="md" sx={{ height: '100%' }}>
        {username && <Header />}

        <Outlet />
    </Container>)
}