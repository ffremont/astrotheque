import { useContext } from "react"
import {  AstrothequeContext } from "../providers/AstrothequeProvider"
import { Astrotheque } from "../types/Astrotheque";

export const useAstrotheque = (): Astrotheque => {
    const ctx =  useContext(AstrothequeContext);

    if(ctx === null){
        throw new Error('Astrotheque ctx not found');
    }

    return ctx;
}