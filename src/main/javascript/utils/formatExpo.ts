import { Picture } from "../types/Picture";

export const formatExpo = (picture: Picture) => {
    const expo = (picture.exposure || 0) * (picture.stackCnt || 0);
    if (expo > 60) {
        return `${Math.floor(expo / 60)}m ${(expo % 60).toFixed(0)}s`;
    } else {
        return `${expo}s`
    }
};