# mcbyggnadlib
Minecraft buildings data serialization library for Spigot.

## Add to project
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://www.jitpack.io/</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.Yuukosu</groupId>
    <artifactId>mcbyggnadlib</artifactId>
    <version>2.0.3-SNAPSHOT</version>
</dependency>
```

## Examples

### Create Building Data

```java
import net.yuukosu.Byggnad;
import net.yuukosu.data.ByggnadData;
import org.bukkit.Location;

public Byggnad createByggnad(Location center, Location corner1, Location corner2) {
    return Byggnad.create(center, corner1, corner2);
}
```

### Serialize

```java
import net.yuukosu.Byggnad;

public byte[] serializeBuildingData(Byggnad byggnad) {
    return byggnad.pack();
}
```

### Deserialize

```java
import net.yuukosu.Byggnad;

public Byggnad deserializeBuildingData(byte[] serialized) {
    return Byggnad.unpack(serialized);
}
```

### Save to file

```java
import net.yuukosu.ByggnadSerializerImpl;

import java.io.IOException;

public void save(ByggnadData byggnad) throws IOException {
    ByggnadSerializerImpl.save(byggnad);
}
```

### Load from file

```java
import net.yuukosu.ByggnadSerializerImpl;
import net.yuukosu.ByggnadLib;
import net.yuukosu.Byggnad;

import java.io.File;
import java.io.IOException;

public Byggnad loadBuildingDataFromFile(File file) throws IOException {
    return ByggnadSerializerImpl.load(file);
}
```

### Generate a building

```java
import net.yuukosu.Byggnad;
import org.bukkit.Location;

public void generateBuildingData(Byggnad byggnad, Location center, boolean updateChunks) {
    byggnad.byggnad(center, updateChunks);
}
```