import { PropsWithChildren, createContext, useState } from "react";
import { Astrotheque } from "../types/Astrotheque";


export const AstrothequeContext = createContext<Astrotheque | null>(null);

export const AstrothequeProvider = ({ children }: PropsWithChildren) => {
    const [username, setUsername] = useState('');

    const initValue = {
        username, setUsername
    };
    return (<AstrothequeContext.Provider value={initValue}>
        {children}
    </AstrothequeContext.Provider>);
}