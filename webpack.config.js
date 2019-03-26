const webpack = require('webpack');                         //to access built-in plugins
const path = require('path');
const VueLoaderPlugin = require('vue-loader/lib/plugin')

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
    resolve: {
        alias: {
            vue: 'vue/dist/vue.js'
        }
    },
    module: {
        rules: [
            {
                test: /\.vue$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'vue-loader'
            },
            // this will apply to both plain `.js` files
            // AND `<script>` blocks in `.vue` files
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                loader: 'babel-loader'
            },
            // this will apply to both plain `.css` files
            // AND `<style>` blocks in `.vue` files
            {
                test: /\.css$/,
                exclude: /(node_modules|bower_components)/,
                use: [
                    'vue-style-loader',
                    'css-loader'
                ]
            }
        ]
    },
    plugins: [
        // make sure to include the plugin for the magic
        new VueLoaderPlugin()
    ]
}
