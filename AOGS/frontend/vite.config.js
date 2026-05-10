import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// AOGS Frontend - Vite yapılandırması
// Geliştirme sunucusu Spring Boot backend'ine (8080) proxy yapar.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
