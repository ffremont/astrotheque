import { Slide } from "yet-another-react-lightbox";
import { Photo } from "react-photo-album";
import { Picture } from "./Picture";



export type PictureInAlbum = Slide & Photo & {data: Picture} & { day: string}