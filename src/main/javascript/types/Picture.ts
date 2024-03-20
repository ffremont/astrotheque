import { MoonPhase } from './MoonPhase'
import { PlanetSatellite } from './PlanetSatellite'

export type Picture = {
    id: string
    observationId: string
    state: 'PENDING' | 'DONE' | 'FAILED'
    imported: number
    name: string
    planetSatellite?:PlanetSatellite
    filename: string
    moonPhase: MoonPhase
    dateObs: string
    weather: 'VERY_GOOD' | 'FAVORABLE' | 'GOOD' | 'BAD'
    instrument: string
    location: string
    camera: string
    exposure: number
    gain: number
    note:string

    stackCnt: number
    tags: string[]
    constellation: string
    ra: number
    dec: number
    type: string
}
