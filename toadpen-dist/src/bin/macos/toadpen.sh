#!/bin/sh
chmod +x jre-macos64/bin/java
chmod +x jre-macos64/lib/jspawnhelper

# generate static cds
if [ ! -f "./jre-macos64/lib/client/classes.jsa" ]; then
    "./jre-macos64/bin/java" -Xshare:dump
fi

CDS_COMMAND=-XX:SharedArchiveFile=app-cds.jsa
if [ ! -f "app-cds.jsa" ]; then
  CDS_COMMAND=-XX:ArchiveClassesAtExit=app-cds.jsa
fi

./jre-macos64/bin/java $CDS_COMMAND \
	-client -Xmx2G \
	-XX:+UseCompressedOops \
	-XX:+UseCompressedClassPointers \
	-cp plugins/*:toadpen-core.jar wrm.toadpen.core.Application
