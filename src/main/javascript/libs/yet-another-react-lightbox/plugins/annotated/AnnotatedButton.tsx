import { useNavigate } from "react-router-dom";
import { IconButton, createIcon, useLightboxProps, useLightboxState } from "yet-another-react-lightbox";
import { downloadBlob } from "../../../../utils/download";
import { useAstrotheque } from "../../../../hooks/useAstrotheque";

const AnnotatedIcon = createIcon(
  "AnnotatedIcon",
  <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24"><path d="M200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h200v80H200v560h560v-214l80 80v134q0 33-23.5 56.5T760-120H200Zm40-160 120-160 90 120 120-160 150 200H240Zm622-144L738-548q-21 14-45 21t-51 7q-74 0-126-52.5T464-700q0-75 52.5-127.5T644-880q75 0 127.5 52.5T824-700q0 27-8 52t-20 46l122 122-56 56ZM644-600q42 0 71-29t29-71q0-42-29-71t-71-29q-42 0-71 29t-29 71q0 42 29 71t71 29Z"/></svg>
);

export function AnnotatedButton() {
  const { render } = useLightboxProps();
  const { setNotification } = useAstrotheque();
  const { currentSlide } = useLightboxState();


  if (render.buttonShare) {
    return <>{render.buttonShare()}</>;
  }


  const handleDownload = () => {
    if (currentSlide) {
      fetch(`/api/pictures/annotated/${currentSlide.imageId}`)
        .then(response => response.blob())
        .then(blob => {
          downloadBlob(blob, `${currentSlide.imageId}-annotated.jpg`);
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
      label="Annoté"
      icon={AnnotatedIcon}
      renderIcon={render.iconShare}
      onClick={handleDownload}
    />
  );
}