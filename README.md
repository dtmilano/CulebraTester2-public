<a href="#"><img src="https://github.com/dtmilano/AndroidViewClient/wiki/images/culebra-logo-transparent-204x209-rb-border.png" align="left" hspace="0" vspace="6"></a>

# CulebraTester2-public
CulebraTester: Snaky Android Testing


  ⚠️ Warning    |
:------------------|
**This is an alpha version of CulebraTester2** |



# How to run CulebraTester2 ?
1. Build and install the APK `./gradlew installDebug`
1. Forward the port `./culebratester2 forward-port`
1. Run the instrumentation `./culebratester2 run-instrumentation`
1. Open http://localhost:9987/ with a browser or `curl` 
1. You should see `CulebraTester2: Go to http://localhost:<port>/help for usage details.`
1. If the previous request worked, you can try something more ambitious as http://localhost:9987/v2/uiDevice/screenshot
