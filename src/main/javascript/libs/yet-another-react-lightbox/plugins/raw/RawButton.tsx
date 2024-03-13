import { IconButton, createIcon, useLightboxProps, useLightboxState } from "yet-another-react-lightbox";
import { downloadBlob } from "../../../../utils/download";
import { useAstrotheque } from "../../../../hooks/useAstrotheque";

const RawIcon = createIcon(
  "RawIcon",
  <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24"><path d="M120-360v-240h140q24 0 42 18t18 42v40q0 18-9.5 32.5T284-444l36 84h-60l-36-80h-44v80h-60Zm230 0 60-240h100l60 240h-60l-14-60h-70l-16 60h-60Zm270 0-60-240h60l30 120 30-120h60l30 120 30-120h60l-60 240h-60l-30-122-30 122h-60ZM440-480h40l-10-40h-20l-10 40Zm-260-20h80v-40h-80v40Z" /></svg>,
);

export function RawButton() {
  const { render } = useLightboxProps();
  const { setNotification } = useAstrotheque();
  const { currentSlide } = useLightboxState();


  if (render.buttonShare) {
    return <>{render.buttonShare()}</>;
  }


  const handleDownload = () => {
    if (currentSlide) {
      fetch(`/api/pictures/raw/${currentSlide.imageId}`)
        .then(response => response.blob())
        .then(blob => {
          downloadBlob(blob, `${currentSlide.imageId}.fit`);
        }).catch(() => {
          setNotification({
              type:'error',
              title:'Téléchargement impossible',
              message: 'Une erreur est survenue lors du pré-téléchargement, veuillez réitérer.'
          })
        })
    }
  };

  return (
    <IconButton
      label="Editer"
      icon={RawIcon}
      renderIcon={render.iconShare}
      onClick={handleDownload}
    />
  );
}