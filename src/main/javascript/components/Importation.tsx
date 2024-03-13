import { Alert, Box, Button, Card, CardContent, CardMedia, CircularProgress, NativeSelect, Paper, TextField, Typography } from "@mui/material"
import obs from '../assets/obs.jpeg'
import { SubmitHandler, useForm } from "react-hook-form"
import { useLocalStorage } from "usehooks-ts"
import { blue } from '@mui/material/colors';
import { useEffect, useRef, useState } from "react"
import { useFetch } from "../hooks/useFetch"
import { useNavigate } from "react-router-dom";
import { MAX_UPLOAD_SIZE } from "../constant";

type Inputs = {
    weather: string,
    instrument: string,
    corrred: string,
    location: string
}

type Issue = {
    title:string,
    message: string
}

export const Importation = () => {
    const {
        register,
        handleSubmit,
        setValue,
        formState: { errors },
    } = useForm<Inputs>({
        shouldUseNativeValidation: true,

    })
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const fitFiles = useRef(null);
    const [issue, setIssue] = useState<Issue|null>(null);
    const previewFiles = useRef(null);
    const myFetch = useFetch(15 * 60000);
    const [instrument, saveInstrument] = useLocalStorage("instrument", '');
    const [location, saveLocation] = useLocalStorage("location", 'maison');
    const [corrred, saveCorrred] = useLocalStorage("corrred", '');
    useEffect(() => setValue('instrument', instrument), [instrument]);
    useEffect(() => setValue('corrred', corrred), [corrred]);
    useEffect(() => setValue('location', location), [location]);

    const onSubmit: SubmitHandler<Inputs> = (data) => {
        const form = new FormData();
        const fitNames = [];
        const jpgNames = [];
        let totalSize = 0;
        form.append('data', JSON.stringify({
            location: data.location,
            weather: data.weather,
            instrument: data.instrument,
            corrred: data.corrred
        }));
        if (fitFiles.current) {
            const files: any = (fitFiles.current as HTMLElement).querySelector('input')?.files;
            for (let index = 0; index < files.length; index++) {
                form.append('fits', files[index]);
                fitNames.push(files[index].name);
                totalSize += parseInt(files[index].size);
            }
        }
        if (previewFiles.current) {
            const files: any = (previewFiles.current as HTMLElement).querySelector('input')?.files;
            for (let index = 0; index < files.length; index++) {
                form.append('previews', files[index]);
                jpgNames.push(files[index].name);
                totalSize += parseInt(files[index].size);
            }
        }

        
        if(totalSize > MAX_UPLOAD_SIZE){
            setIssue({
                title:"Téléversement",
                message: `La taille totale de "${(totalSize/(1024*1024)).toFixed(1)}Mo" excède la limitation de ${(MAX_UPLOAD_SIZE/(1024*1024)).toFixed(0)}Mo.`
            })
            return;
        }

        if(!fitNames.every(filename => filename.endsWith(".fit"))){
            setIssue({
                title:"Fichiers FIT",
                message: `Certains fichiers FIT n'ont pas l'extension .fit.`
            })
            return;
        }

        if(!jpgNames.every(filename => filename.endsWith(".jpg") || filename.endsWith(".jpeg"))){
            setIssue({
                title:"Fichiers JPG",
                message: `Certains fichiers aperçus n'ont pas l'extension .jpg / .jpeg.`
            })
            return;
        }
        setLoading(true);
        saveInstrument(data.instrument);
        saveCorrred(data.corrred);
        saveLocation(data.location);
        myFetch.post('/api/observation', form)
            .then(() => {
                navigate('/');
            })
            .catch(() => navigate('/error'))
    }

    return (<Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <Card className="form-intro">
            <CardMedia
                sx={{ height: 140 }}
                image={obs}
                title="green iguana"
            />
            <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                    Nouvelle session
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    Cette page permet aux utilisateurs de téléverser facilement leurs clichés astronomiques au format FIT. Chaque image inclura automatiquement les données sur les conditions atmosphériques et les informations relatives au matériel utilisé.
                </Typography>
            </CardContent>
        </Card>

        <Paper className="form-section">
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Fichiers FIT
            </Typography>

            <TextField type="file"
                required
                ref={fitFiles}
                name="fits"
                inputProps={{
                    multiple: true,
                    accept: "image/fits"
                }}
                fullWidth label="Fichiers FITs" variant="standard" />
        </Paper>

        <Paper className="form-section">
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Fichiers Aperçu (facultatif)
            </Typography>
            <Typography align="justify" variant="body2" gutterBottom>
                Les fichiers doivent avoir le même nom que les fichiers FITs afin de les associer ensemble.
            </Typography>

            <TextField type="file"
                ref={previewFiles}
                name="preview"
                inputProps={{
                    multiple: true,
                    accept: "image/jpeg,image/jpg"
                }}
                fullWidth label="Fichiers apercu" variant="standard" />

        </Paper>

        <Paper className="form-section">
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Conditions
            </Typography>

            <TextField className="form-control" fullWidth required {...register("location", { required: true, maxLength: 256, minLength: 2 })} error={!!errors.location}
                label="Lieu" variant="standard" helperText="Nom de l'endroit d'observation" />
            <NativeSelect
                className="form-control"
                required {...register("weather", { required: true })} error={!!errors.weather}
                fullWidth
                defaultValue={30}
                inputProps={{
                    name: 'weather',
                }}
            >
                <option value={'VERY_GOOD'}>Excellente</option>
                <option value={'GOOD'}>Bonne</option>
                <option value={'FAVORABLE'}>Favorable</option>
                <option value={'BAD'}>Mauvaise</option>
            </NativeSelect>
        </Paper>

        <Paper className="form-section">
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Equipement
            </Typography>

            <TextField className="form-control" fullWidth required {...register("instrument", { required: true, maxLength: 256, minLength: 2 })} error={!!errors.instrument} label="Instrument" variant="standard" />
            <TextField className="form-control" fullWidth {...register("corrred", { maxLength: 256, minLength: 2 })} error={!!errors.corrred} label="Correcteur / reducteur" variant="standard" />
        </Paper>

        {issue && <Alert sx={{textAlign:'left',marginBottom:'1rem'}} severity="error">
            <strong>{issue.title}</strong>{' '}{issue.message}
        </Alert>}

        <Box className="form-actions">
            <Button>Annuler</Button>
            <Button type="submit" disabled={loading} variant="contained">Importer</Button>
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
    </Box>)
}