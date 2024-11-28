#!/usr/bin/env bash
rm -r target/appbundle
jpackage \
  --type app-image \
  --verbose \
  --dest target/appbundle \
  --input ../toadpen-core/target/ \
  --resource-dir src/bin/macos-appbundle/ \
  --name "Toadpen++" \
  --main-jar toadpen-core-1.0-SNAPSHOT.jar \
  --main-class wrm.toadpen.core.Application \
  --icon src/bin/macos-bundle/Toadpen.app/Contents/Resources/Toadpen++.icns \
  --module-path  /Users/peter.mucha/.sdkman/candidates/java/21.0.1-tem/jmods \
  --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.naming,java.prefs,java.scripting,java.sql,java.xml \
  --mac-package-name "Toadpen++" \
  --mac-package-identifier toadpen \
  --java-options -Xmx2048m


