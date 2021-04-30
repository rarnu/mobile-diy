const fs = require('fs');
const CompressionPlugin = require('compression-webpack-plugin');

// 保存配置文件
function saveConfig() {
    if (process.env.NODE_ENV === 'production') {
        let config = {
            updateTime: new Date().getTime()
        };
        fs.writeFileSync('./src/assets/json/config.json', JSON.stringify(config));
    }
}

module.exports = {
    publicPath: './',
    productionSourceMap: false, //打包不生成map文件
    devServer: {
        port: 8010
    },
    css: {
        loaderOptions: {
            sass: {
                additionalData: `@import './src/style/variable.scss';`
            }
        }
    },
    configureWebpack: {
        plugins: [
            new CompressionPlugin({
                test: /\.(js|css)$/,
                threshold: 10240,   // 超过10k压缩
                deleteOriginalAssets: false // 删除源文件
            }),
            saveConfig
        ]
    }
};