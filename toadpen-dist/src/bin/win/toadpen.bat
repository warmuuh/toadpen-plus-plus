@echo off

if not exist .\jre-win64\lib\client\classes.jsa (
    .\jre-win64\bin\java.exe -Xshare:dump
)

set CDS_COMMAND=-XX:SharedArchiveFile=app-cds.jsa
if not exist "app-cds.jsa" (
  set CDS_COMMAND=-XX:ArchiveClassesAtExit=app-cds.jsa
)

@start .\jre-win64\bin\javaw.exe ^
	%CDS_COMMAND% ^
	-client -Xmx2G ^
	-XX:+UseCompressedOops ^
	-XX:+UseCompressedClassPointers ^
	-cp plugins\*;toadpen-core.jar wrm.toadpen.core.Application
