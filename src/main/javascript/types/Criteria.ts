import { constellations } from "./Constellations";
import { Item } from "./Item";
import { MoonPhases } from "./MoonPhases";
import { Picture } from "./Picture";
import { PictureTypes } from "./PictureTypes";

export type CriteriaNames = 'CONST' | 'MOON' | 'LOCATION' | 'TYPE' | 'TARGET';

export type Criteria = {
    name: CriteriaNames,
    label: string,
    values: (pictures: Picture[]) => Item[],
    filter: (search: string, pictures: Picture[]) => Picture[]
}

export const allCriteria: Criteria[] = [{
    name: 'CONST',
    label: 'Constellations',
    values: () => constellations.map(c => ({ label: c.label, value: c.abr })),
    filter: (search: string, pictures: Picture[]) => pictures.filter(p => p.constellation === search)
}, {
    name: 'TYPE',
    label: 'Types',
    values: () => Object.keys(PictureTypes).map(key => ({ label: PictureTypes[key], value: key })),
    filter: (search: string, pictures: Picture[]) => pictures.filter(p => p.type === search)
}, {
    name: 'MOON',
    label: 'Lunaisons',
    values: () => Object.keys(MoonPhases).map(key => ({ label: MoonPhases[key], value: key })),
    filter: (search: string, pictures: Picture[]) => pictures.filter(p => p.moonPhase === search)
}, {
    name: 'LOCATION',
    label: 'Lieux',
    values: (pictures) => {
        const locations = new Set<string>([]);
        pictures.map(pia => pia.location).forEach(loc => locations.add(loc));
        return Array.from(locations).map(loc => ({ label: loc, value: loc }));
    },
    filter: (search: string, pictures: Picture[]) => pictures.filter(p => p.location === search)
}, {
    name: 'TARGET',
    label: 'Cibles',
    values: (pictures) => {
        const targets = new Set<string>([]);
        pictures.flatMap(pia => pia.tags).forEach(tag => targets.add(tag));
        return Array.from(targets).map(loc => ({ label: loc, value: loc }));
    },
    filter: (search: string, pictures: Picture[]) => pictures.filter(p => p.tags.indexOf(search) > -1)
}]