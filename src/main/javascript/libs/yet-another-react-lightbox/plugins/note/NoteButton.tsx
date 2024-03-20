import { IconButton, createIcon, useLightboxProps, useLightboxState } from "yet-another-react-lightbox";
import { useState } from "react";
import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import { Picture } from "../../../../types/Picture";

const NoteIcon = createIcon(
  "NoteIcon",
  <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24"><path d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z" /></svg>
);

export function NoteButton() {
  const { render } = useLightboxProps();
  const { currentSlide } = useLightboxState();
  const [open, setOpen] = useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };


  if (render.buttonShare) {
    return <>{render.buttonShare()}</>;
  }


  const data = (currentSlide as any).data as Picture;

  return (
    <><IconButton
      label="Note"
      icon={NoteIcon}
      renderIcon={render.iconShare}
      onClick={handleClickOpen}
    />
      <Dialog
        sx={{ zIndex: '99999' }}
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          Note
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            {data?.note || `Aucune note pour l'instant.`}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" color="primary" onClick={handleClose} autoFocus>Fermer</Button>
         
        </DialogActions>
      </Dialog>
    </>
  );
}