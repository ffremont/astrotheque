import { Search } from "@mui/icons-material"
import { Autocomplete, Fab, InputAdornment, NativeSelect, TextField } from "@mui/material"
import { Box } from "@mui/system"
import { useEffect, useState } from "react";
import Lightbox from "yet-another-react-lightbox";
import { Captions, Counter, Share } from "yet-another-react-lightbox/plugins";
import { Edit } from "../libs/yet-another-react-lightbox/plugins/edit/Edit";
import { Remove } from "../libs/yet-another-react-lightbox/plugins/remove/Remove";
import AddIcon from '@mui/icons-material/Add';
import { Link, useLocation } from "react-router-dom";
import { useFetch } from "../hooks/useFetch";
import { Picture } from "../types/Picture";
import { PictureInAlbum } from "../types/PictureInAlbum";
import { fromList } from "../utils/pictureInAlbumFactory";
import { CriteriaNames, allCriteria } from "../types/Criteria";
import { Item } from "../types/Item";
import { Note } from "../libs/yet-another-react-lightbox/plugins/note/Note";
import { useAstrotheque } from "../hooks/useAstrotheque";
import { Binaries } from "../libs/yet-another-react-lightbox/plugins/binaries/Binaries";
import { Footer } from "./Layout/Footer";
import { View } from "../libs/yet-another-react-lightbox/plugins/view/View";
import { PhotoAlbum } from "./Layout/PhotoAlbum";

const donePictures = (pictures: Picture[]) => pictures.filter(p => p.state === 'DONE');

export const Home = () => {
    const [index, setIndex] = useState(-1);
    const { state } = useLocation();
    const { pictures, setPictures } = useAstrotheque();
    const [picturesInAlbum, setPicturesInAlbum] = useState<(PictureInAlbum)[]>([]);

    const myFetch = useFetch();
    const [search, setSearch] = useState<Item | null>(null);
    const [searchIn, setSearchIn] = useState<Item[]>([]);
    const [criteria, setCriteria] = useState<CriteriaNames>('CONST');

    useEffect(() => {
        if (criteria) {
            const myCriteria = allCriteria.find(c => c.name === criteria);
            setSearchIn(myCriteria?.values(pictures) || []);
        }
        setSearch(null);
    }, [criteria]);

    useEffect(() => {
        if (search && criteria) {
            const myCriteria = allCriteria.find(c => c.name === criteria);

            setPicturesInAlbum(fromList(myCriteria?.filter(search.value, donePictures(pictures)) || []))
        } else {
            setPicturesInAlbum(fromList(donePictures(pictures)));
        }
    }, [search, criteria, pictures])

    useEffect(() => {
        myFetch.get<Picture[]>('/api/pictures')
            .then(pictures => {
                setPictures(pictures);
            });
    }, [state]);

    const handleClickPhoto = (photo: PictureInAlbum)=> {
        const index = picturesInAlbum.findIndex(p => p.imageId === photo.imageId);
        setIndex(index);
    }


    const days = (new Set(picturesInAlbum.map(picture => picture.day)));

    return (
        <Box display="flex" flexDirection="column">
            <Box height="2rem" display="flex" flexBasis="content" gap="0.4rem">
                <NativeSelect
                    sx={{ width: "12rem" }}
                    value={criteria}
                    onChange={(event) => {
                        setCriteria(event.target.value as CriteriaNames);
                    }}
                    inputProps={{
                        name: 'criteria',
                    }}
                >
                    {allCriteria.map((c, i) => <option key={c.name + i} value={c.name}>{c.label}</option>)}
                </NativeSelect>
                <Autocomplete
                    fullWidth
                    value={search}
                    onChange={(event: any, newValue: Item | null) => {
                        setSearch(newValue);
                    }}
                    options={searchIn}
                    renderInput={(params) => (
                        <TextField
                            {...params}
                            variant="standard"

                            InputProps={{
                                ...params.InputProps,
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Search />
                                    </InputAdornment>
                                ),
                                type: 'text',
                            }}
                        />
                    )}
                />
            </Box>
            <Box flex="1" sx={{ padding: "1rem 0rem" }}>

                {/*<PhotoAlbum componentsProps={(containerWidth) => ({
                    imageProps: {
                        loading: (containerWidth || 0) > 600 ? "eager" : "lazy",
                        "style": {
                            aspectRatio: "auto"
                        }
                    },
                })}
                    renderPhoto={({ imageProps: { src, alt, style, ...restImageProps } }) => (
                        <img src={src} alt={alt} style={style} {...restImageProps} />
                    )}
                photos={picturesInAlbum} layout="rows" targetRowHeight={150} onClick={({ index }) => setIndex(index)} />*/}


                <div className="album">{
                    Array.from(days).map(day => 
                        <PhotoAlbum photos={picturesInAlbum.filter(p => p.day === day)} title={day} onClickPhoto={handleClickPhoto} />
                    )
                }
            </div>

                <Lightbox
                    slides={picturesInAlbum}
                    open={index >= 0}
                    index={index}
                    close={() => setIndex(-1)}
                    plugins={[Captions, Share, Counter, Remove, Edit, Binaries, Note, View]}
                />

                <Link to="/importation"><Fab className="fab-add-obs" size="medium" color="secondary" variant="extended">
                    <AddIcon sx={{ mr: 1 }} />
                    Importer
                </Fab>
                </Link>
            </Box>

            <Footer version={import.meta.env.VITE_REACT_APP_VERSION} totalBytes={picturesInAlbum.map(p => p.data.size || 0).reduce((a, b) => a + b, 0)} totalItems={picturesInAlbum.length} />
        </Box>)
}