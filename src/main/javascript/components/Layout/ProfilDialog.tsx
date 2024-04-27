import { AppBar, Box, Button, Dialog, IconButton, Paper, TextField, Toolbar, Typography } from "@mui/material"
import CloseIcon from '@mui/icons-material/Close';
import { SubmitHandler, useForm } from "react-hook-form";
import { useFetch } from "../../hooks/useFetch";
import { useAstrotheque } from "../../hooks/useAstrotheque";

type ProfilDialogProps = {
    open: boolean,
    onClose: () => void
}


type Inputs = {
    actualPassword: string,
    newPassword: string,
    confirmNewPassword: string,
}

export const ProfilDialog = ({ open, onClose }: ProfilDialogProps) => {
    const {
        register,
        watch,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<Inputs>({
        shouldUseNativeValidation: true
    });
    const myFetch = useFetch();
    const { setNotification } = useAstrotheque();


    const onSubmit: SubmitHandler<Inputs> = (data) => {
        myFetch.put('/api/me/password', {
            actualPassword: data.actualPassword,
            newPassword: data.newPassword
        })
            .then(() => {
                setNotification({
                    type: 'success',
                    title: 'Profil mis à jour',
                    message: 'Vos modifications ont été enregistrées.'
                })
            }).catch(() => {
                setNotification({
                    type: 'error',
                    title: 'Profil non mis à jour',
                    message: 'Une erreur est survenue lors de l\'enregistrement. Veuillez réitérer.'
                })
            })
            .finally(() => {
                reset();
                onClose();
            });
    }

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
                        Profil
                    </Typography>
                    <Button type="submit" autoFocus color="inherit">
                        Valider
                    </Button>
                </Toolbar>
            </AppBar>

            <Box sx={{ margin: '1rem' }}>
                <Paper className="form-section">
                    <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                        Mot de passe
                    </Typography>

                    <TextField fullWidth required {...register("actualPassword", { required: true, maxLength: 256 })} error={!!errors.actualPassword} helperText={errors.actualPassword?.message} label="Actuel mot de passe" type="password" variant="standard" />
                    <TextField fullWidth required {...register("newPassword", { required: true, maxLength: 256, minLength: 8 })} error={!!errors.newPassword} helperText={errors.newPassword?.message || 'Minimum 8 caractères'} type="password" label="Nouveau mot de passe" variant="standard" />
                    <TextField fullWidth required {...register("confirmNewPassword", {
                        required: true, maxLength: 256, minLength: 8, validate: (value) => {
                            
                            return watch('newPassword') === value || "Les mots de passe doivent correspondre"
                        }
                    })} error={!!errors.confirmNewPassword} helperText={errors.confirmNewPassword?.message || 'Minimum 8 caractères'} type="password" label="Confirmer le nouveau mot de passe" variant="standard" />
                </Paper>
            </Box>
        </form>
    </Dialog>)
}