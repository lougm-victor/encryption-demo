import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [vue()],
	resolve: {
		alias: {
			'@': path.resolve(__dirname, 'src'),
		},
	},
	server: {
		port: 3000,
		host: true,
		open: true,
		proxy: {
			'/dev-api': {
				target: 'http://localhost:8888',
				changeOrigin: true,
				rewrite: path => path.replace(/^\/dev-api/, ''),
			},
		},
	},
});
