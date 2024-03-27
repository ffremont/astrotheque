
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import { Link } from 'react-router-dom';
import BugReportIcon from '@mui/icons-material/BugReport';


export const Error = () => {
  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom>
        <BugReportIcon/>Oups, une erreur est survenue
      </Typography>
      <Typography variant="body1" gutterBottom>
        Veuillez réitérer, si le problème persiste, veuillez contacter la administrateur.
      </Typography>
      <Button component={Link} to="/" variant="contained" color="primary">
        Retour à la page d'accueil
      </Button>
    </Container>
  );
};
