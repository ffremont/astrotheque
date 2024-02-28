import { useNavigate } from "react-router-dom";
import { IconButton, createIcon, useLightboxProps, useLightboxState } from "yet-another-react-lightbox";

const RemoveIcon = createIcon(
    "RemoveIcon",
    <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24"><path d="M280-120q-33 0-56.5-23.5T200-200v-520h-40v-80h200v-40h240v40h200v80h-40v520q0 33-23.5 56.5T680-120H280Zm400-600H280v520h400v-520ZM360-280h80v-360h-80v360Zm160 0h80v-360h-80v360ZM280-720v520-520Z"/></svg>
  );
  
  export function RemoveButton() {
    const { render } = useLightboxProps();
    const { currentSlide } = useLightboxState();
    const navigate = useNavigate();

  
  
    const handleShare = () => {
      if (currentSlide) {
        navigate(`/photos/${currentSlide.imageId}`)
      }
    };
  
    return (
      <IconButton
        label="Effacer"
        icon={RemoveIcon}
        renderIcon={render.iconShare}
        onClick={handleShare}
      />
    );
  }