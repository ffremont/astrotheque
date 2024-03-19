#!/bin/sh
# https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jre_aarch64_mac_hotspot_21.0.2_13.tar.gz

cd target
mkdir astrotheque-macos
cp -r ../platforms/macos/aarch64/jdk-21.0.2+13-jre/ astrotheque-macos
cp -r ../dist astrotheque-macos/
cp -r ../dbs astrotheque-macos/
cp astrotheque.jar astrotheque-macos/
cp ../platforms/macos/astrotheque.command astrotheque-macos

zip -vr macos-m1-m2-bundle.zip astrotheque-macos -x "*.DS_Store"

cd ..
