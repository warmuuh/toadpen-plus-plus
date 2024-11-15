## Native compilation

use bellsoft native image kit 24.1.1.r23-nik
```bash
# run application with 
-agentlib:native-image-agent=config-output-dir=asd-core/src/main/resources

# then 
mkdir -p asd-core/target/native/agent-output/main
cp asd-core/src/main/resources/reachability-metadata.json asd-core/target/native/agent-output/main

# compile native image
mvn package -Pnative

# run with
asd-core/target/asd-core  -Dflatlaf.useNativeLibrary=false
```

### Issues
* cant resize window, throws some native exception
