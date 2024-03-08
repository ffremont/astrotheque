import { Notification } from "./Notification"
import { Picture } from "./Picture"

export type Astrotheque = {
    username: string,
    setUsername: (username: string) => void,

    notification: Notification|null,
    setNotification: (notification: Notification|null) => void

    pictures: Picture[],
    setPictures: (pictures:Picture[]) => void
}