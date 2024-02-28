import { GenericSlide } from "yet-another-react-lightbox";
declare module "yet-another-react-lightbox" {
    interface GenericSlide {
      imageId?: string;
    }
  
    interface SlideImage {
      imageId?: string;
    }
  }