import { constellations } from '../types/Constellations'
import { MoonPhases } from '../types/MoonPhases'
import { Picture } from '../types/Picture'
import { PictureInAlbum } from '../types/PictureInAlbum'
import { PictureTypes } from '../types/PictureTypes'
import { Weathers } from '../types/Weathers'
import { formatExpo } from './formatExpo'

export const fromList = (pictures: Picture[]): PictureInAlbum[] => {
    return pictures.map((picture) => {
        const title = `${
            picture.type ? PictureTypes[picture.type] + ' : ' : 'Type inconnu '
        }${picture.name ? picture.name : 'cible introuvé'} ${
            picture.constellation
                ? '(' +
                  constellations.find((c) => c.abr === picture.constellation)
                      ?.label +
                  ')'
                : ''
        }`
        const description = `Exp. ${formatExpo(picture)} (${
            picture.exposure
        }s x ${picture.stackCnt}) avec ${picture.camera} sur ${
            picture.instrument
        } / ${[
            picture.corrRed,
            MoonPhases[picture.moonPhase],
            `Météo : ${Weathers[picture.weather]}`,
        ]
            .filter((cell) => !!cell)
            .join(' / ')}`

        return {
            data: picture,
            title,
            description,
            src: `/api/pictures/thumb/${picture.id}`,
            share: {
                url: `${window.location.origin}/api/pictures/${picture.id}`,
                text: description,
                title: `🔭 ${picture.name} sur mon Astrothèque`,
            },
            downloadUrl: `/api/pictures/image/${picture.id}`,
            height: 512,
            width: 512,
            href: `/api/pictures/${picture.id}`,
            imageId: picture.id,
        }
    })
}
