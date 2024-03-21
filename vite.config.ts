import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:9999',
                changeOrigin: true,
                secure: false,
            },
            '/install': {
                target: 'http://localhost:9999',
                changeOrigin: true,
                secure: false,
            },
            '/login': {
                target: 'http://localhost:9999',
                changeOrigin: true,
                secure: false,
            },
            '/logout': {
                target: 'http://localhost:9999',
                changeOrigin: true,
                secure: false,
            },
        },
    },
})
