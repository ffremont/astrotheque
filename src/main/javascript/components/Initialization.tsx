import { Box, CircularProgress, Typography } from "@mui/material";
import { useAstrotheque } from "../hooks/useAstrotheque";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";


export const Initialization = () => {
  const { username } = useAstrotheque();
  const navigate = useNavigate();
  
  useEffect(() => {
    if(username){
      navigate('/album')
    }
  }, [username]);

  return <Box>
    <CircularProgress />
    <Typography variant="h1" sx={{ opacity: '0.5', fontFamily: 'Lexend Exa', fontSize: '1.2rem' }} gutterBottom>
      initilisation de l'application...
    </Typography>
  </Box>
}