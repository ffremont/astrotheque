
import { Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { themeOptions } from "./theme";
import { Home } from './components/Home'
import { Layout } from './components/Layout/Layout'
import { NoMatch } from './components/NoMatch'
import { Login } from './components/Login';
import { Importation } from './components/Importation';
import { AstrothequeProvider } from './providers/AstrothequeProvider';
import { Installation } from './components/Installation';
import { Error } from './components/Error';
import { PictureForm } from './components/PictureForm';
import { Initialization } from './components/Initialization';

function App() {
    const myTheme = createTheme(themeOptions);

    return (
        (
            <AstrothequeProvider><ThemeProvider theme={myTheme}><Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={<Initialization />} />

                    <Route path="album" element={<Home />} />
                    <Route path="login" element={<Login />} />
                    <Route path="initialization" element={<Initialization />} />
                    <Route path="error" element={<Error />} />
                    <Route path="importation" element={<Importation />} />
                    <Route path="photos/:id" element={<PictureForm />} />
                    <Route path="installation" element={<Installation />} />

                    <Route path="*" element={<NoMatch />} />
                </Route>
            </Routes>
            </ThemeProvider></AstrothequeProvider>
        ))
}

export default App
