import { Box, Button, Card, CardContent, CardMedia, NativeSelect, Paper, TextField, Typography } from "@mui/material"
import obs from '../assets/obs.jpeg'
import { SubmitHandler, useForm } from "react-hook-form"
import { useLocalStorage } from "usehooks-ts"
import { useEffect } from "react"

type Inputs = {
    weather: string,
    instrument: string,
    corrred: string,
    location: string
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
    const [instrument, saveInstrument] = useLocalStorage("instrument", '');
    const [location, saveLocation] = useLocalStorage("location", 'maison');
    const [corrred, saveCorrred] = useLocalStorage("corrred", '');
    useEffect(() => setValue('instrument', instrument), [instrument]);
    useEffect(() => setValue('corrred', corrred), [corrred]);
    useEffect(() => setValue('location', location), [location]);

    const onSubmit: SubmitHandler<Inputs> = (data) => {
        saveInstrument(data.instrument);
        saveCorrred(data.corrred);
        saveLocation(data.location);


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
                Fichiers FITS
            </Typography>

            <TextField type="file"
                required
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

            <TextField className="form-control" fullWidth required {...register("location", { required: true, maxLength: 256, minLength:2 })} error={!!errors.location} 
            label="Lieu" variant="standard" helperText="Nom de l'endroit d'observation" />
            <NativeSelect
                className="form-control"
                required {...register("weather", { required: true})} error={!!errors.weather}
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

            <TextField className="form-control" fullWidth required {...register("instrument", { required: true, maxLength: 256, minLength:2 })} error={!!errors.instrument}  label="Instrument" variant="standard" />
            <TextField className="form-control" fullWidth {...register("corrred", {  maxLength: 256, minLength:2 })} error={!!errors.corrred} label="Correcteur / reducteur" variant="standard" />
        </Paper>

        <Box className="form-actions">
            <Button>Annuler</Button>
            <Button type="submit" variant="contained">Importer</Button>
        </Box>
    </Box>)
}