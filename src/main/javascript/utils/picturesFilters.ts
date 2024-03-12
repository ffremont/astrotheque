import { Picture } from "../types/Picture";

export const recentPictures = (pictures: Picture[]): Picture[] => {
    return pictures.filter(p => p.filename && p.imported)
    .filter(p => (new Date(p.imported)).getTime() > ((new Date()).getTime()-43200000));
}