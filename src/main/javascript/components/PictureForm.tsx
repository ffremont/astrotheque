import { Box, Button, Card, CardMedia, CircularProgress, MenuItem, Paper, Select, TextField, Typography } from "@mui/material";
import { SyntheticEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Picture } from "../types/Picture";
import { useFetch } from "../hooks/useFetch";
import { PictureTypes } from "../types/PictureTypes";
import { blue } from '@mui/material/colors';
import { constellations } from "../types/Constellations";
import { Weathers } from "../types/Weathers";
import { MoonPhases } from "../types/MoonPhases";
import { useAstrotheque } from "../hooks/useAstrotheque";

export const PictureForm = () => {
    let { id } = useParams();
    const {setNotification } = useAstrotheque();
    const navigate = useNavigate();
    const myFetch = useFetch();
    const [loading, setLoading] = useState(false);
    const [picture, setPicture] = useState<Picture | null>(null);

    useEffect(() => {
        myFetch.get<Picture>(`/api/pictures/${id}`)
            .then(p => setPicture(p))
            .catch(() => navigate('/error'))
    }, []);


    const handleSubmit = (e: SyntheticEvent) => {
        e.preventDefault();
        setLoading(true);
        const data: any = {};
        new FormData(e.target as any).forEach((v, key) => {
            if (key === 'tags') {
                data[key] = (v as string).split(',').map(cel => cel.trim());
            } else {
                data[key] = v;
            }

        });
        myFetch.put(`/api/pictures/${id}`,data)
            .then(r => {
                navigate('/');
                setNotification({
                    type:'success',
                    title:`Succès de la mise à jour`,
                    message: `Photo astro "${id}" modifiée.`
                })
            }).catch(e => {
                navigate('/error')
            });
    }

    return (<Box component="form" onSubmit={handleSubmit}>
        {picture && <><Card className="form-intro">
            <CardMedia
                sx={{ height: 200 }}
                image={`/api/pictures/thumb/${picture.id}`}
                title="green iguana"
            />
        </Card>

            <Paper className="form-section">
                <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                    Astres
                </Typography>
                
                <TextField
                className="field"
                    autoFocus
                    margin="dense"
                    required
                    label="Nom"
                    type="text"
                    name="name"
                    fullWidth
                    defaultValue={picture.name}
                    variant="standard"
                />
                <Select
                    defaultValue={picture.type}
                    fullWidth
                    className="field"
                    required
                    variant="standard"
                    name="type"
                    label="Type"
                >
                    {Object.keys(PictureTypes).map(key => <MenuItem key={key} value={key}>{PictureTypes[key]}</MenuItem>)}
                </Select>
                <Select
                    defaultValue={picture.constellation}
                    label="Constellation"
                    className="field"
                    required
                    name="constellation"
                    variant="standard"
                    fullWidth
                >
                    {constellations.map(constellation => <MenuItem key={constellation.abr} value={constellation.abr}>{constellation.label}</MenuItem>)}
                </Select>
                <TextField
                    margin="dense"
                    label="Tags"
                    className="field"
                    required
                    type="text"
                    name="tags"
                    fullWidth
                    defaultValue={picture.tags?.join("; ")}
                    variant="standard"
                    helperText={`Valeurs séparées par un point virgule`}
                />


            </Paper>
            <Paper className="form-section">
                <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                    Image
                </Typography>
                {/** date, empilement, gain, exposition*/}

                <TextField
                    margin="dense"
                    label="Date"
                    required
                    className="field"
                    type="datetime-local"
                    name="dateObs"
                    fullWidth
                    defaultValue={picture.dateObs?.substring(0, picture.dateObs.lastIndexOf(':')
                        !== -1 ? picture.dateObs.lastIndexOf(':') : undefined)}
                    variant="standard"
                />
                <TextField
                    margin="dense"
                    label="Empilement"
                    type="number"
                    required
                    className="field"
                    name="stackCnt"
                    fullWidth
                    defaultValue={picture.stackCnt}
                    variant="standard"
                />

                <TextField
                    margin="dense"
                    label="Gain"
                    required
                    type="number"
                    name="gain"
                    className="field"
                    fullWidth
                    defaultValue={picture.gain}
                    variant="standard"
                />
                <TextField
                    margin="dense"
                    label="Exposure"
                    required
                    type="number"
                    name="exposure"
                    className="field"
                    inputProps={{
                        step:"0.01"
                    }}
                    fullWidth
                    defaultValue={picture.exposure}
                    variant="standard"
                />

            </Paper>

            <Paper className="form-section">
                <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                    Equipement
                </Typography>

                <TextField
                    margin="dense"
                    label="Instrument"
                    className="field"
                    type="text"
                    required
                    fullWidth
                    name="instrument"
                    defaultValue={picture.instrument}
                    variant="standard"
                />
                <TextField
                    margin="dense"
                    label="Camera"
                    required
                    type="text"
                    className="field"
                    fullWidth
                    name="camera"
                    defaultValue={picture.camera}
                    variant="standard"
                />
                <TextField
                    margin="dense"
                    label="Corrector / reductor"
                    type="text"
                    className="field"
                    fullWidth
                    name="corrRed"
                    defaultValue={picture.corrRed}
                    variant="standard"
                />

            </Paper>

            <Paper className="form-section">
                <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                    Conditions
                </Typography>

                <Select
                    defaultValue={picture.weather}
                    label="Weather"
                    className="field"
                    required
                    name="weather"
                    fullWidth
                >
                    {Object.keys(Weathers).map(key => <MenuItem key={key} value={key}>{Weathers[key]}</MenuItem>)}
                </Select>

                <Select
                    defaultValue={picture.moonPhase}
                    fullWidth
                    name="moonPhase"
                    className="field"
                    required
                    label="Moon phase"
                >
                    {Object.keys(MoonPhases).map(key => <MenuItem value={key} key={key}>{MoonPhases[key]}</MenuItem>)}
                </Select>

                <TextField
                    margin="dense"
                    label="Location"
                    type="text"
                    fullWidth
                    required
                    name="location"
                    defaultValue={picture.location}
                    variant="standard"
                />
            </Paper>

            <Box className="form-actions">
            <Button>Annuler</Button>
            <Button type="submit" disabled={loading} variant="contained">Mettre à jour</Button>
            {loading && (
                <CircularProgress
                    size={24}
                    sx={{
                        color: blue[500],
                        marginTop: '6px',
                        marginLeft: '-83px'
                    }}
                />
            )}
        </Box>
        </>}


    </Box>);
}