var webpack = require('webpack');

module.exports = require('./scalajs.webpack.config');

// NOTE: development is useful for debugging but apparently it breaks the build for Chrome
// with the current settings.
module.exports.mode = "production";

// by default, scalajs-bundler sets this to "var" but that breaks the build for Firefox.
module.exports.output.libraryTarget = "window";
