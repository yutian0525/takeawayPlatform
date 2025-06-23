const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // 配置开发服务器
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:9000', // 后端服务器地址
        changeOrigin: true, // 允许跨域
        pathRewrite: {
          '^/api': '' // 将/api前缀重写为空
        }
      }
    }
  }
})
