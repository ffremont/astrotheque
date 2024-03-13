import {  Box, CircularProgress, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, ListSubheader, Typography } from "@mui/material"

import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import { useTheme } from '@mui/material/styles';
import { useAstrotheque } from "../../hooks/useAstrotheque";
import { recentPictures } from "../../utils/picturesFilters";

type ImportationDrawerProps = {
    open:boolean,
    onClose: () => void
}

export const ImporationDrawer = ({open, onClose}: ImportationDrawerProps) => {
    const {  pictures } = useAstrotheque();
    const theme = useTheme();

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

            {recentPictures(pictures)
            .map(p =>
                <ListItem key={p.id} disablePadding>
                    <ListItemButton>
                        <ListItemIcon>
                            {p.state === 'PENDING' && <CircularProgress />}
                            {p.state === 'DONE' && <DoneIcon />}
                            {p.state === 'FAILED' && <CloseIcon />}
                        </ListItemIcon>

                        <ListItemText sx={{wordBreak: 'break-all'}} primary={`${p.filename}`} />
                    </ListItemButton>
                </ListItem>)}

            {recentPictures(pictures).length === 0 && <p>
                <Typography fontStyle="italic" marginLeft={"1rem"} variant="caption" display="block" gutterBottom>
                    Aucune image importée
                </Typography>
            </p>}
        </List>
    </Box>
</Drawer>)
}