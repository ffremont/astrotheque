import { Alert, Avatar, Box, Button, Grid, Link, TextField, Typography } from "@mui/material";
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { FormEvent, useState } from "react";
import { useFetch } from "../hooks/useFetch";
import { useNavigate } from "react-router-dom";

export const Login = () => {
    const [error, setError] = useState(false);
    const navigate = useNavigate();
    const myFetch = useFetch();

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formDataEntries = new FormData(e.currentTarget);

        const formDataObject: { [key: string]: string } = {};
        for (const [name, value] of formDataEntries) {
            formDataObject[name] = value as string;
        }

        myFetch.post('/login', formDataObject)
            .then(() => {
                navigate('/');
        })
            .catch(() => setError(true));
    };

    return (<Box
        sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
        }}
    >
        <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
            <LockOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5">
            Se connecter
        </Typography>
        
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                label="Nom utilisateur"
                name="login"
                type="text"
                autoFocus
            />
            <TextField
                margin="normal"
                required
                fullWidth
                name="pwd"
                label="Mot de passe"
                type="password"
                id="password"
                autoComplete="current-password"
            />
            {/*<FormControlLabel
            control={<Checkbox value="remember" color="primary" />}
            label="Remember me"
    />*/}
    {error && <Alert sx={{textAlign:'left'}} severity="error">
            Login / mot de passe invalide, si le problème persiste contacter l'admin</Alert>}
            <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
            >
                Se Connecter
            </Button>
            <Grid container>
                <Grid item xs>
                    <Link href="#" variant="body2">
                        Mot de passe oublié?
                    </Link>
                </Grid>
                <Grid item>
                    {/*<Link href="#" variant="body2">
                        {"Je n'ai pas de compte? S'inscrire"}
</Link>*/}
                </Grid>
            </Grid>
        </Box>
    </Box>);
}