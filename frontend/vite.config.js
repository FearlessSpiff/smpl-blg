import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
      vue(),
  vuetify(),
  ],
  server: {
    proxy: {
      '/api': 'http://localhost:9666'
    },
    host: '0.0.0.0'
  }
})
