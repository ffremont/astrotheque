import { Alert, Box, Button, Card, CardContent, CardMedia, Paper, TextField, Typography } from "@mui/material";
import setup from '../assets/setup.jpeg';
import { SubmitHandler, useForm } from "react-hook-form";
import { useState } from "react";
import { useFetch } from "../hooks/useFetch";
import { useNavigate } from "react-router-dom";

type Inputs = {
    baseurl: string
    astrometryNovaApikey: string,
    adminLogin: string,
    adminPwd: string,
    adminConfirm: string
}

export const Installation = () => {
    const [same, setSame] = useState(true);
    const navigate = useNavigate();
    const myFetch = useFetch();
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<Inputs>({
        shouldUseNativeValidation: true,
        defaultValues: {
            baseurl: window.location.origin,
            adminLogin: 'admin',
        }
    })

    const onSubmit: SubmitHandler<Inputs> = (data) => {
        if (data.adminPwd !== data.adminConfirm) {
            setSame(false);
            return;
        }

        if (window.confirm(`Confirmez-vous ces informations ?`)) {
            myFetch.post('/install', {
                baseurl: data.baseurl,
                astrometryNovaApikey: data.astrometryNovaApikey,
                admin:{
                    login: data.adminLogin,
                    pwd: data.adminPwd
                }
            }).then(() => navigate('/'))
        }
    }

    return (<Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <Card className="form-intro" >
            <CardMedia
                sx={{ height: 140 }}
                image={setup}
                title="green iguana"
            />
            <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                    Installation
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    L'application <strong>Astrothèque</strong> nécessite quelques informations pour fonctionner, merci de prendre le temps de compléter les informations ci-dessous.
                </Typography>
            </CardContent>
        </Card>

        <Paper className="form-section">
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Configuration générale
            </Typography>

            <TextField fullWidth required {...register("baseurl", { required: true, maxLength: 256 })} error={!!errors.baseurl} label="Base url" type="url" variant="standard" />
            <TextField fullWidth required {...register("astrometryNovaApikey", { required: true, maxLength: 124, minLength:10 })} error={!!errors.astrometryNovaApikey} label="Api Key, Astrometry Nova" variant="standard" />

        </Paper>

        <Paper className="form-section">
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Accès principal (administrateur)
            </Typography>

            <TextField fullWidth required {...register("adminLogin", { required: true, maxLength: 256 })} error={!!errors.adminLogin} label="Identifiant" variant="standard" />
            <TextField fullWidth required {...register("adminPwd", { required: true, maxLength: 256 })} error={!!errors.adminLogin} label="adminPwd" type="password" variant="standard" />
            <TextField fullWidth required {...register("adminConfirm", { required: true, maxLength: 256 })} error={!!errors.adminConfirm} label="Confirmer le mot de passe" type="password" variant="standard" />
            {!same && <Alert severity="error">La confirmation doit être identique au mot de passe.</Alert>}
        </Paper>

        <Box className="form-actions">
            <Button type="submit" color="secondary" variant="contained">Valider</Button>
        </Box>
    </Box>)
};