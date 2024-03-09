import { AppBar, Button, Dialog, Divider, IconButton, List, ListItemButton, ListItemText, Toolbar, Typography } from "@mui/material"
import CloseIcon from '@mui/icons-material/Close';

type ConfigurationDialogProps = {
    open: boolean,
    onClose: () => void
}

export const ConfigurationDialog = ({open, onClose}: ConfigurationDialogProps) => {

    return (<Dialog
        fullScreen
        open={open}
        onClose={onClose}
      >
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
              Sound
            </Typography>
            <Button autoFocus color="inherit" onClick={onClose}>
              save
            </Button>
          </Toolbar>
        </AppBar>
        <List>
          <ListItemButton>
            <ListItemText primary="Phone ringtone" secondary="Titania" />
          </ListItemButton>
          <Divider />
          <ListItemButton>
            <ListItemText
              primary="Default notification ringtone"
              secondary="Tethys"
            />
          </ListItemButton>
        </List>
      </Dialog>)
}