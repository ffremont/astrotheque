import { PictureInAlbum } from "../../types/PictureInAlbum";

type PhotoAlbumProps = {
    photos: PictureInAlbum[],
    title: string
    onClickPhoto: (photo: PictureInAlbum) => void
}

export const PhotoAlbum = ({ photos, title, onClickPhoto }: PhotoAlbumProps) => {
    return <section className="piece-of-album">
        <h3>{title}</h3>

        <div className="photos">
            {photos.map(photo =>
                <div key={photo.imageId}><img alt={photo.title ?? ''} onClick={()=> onClickPhoto(photo)} title={photo.title ?? ''} src={photo.src} /></div>
            )}
        </div>
    </section>

}