import { AppBar, Box, Button, Dialog, IconButton, Paper, TextField, Toolbar, Typography } from "@mui/material"
import CloseIcon from '@mui/icons-material/Close';
import { SubmitHandler, useForm } from "react-hook-form";
import { useEffect } from "react";
import { useFetch } from "../../hooks/useFetch";
import { Config } from "../../types/Config";

type ConfigurationDialogProps = {
    open: boolean,
    onClose: () => void
}


type Inputs = {
    baseurl: string
    astrometryNovaApikey: string,
}

export const ConfigurationDialog = ({ open, onClose }: ConfigurationDialogProps) => {
    const {
        register,
        handleSubmit,
        setValue,
        formState: { errors },
    } = useForm<Inputs>({
        shouldUseNativeValidation: true,
        defaultValues: {
            baseurl: window.location.origin,
        }
    });
    const myFetch = useFetch();

    useEffect(() => {
        if(open === true){
        myFetch.get<Config>('/api/config')
            .then(config => {
                setValue('baseurl', config.baseurl);
                setValue('astrometryNovaApikey', config.astrometryNovaApikey);
            })
        }
    }, [open]);

    const onSubmit: SubmitHandler<Inputs> = (data) => { }
    return (<Dialog
        fullScreen
        open={open}
        onClose={onClose}
    >
        <form onSubmit={handleSubmit(onSubmit)}>
            <AppBar sx={{ position: 'relative' }}>
                <Toolbar>
                    <IconButton
                        edge="start"
                        color="inherit"
                        onClick={onClose}
                        aria-label="close"
                    >
                        <CloseIcon />
                    </IconButton>
                    <Typography sx={{ ml: 2, flex: 1 }} variant="h6" component="div">
                        Réglages
                    </Typography>
                    <Button type="submit" autoFocus color="inherit">
                        Valider
                    </Button>
                </Toolbar>
            </AppBar>

            <Box sx={{ margin: '1rem' }}>
                <Paper className="form-section">
                    <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                        Configuration générale
                    </Typography>

                    <TextField fullWidth required {...register("baseurl", { required: true, maxLength: 256 })} error={!!errors.baseurl} label="Base url" type="url" variant="standard" />
                    <TextField fullWidth required {...register("astrometryNovaApikey", { required: true, maxLength: 124, minLength: 10 })} error={!!errors.astrometryNovaApikey} label="Api Key, Astrometry Nova" variant="standard" />

                </Paper>
            </Box>
        </form>
    </Dialog>)
}