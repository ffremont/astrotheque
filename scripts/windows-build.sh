#!/bin/sh
# https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jre_x64_windows_hotspot_21.0.2_13.zip

cd target
mkdir astrotheque-windows
cp -r ../platforms/windows/x64/ astrotheque-windows/core/
cp -r ../dist astrotheque-windows/core/
cp -r ../dbs astrotheque-windows/core/
cp astrotheque.jar astrotheque-windows/
cp ../platforms/windows/Astrotheque.bat astrotheque-windows

zip -vr astrotheque-windows-x64-bundle-$VERSION.zip astrotheque-windows -x "*.DS_Store"

cd ..
