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
        }, 🗓️ ${new Intl.DateTimeFormat(
            'fr-FR',
            {
                dateStyle: 'medium',
                timeStyle: 'short',
                timeZone: 'Europe/Paris',
            }
        ).format(
            new Date(picture.dateObs || new Date())
        )} `
        const description = `Intégration ${formatExpo(picture)} (${
            picture.exposure
        }s x ${picture.stackCnt}) avec ${picture.camera} sur ${
            picture.instrument
        } / ${[
            MoonPhases[picture.moonPhase],
            `Météo : ${Weathers[picture.weather]}`,
            picture.tags.join(', ')
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
            height: 1024,
            width: 1024,
            href: `/api/pictures/${picture.id}`,
            imageId: picture.id,
        }
    })
}
