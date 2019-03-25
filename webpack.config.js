const webpack = require('webpack');                         //to access built-in plugins
const path = require('path');

// Set production mode
// mode: 'production'

module.exports = {
    mode: 'development',
    entry: {
        app: './src/main/vue/app.js'
    },
    output: {
        path: path.resolve(__dirname, 'src/main/webapp/dist/'),
        filename: 'app.bundle.js'
    },
    // module: {
    //     rules: [
    //         {
    //             test: /\.js$/,
    //             exclude: /(node_modules|bower_components)/,
    //             use: {
    //                 loader: 'babel-loader',
    //                 options: {
    //                     presets: ['@babel/preset-env']
    //                 }
    //             }
    //         }
    //     ]
    // },
    // plugins: [
    // ]
}
