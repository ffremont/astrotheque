import { MoonPhase } from './MoonPhase'

export type Picture = {
    id: string
    observationId: string
    state: 'PENDING' | 'DONE' | 'FAILED'
    imported: number
    name: string
    filename: string
    moonPhase: MoonPhase
    dateObs: string
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
