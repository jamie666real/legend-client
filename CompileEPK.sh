#!/bin/sh
python3 "$(dirname "$0")/PrepareViaItems.py"
java -jar "desktopRuntime/CompileEPK.jar" "desktopRuntime/resources" "javascript/assets.epk"
