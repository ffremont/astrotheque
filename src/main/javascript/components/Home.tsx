import { Search } from "@mui/icons-material"
import { Fab, InputAdornment, NativeSelect,  TextField } from "@mui/material"
import { Box } from "@mui/system"
import { useState } from "react";
import PhotoAlbum from "react-photo-album";
import Lightbox from "yet-another-react-lightbox";
import { Captions, Counter, Download, Share } from "yet-another-react-lightbox/plugins";
import { Edit } from "../libs/yet-another-react-lightbox/plugins/edit/Edit";
import { Remove } from "../libs/yet-another-react-lightbox/plugins/remove/Remove";
import AddIcon from '@mui/icons-material/Add';
import { Link } from "react-router-dom";

export const Home = () => {
    const [index, setIndex] = useState(-1);
    const photos = [{
        src: 'https://dummyimage.com/1080x1620/000/fff&text=AA',
        width: 1080,
        height: 1620,
        title: 'Nébuleuse du coeur',
        share: { url: "/image3.png", title: "Image title", text: "" },
        downloadUrl: 'https://dummyimage.com/1080x1620/000/fff&text=AA',
        description: 'blabla',
        imageId: 'aa',
        href: ''
    }, {
        src: 'https://dummyimage.com/1080x1620/000/fff&text=BB',
        width: 1080,
        height: 1620,
        imageId: 'bb',
        title: 'Nébuleuse du coeur',
        description: 'blabla',
        share: { url: "/image3.png", title: "Image title", text: "" },
        downloadUrl: 'https://dummyimage.com/1080x1620/000/fff&text=AA',
        href: ''
    }]

    return (
        <Box display="flex" flexDirection="column">
            <Box height="2rem" display="flex" flexBasis="content" gap="0.4rem">
                <NativeSelect
                    sx={{ width: "9rem" }}
                    defaultValue={30}
                    inputProps={{
                        name: 'age',
                        id: 'uncontrolled-native',
                    }}
                >
                    <option value={10}>Constellations</option>
                    <option value={20}>Types</option>
                    <option value={30}>Lieux</option>
                    <option value={40}>Cibles</option>
                    <option value={40}>Lunaisons</option>
                </NativeSelect>
                <TextField
                    fullWidth
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <Search />
                            </InputAdornment>
                        ),
                    }}
                    variant="standard"
                />
            </Box>
            <Box flex="1" sx={{ padding: "1rem 0rem" }}>

                <PhotoAlbum photos={photos} layout="rows" targetRowHeight={150} onClick={({ index }) => setIndex(index)} />

                <Lightbox
                    slides={photos}
                    open={index >= 0}
                    index={index}
                    close={() => setIndex(-1)}
                    // enable optional lightbox plugins
                    plugins={[Captions, Share, Counter, Remove, Edit, Download]}


                // Share / Edit / Remove
                />

                <Link to="/importation"><Fab className="fab-add-obs" size="medium" color="secondary" variant="extended">
                    <AddIcon sx={{ mr: 1 }} />
                    Importer
                </Fab>
                </Link>
            </Box>

        </Box>)
}