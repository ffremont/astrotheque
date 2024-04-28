import { Box, CircularProgress, Typography } from "@mui/material";


export const Initialization = () => <Box>
    <CircularProgress />
    <Typography variant="h1" sx={{opacity:'0.5', fontFamily:'Lexend Exa', fontSize:'1.2rem'}} gutterBottom>
        initilisation de l'application...
      </Typography>
</Box>