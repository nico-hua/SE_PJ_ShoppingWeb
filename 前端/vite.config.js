import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '127.0.0.1', // 本机地址
    port: 80, // 前端端口
    // 反向代理，跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端部署地址 本地
        // target: 'http://10.117.169.218:8080', //后端部署地址 王
        //target: 'http://10.117.66.214:8080', // 后端部署地址 徐
        //target: 'http://10.117.91.139:8080', // 后端部署地址 滕
        // target: 'http://120.46.164.17:8080', // 后端部署地址 云服务器
        changeOrigin: true, // 跨域
        rewrite: (path) => path.replace(/^\/api/, '')
      },
    }
  }
})
