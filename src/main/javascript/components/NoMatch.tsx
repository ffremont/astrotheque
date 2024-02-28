
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import { Link } from 'react-router-dom';


export const NoMatch = () => {
  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom>
        404 - Page non trouvée
      </Typography>
      <Typography variant="body1" gutterBottom>
        La page que vous recherchez n'existe pas.
      </Typography>
      <Button component={Link} to="/" variant="contained" color="primary">
        Retour à la page d'accueil
      </Button>
    </Container>
  );
};
