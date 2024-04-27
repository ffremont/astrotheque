import { Box, Button, CircularProgress, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, ListSubheader, Typography } from "@mui/material"

import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import { useTheme } from '@mui/material/styles';
import { useAstrotheque } from "../../hooks/useAstrotheque";
import { recentPictures } from "../../utils/picturesFilters";
import { useFetch } from "../../hooks/useFetch";

type ImportationDrawerProps = {
    open: boolean,
    onClose: () => void
}

export const ImporationDrawer = ({ open, onClose }: ImportationDrawerProps) => {
    const { pictures, setNotification } = useAstrotheque();
    const theme = useTheme();
    const myFetch = useFetch();

    const handleCancelAll = () => {
        const firstPicturePending = recentPictures(pictures).find(picture => picture.state === 'PENDING')
        if (firstPicturePending && window.confirm(`Confirmez-vous l'annulation ?`)) {
            myFetch.delete(`/api/pictures/observations/pending`)
                .then(() => {
                    setNotification({
                        type: 'success',
                        title: 'Tentative d\'annulation',
                        message: 'Votre session sera annulée.'
                    })
                })
                .catch(() => {
                    setNotification({
                        type: 'error',
                        title: 'Annulation impossible',
                        message: 'Une erreur est survenue lors de l\'annulation. Veuillez réitérer.'
                    })
                })
        }
    }

    const recentPicturesList = recentPictures(pictures);

    return (<Drawer open={open} anchor="right" onClose={() => onClose()}>
        <Box sx={{ width: 250 }} role="presentation">
            <List
                subheader={
                    <ListSubheader sx={{
                        background: theme.palette.secondary.main
                    }} component="div">
                        Importation des fichiers
                    </ListSubheader>
                }>

                {recentPicturesList
                    .map(p =>
                        <ListItem key={p.id} disablePadding>
                            <ListItemButton>
                                <ListItemIcon>
                                    {p.state === 'PENDING' && <CircularProgress />}
                                    {p.state === 'DONE' && <DoneIcon />}
                                    {p.state === 'FAILED' && <CloseIcon />}
                                </ListItemIcon>

                                <ListItemText sx={{ wordBreak: 'break-all', fontSize: '0.8rem' }} primary={`${p.filename}`} />
                            </ListItemButton>
                        </ListItem>)}

                {recentPicturesList.length === 0 && <p>
                    <Typography fontStyle="italic" marginLeft={"1rem"} variant="caption" display="block" gutterBottom>
                        Aucune image importée
                    </Typography>
                </p>}
            </List>
            <Box textAlign="center">
                <Button disabled={recentPicturesList.length === 0 || !recentPicturesList.some(p => p.state === 'PENDING')} variant="contained" color="primary" onClick={handleCancelAll}>Tout annuler</Button>
            </Box>
        </Box>
    </Drawer>)
}