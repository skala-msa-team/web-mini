import tailwindcss from '@tailwindcss/vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue(), tailwindcss()],
    server: {
      host: env.DEV_SERVER_HOST || '0.0.0.0',
      port: Number(env.DEV_SERVER_PORT) || 5173,
      strictPort: env.DEV_SERVER_STRICT_PORT !== 'false',
      proxy: {
        '/api': {
          target: env.BACKEND_HTTP_ORIGIN || 'http://localhost:8080',
        },
        '/ws': {
          target: env.BACKEND_WS_ORIGIN || 'ws://localhost:8080',
          ws: true,
        },
      },
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
  }
})
