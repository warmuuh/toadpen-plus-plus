## Native compilation

use bellsoft native image kit 25.0.1.r25-nik

```bash
# run application with 
-agentlib:native-image-agent=config-output-dir=toadpen-core/src/main/resources

# then 
mkdir -p toadpen-core/target/native/agent-output/main
cp toadpen-core/src/main/resources/reachability-metadata.json toadpen-core/target/native/agent-output/main

# compile native image

mvn package -Pnative -pl toadpen-core -DskipTests

# run with
toadpen-core/target/toadpen-core  -Dflatlaf.useNativeLibrary=false
```

### Issues
* cant resize window, throws some native exception
