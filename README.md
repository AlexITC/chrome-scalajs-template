# The chrome-scalajs-template
This is an opinionated template that can help you to get started fast while building browser extensions with scala-js.

## Get started
It's pretty simple to get started, just follow these steps:
- Clone the repo: `git clone https://github.com/AlexITC/chrome-scalajs-template.git`
- Move to the cloned repo: `cd chrome-scalajs-template`
- Add your brand: `./customize.sh com.alexitc.chrome com/alexitc/chrome` (replace the arguments with your desired base package)
- Edit the [build.sbt](build.sbt) to add the desired details for your app.
- Edit the [app resources](src/main/resources) to the ones for your app.
- Commit your changes and continue to the next section for building the app.

## Dependencies
Until this [PR](https://github.com/lucidd/scala-js-chrome/pull/46) gets merged, we need a custom plugin to support [content_scripts](https://developer.chrome.com/extensions/content_scripts), if you don't need those, feel free to replace the forked plugin on the [plugins.sbt](project/plugins.sbt).

Before being able to build the extension, you'll need to install a plugin locally.
- Clone `https://github.com/AlexITC/scala-js-chrome`
- Move to the `add-content-script` branch (`git checkout add-content-script`).
- Install the custom `scala-js-chrome` locally (`sbt publishLocal`).

Then, running `sbt chromePackage` on this project is enough.

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
