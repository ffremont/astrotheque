import { Image, ImageSearch, RawOn } from "@mui/icons-material";
import { ListItemIcon, ListItemText, Menu, MenuItem } from "@mui/material";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { IconButton, createIcon, useLightboxProps, useLightboxState } from "yet-another-react-lightbox";
import { downloadBlob } from "../../../../utils/download";
import { useAstrotheque } from "../../../../hooks/useAstrotheque";
import { Picture } from "../../../../types/Picture";

const BinariesIcon = createIcon(
  "BinariesIcon",
  <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24"><path d="M480-320 280-520l56-58 104 104v-326h80v326l104-104 56 58-200 200ZM240-160q-33 0-56.5-23.5T160-240v-120h80v120h480v-120h80v120q0 33-23.5 56.5T720-160H240Z" /></svg>,
);

export function BinariesButton() {
  const { render } = useLightboxProps();
  const { currentSlide } = useLightboxState();
  const { setNotification } = useAstrotheque();

  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  const downloadAnnotated = () => {
    if (currentSlide) {
      console.log(currentSlide);
      fetch(`/api/pictures/annotated/${currentSlide.imageId}`)
        .then(response => response.blob())
        .then(blob => {
          downloadBlob(blob, `${currentSlide.imageId}-annotated.jpg`);
        }).catch(() => {
          setNotification({
            type: 'error',
            title: 'Téléchargement impossible',
            message: 'Une erreur est survenue lors du pré-téléchargement, veuillez réitérer.'
          })
        })
    }
  }

  const downloadImage = () => {
    if (currentSlide) {
      fetch(`/api/pictures/image/${currentSlide.imageId}`)
        .then(response => response.blob())
        .then(blob => {
          downloadBlob(blob, `${currentSlide.imageId}-image.jpg`);
        }).catch(() => {
          setNotification({
            type: 'error',
            title: 'Téléchargement impossible',
            message: 'Une erreur est survenue lors du pré-téléchargement, veuillez réitérer.'
          })
        })
    }
  }

  const downloadRaw = () => {
    if (currentSlide) {
      fetch(`/api/pictures/raw/${currentSlide.imageId}`)
        .then(response => response.blob())
        .then(blob => {
          downloadBlob(blob, `${currentSlide.imageId}-raw.fit`);
        }).catch(() => {
          setNotification({
            type: 'error',
            title: 'Téléchargement impossible',
            message: 'Une erreur est survenue lors du pré-téléchargement, veuillez réitérer.'
          })
        })
    }
  }

  const data = (currentSlide as any).data as Picture;
  return (
    <><IconButton
      label="Images"
      icon={BinariesIcon}
      renderIcon={render.iconShare}
      onClick={handleClick}
      aria-controls={open ? 'binaries-menu' : undefined}
      aria-haspopup="true"
      aria-expanded={open ? 'true' : undefined}
    />
      <Menu
        id="binaries-menu"
        sx={{ zIndex: '999999' }}
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
      >
        <MenuItem onClick={downloadImage}>
          <ListItemIcon>
            <Image fontSize="small" />
          </ListItemIcon>
          <ListItemText>Image (jpg)</ListItemText>
        </MenuItem>
        <MenuItem onClick={downloadRaw}>
          <ListItemIcon>
            <RawOn fontSize="small" />
          </ListItemIcon>
          <ListItemText>Raw (fit)</ListItemText>
        </MenuItem>

        {!data?.planetSatellite && (<MenuItem onClick={downloadAnnotated}>
            <ListItemIcon>
              <ImageSearch fontSize="small" />
            </ListItemIcon>
            <ListItemText>Annotée</ListItemText>
          </MenuItem>)}
      </Menu>
    </>
  );
}