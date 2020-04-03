# The chrome-scalajs-template
This is an opinionated template that can help you to get started fast while building browser extensions with scala-js.

## Why
While there are docs for building browser extensions, it isn't obvious how to do it with scala-js, after dealing with an extension for a while, I got a reasonable architecture that can be reused on other extensions and simplify its development, the goal from this template is to save you valuable time.

The current template includes the examples for the following:
- A tiny script that displays a notification every time you visit github.com
- A small button on the browser toolbar which renders pop-up and displays a notification.
- An alarm which is a task executed frequently, currently, displaying a notification.
- Support for two languages (English/Spanish).
- An example for dealing with the storage.
- Configuration classes.
- A way for building the extension for the dev environment by default, which can be overriden by an environment variable to prepare the extension for release, in this case, it replaces the server from localhost to the one you choose.

## Get started

**NOTE**: This template works only with scalajs 1.0.0, if you want to use a previous version, run `git checkout 402abfac9a4b9eba7f395009aa9b2243f3498273` and follow the docs from that version.

It's pretty simple to get started, just follow these steps:
- Clone the repo: `git clone https://github.com/AlexITC/chrome-scalajs-template.git`
- Move to the cloned repo: `cd chrome-scalajs-template`
- Add your brand: `./customize.sh com.alexitc.chrome com/alexitc/chrome` (replace the arguments with your desired base package, ignore the `sed` related warnings).
- Edit the [build.sbt](build.sbt) to add the desired details for your app.
- Edit the [app resources](src/main/resources) to the ones for your app.
- Commit your changes and continue to the next section for building the app, also, start looking on the [Firefox guide](https://developer.mozilla.org/en-US/docs/Mozilla/Add-ons/WebExtensions) or the [Chrome guide](https://developer.chrome.com/extensions/devguide) for developing extensions.
- Running `sbt chromePackage` on this project is enough to get your extension packaged.

## Development
- Running `sbt ~chromeUnpackedFast` will build the app each time it detects changes on the code, it also disables js optimizations which result in faster builds (placing the build at `target/chrome/unpacked-fast`).
- Be sure to integrate scalafmt on your IntelliJ to format the source code on save (see https://scalameta.org/scalafmt/docs/installation.html#intellij).

## Release
Run: `PROD=true sbt chromePackage` which generates:
- A zip file that can be uploaded to the chrome store: `target/chrome/chrome-scalajs-template.zip`
- A folder with all resources which can be packaged for firefox: `target/chrome/unpacked-opt/`

## Docs
The project has 3 components, and each of them acts as a different application, there are interfaces for interacting between them.

### Active Tab
The [activetab](/src/main/scala/com/alexitc/activetab) package has the script that is executed when the user visits a web page that the app is allowed to interact with (like github.com), everything running on the active tab has limited permissions, see the [official docs](https://developer.chrome.com/extensions/activeTab) for more details, be sure to read about the [content scripts](https://developer.chrome.com/extensions/content_scripts) too.

### Popup
The [activetab](/src/main/scala/com/alexitc/popup) package has the script that is executed when the user visits clicks on the app icon which is displayed on the browser navigation bar.
 
There is a limited functionality that the popup can do, see the [official docs](https://developer.chrome.com/extensions/browserAction) for more details.

### Background
The [background](/src/main/scala/com/alexitc/background) package has the script that is executed when the browser starts, it keeps running to do anything the extension is supposed to do on the background, for example, interacts with the storage, web services, and alarms.

As other components can't interact directly with the storage or web services, they use a high-level API ([BackgroundAPI](/src/main/scala/com/alexitc/background/BackgroundAPI.scala)) to request the background to do that.

Be sure to review the [official docs](https://developer.chrome.com/extensions/background_pages).


## Hints
While scalajs works great, there are some limitations, in particular, you must pay lots of attention while dealing with boundaries.

A boundary is whatever interacts directly with JavaScript, like the browser APIs, for example:
- When the background receives requests from other components, it gets a message that is serialized and deserialized by the browser, in order to support strongly typed models, the app encodes these models to a JSON string, the browser knows how to deal with strings but it doesn't know what to do with Scala objects.
- When the storage needs to persist data, the app encodes the Scala objects to a JSON string, which is what the browser understands.
- When there is a need to interact with JavaScript APIs (like external libraries), you'll need to write a [facade](/src/main/scala/com/alexitc/facades) unless there is one available, this facade will deal with primitive types only (strings, ints, etc).
