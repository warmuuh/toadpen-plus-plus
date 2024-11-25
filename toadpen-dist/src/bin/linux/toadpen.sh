#!/usr/bin/env sh

BASE_DIR="$(cd "$(dirname "$0")"; pwd)" || exit 2

chmod +x "$BASE_DIR"/jre-linux64/bin/java

# generate static cds
if [ ! -f "$BASE_DIR/jre-linux64/lib/client/classes.jsa" ]; then
    "$BASE_DIR/jre-linux64/bin/java" -Xshare:dump
fi

CDS_COMMAND=-XX:SharedArchiveFile=$BASE_DIR/app-cds.jsa
if [ ! -f "$BASE_DIR/app-cds.jsa" ]; then
  CDS_COMMAND=-XX:ArchiveClassesAtExit=$BASE_DIR/app-cds.jsa
fi


"$BASE_DIR"/jre-linux64/bin/java $CDS_COMMAND \
	-client -Xmx2G \
	-XX:+UseCompressedOops \
	-XX:+UseCompressedClassPointers \
  -cp "$BASE_DIR"/plugins/*:"$BASE_DIR"/toadpen-core.jar wrm.toadpen.core.Application
