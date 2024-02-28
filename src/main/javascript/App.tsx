
import { Route, Routes } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { themeOptions } from "./theme";
import { Home } from './components/Home'
import { Layout } from './components/Layout'
import { NoMatch } from './components/NoMatch'
import { Login } from './components/Login';
import { Importation } from './components/Importation';

function App() {
    const myTheme = createTheme(themeOptions);

    return (
        (
            <ThemeProvider theme={myTheme}><Routes>
                <Route path="/" element={<Layout />}>
                    <Route index element={<Home />} />

                    <Route path="login" element={<Login />} />
                    <Route path="importation" element={<Importation />} />

                    <Route path="*" element={<NoMatch />} />
                </Route>
            </Routes>
            </ThemeProvider>)
    )
}

export default App
