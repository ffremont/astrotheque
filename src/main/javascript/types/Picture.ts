import { MoonPhase } from './MoonPhase'

export type Picture = {
    id: string
    observationId: string
    state: 'PENDING' | 'DONE'
    imported: number
    name: string
    moonPhase: MoonPhase
    dateObs: number
    weather: 'VERY_GOOD' | 'FAVORABLE' | 'GOOD' | 'BAD'
    instrument: string
    location: string
    camera: string
    corrRed: string
    exposure: number
    gain: number

    stackCnt: number
    tags: string[]
    constellation: string
    ra: number
    dec: number
    type: string
}
