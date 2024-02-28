import { Box, Button, Card, CardContent, CardMedia, Chip, NativeSelect, Paper, TextField, Typography } from "@mui/material"
import obs from '../assets/obs.jpeg'

export const Importation = () => {
    return (<Box>
        <Card sx={{ marginBottom: '1rem' }}>
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

        <Paper sx={{ padding: "0.6rem" }}>
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Fichiers FITS
            </Typography>

            <TextField type="file"
                inputProps={{
                    multiple: true
                }}
                fullWidth label="Fichiers FITs" variant="standard" />

        </Paper>

        <Paper sx={{ padding: "0.6rem", marginTop: '1rem' }}>
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Fichiers Aperçu (facultatif)
            </Typography>
            <Typography align="justify" variant="body2"  gutterBottom>
                Les fichiers doivent avoir le même nom que les fichiers FITs afin de les associer ensemble.
            </Typography>

            <TextField type="file"
                inputProps={{
                    multiple: true
                }}
                fullWidth label="Fichiers apercu" variant="standard" />

        </Paper>

        <Paper sx={{ padding: "0.6rem", marginTop: '1rem' }}>
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Conditions
            </Typography>

            <TextField fullWidth label="Lieu" variant="standard" />
            <NativeSelect
                fullWidth
                defaultValue={30}
                inputProps={{
                    name: 'weather',
                }}
            >
                <option value={10}>Ten</option>
                <option value={20}>Twenty</option>
                <option value={30}>Thirty</option>
            </NativeSelect>
        </Paper>

        <Paper sx={{ padding: "0.6rem", marginTop: '1rem' }}>
            <Typography align="left" sx={{ fontWeight: "bold" }} gutterBottom>
                Setup
            </Typography>

            <TextField fullWidth label="Instrument" variant="standard" />
            <TextField fullWidth label="Correcteur / reducteur" variant="standard" />
        </Paper>

        <Box display="flex" gap="1rem" justifyContent="center" sx={{ marginTop: "1rem" }}>
            <Button>Annuler</Button>
            <Button type="submit" variant="contained">Importer</Button>
        </Box>


    </Box>)
}