<a href="#"><img src="https://github.com/dtmilano/AndroidViewClient/wiki/images/culebra-logo-transparent-204x209-rb-border.png" align="left" hspace="0" vspace="6"></a>

# CulebraTester2-public
CulebraTester: Snaky Android Testing

### Welcome to CulebraTester2.
Android testing can be complicated, time-consuming, and tedious.
What if it didn’t have to be? 

**CulebraTester2** provides an API that facilitates the creation of tests and test automation tools and UI's.
Not sure what we mean?

Continue reading and see how you can run this early preview.


  ⚠️ Warning    |
:------------------|
**This is an alpha version of CulebraTester2 expect changes** |



# How to run CulebraTester2 ?
1. Have your device or emulator connected to `adb`
1. Install APKs
   1. Downalod prebuilt **app** and **instrumentation** APKs from [Github Actions](https://github.com/dtmilano/CulebraTester2-public/wiki/Prebuilt-APKs)
   1. or build from source and install
      1. Copy `local.properties.SAMPLE` to `local.properties` and adapt the values to your environment
      1. `./culebratester2 install` (or run `./gradlew installDebug installDebugAndroidTest`)
1. Start server `bash <(curl -sL https://git.io/JT5nc) start-server`
      1. alternative if you checked out the source you can run `./culebratester2 start-server` instead
1. Open http://localhost:9987/ with a browser or `curl` 
1. You should see `CulebraTester2: Go to http://localhost:<port>/help for usage details.`
1. If the previous request worked, you can try something more ambitious as http://localhost:9987/v2/uiDevice/screenshot
1. Take a look at [CulebraTester2 API](https://mrin9.github.io/OpenAPI-Viewer/#/load/https%3A%2F%2Fraw.githubusercontent.com%2Fdtmilano%2FCulebraTester2-public%2Fmaster%2Fopenapi.yaml) or its spec [`openapi.yaml`](https://github.com/dtmilano/CulebraTester2-public/blob/master/openapi.yaml) for more info
1. When done testing, http://localhost:9987/quit will terminate the server

# Want to learn more?
Detailed information can be found in the [CulebraTester2 wiki](https://github.com/dtmilano/CulebraTester2-public/wiki) wiki

# AndroidViewClient
**CulebraTester2** is a new backend for [AndroidViewClient/culebra](https://github.com/dtmilano/AndroidViewClient).

It can be used like other backends, in this case you have to specify the command option

```
-h, --use-uiautomator-helper     use UiAutomatorHelper Android app
```

for example

```sh
$ dump -ah emulator-5554 | jq
⚠️ CulebraTester2 server should have been started and port redirected.
```
```json
{
  "id": "hierarchy",
  "text": "Window Hierarchy",
  "timestamp": "2020-10-12T02:18:45.639Z",
  "children": [
    {
      "id": 0,
      "parent": -1,
      "text": "",
      "package": "com.android.systemui",
      "checkable": false,
      "clickable": false,
      "index": 0,
      "content_description": "",
      "focusable": false,
    ...
```

or set `useuiautomatorhelper=True` when you create a `ViewClient` object.

# culebra
**CulebraTester2** is a new implementation in Kotlin of [culebra](culebra.dtmilano.com).

A python client implementation can be found at **[CulebraTester2-client](https://github.com/dtmilano/CulebraTester2-client)**.

This previous version API specification can be found at [here](https://github.com/dtmilano/CulebraTester-public/wiki/RESTful-API). 

# Example
The script [simple-calculator-test](https://github.com/dtmilano/CulebraTester2-public/blob/master/simple-calculator-test) shows a rudimentary usage of this API by
- starting Calculator activity
- finding one of the digit Buttons, can be specified or a random one is slected
- clicking on that Button

# UI
We mentioned **CulebraTester2** provides an API that facilitates the creation of test automation tools and UI's.

Here we are, this is in the making.

<a href="http://www.youtube.com/watch?feature=player_embedded&v=prE0aKoMLfc" target="_blank"><img src="http://img.youtube.com/vi/prE0aKoMLfc/0.jpg" 
alt="CulebraTester2-ui preview" width="560" height="395" border="1" /></a>

# Communication
Found issues? Use https://github.com/dtmilano/CulebraTester2-public/issues

Have questions? Use https://stackoverflow.com/questions/tagged/androidviewclient.
