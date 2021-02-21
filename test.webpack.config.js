var webpack = require('webpack');

// Unfortunately, scalajs-bundler is not generating this file while running `sbt test`
module.exports = require('../main/scalajs.webpack.config');

// NOTE: development is useful for debugging but apparently it breaks the build for Chrome
// with the current settings.
module.exports.mode = "production";

// by default, scalajs-bundler sets this to "var" but that breaks the build for Firefox.
module.exports.output.libraryTarget = "window";
