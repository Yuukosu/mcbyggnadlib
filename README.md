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
    <version>2.0.1-SNAPSHOT</version>
</dependency>
```

## Examples

### Create Building Data

```java
import net.yuukosu.data.Byggnad;
import net.yuukosu.data.ByggnadData;
import org.bukkit.Location;

public Byggnad createByggnad(Location center, Location corner1, Location corner2) {
    return Byggnad.create(center, corner1, corner2);
}
```

### Serialize

```java
import net.yuukosu.data.Byggnad;

public byte[] serializeBuildingData(Byggnad byggnad) {
    return byggnad.pack();
}
```

### Deserialize

```java
import net.yuukosu.data.Byggnad;

public Byggnad deserializeBuildingData(byte[] serialized) {
    return Byggnad.unpack(serialized);
}
```

### Save to file

```java
import net.yuukosu.ByggnadLib;
import java.io.IOException;

public void save(ByggnadData byggnad) throws IOException {
    ByggnadLib.save(byggnad);
}
```

### Load from file

```java
import net.yuukosu.ByggnadLib;
import net.yuukosu.data.Byggnad;

import java.io.File;
import java.io.IOException;

public Byggnad loadBuildingDataFromFile(File file) throws IOException {
    return ByggnadLib.load(file);
}
```

### Generate a building

```java
import net.yuukosu.data.Byggnad;
import org.bukkit.Location;

public void generateBuildingData(Byggnad byggnad, Location center, boolean updateChunks) {
    byggnad.byggnad(center, updateChunks);
}
```