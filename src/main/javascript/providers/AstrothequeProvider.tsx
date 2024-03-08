import { PropsWithChildren, createContext, useState } from "react";
import { Astrotheque } from "../types/Astrotheque";
import { Picture } from "../types/Picture";
import { Notification } from "../types/Notification";


export const AstrothequeContext = createContext<Astrotheque | null>(null);

export const AstrothequeProvider = ({ children }: PropsWithChildren) => {
    const [username, setUsername] = useState('');
    const [pictures, setPictures] = useState<Picture[]>([]);
    const [notification, setNotification] = useState<Notification|null>(null);

    const initValue = {
        username, setUsername,
        notification, setNotification,
        pictures, setPictures
    };
    return (<AstrothequeContext.Provider value={initValue}>
        {children}
    </AstrothequeContext.Provider>);
}