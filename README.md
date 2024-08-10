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
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Examples

### Create Building Data

```java
import net.yuukosu.data.ByggnadData;
import org.bukkit.Location;

public ByggnadData createByggnadData(Location center, Location corner1, Location corner2) {
    return ByggnadData.create(center, corner1, corner2);
}
```

### Serialize

```java
import net.yuukosu.data.ByggnadData;
import net.yuukosu.data.serialization.ByggnadSerializer;

public byte[] serializeBuildingData(ByggnadData byggnadData) {
    return ByggnadSerializer.getInstance().serialize(byggnadData);
}
```

### Deserialize

```java
import net.yuukosu.data.ByggnadData;

public ByggnadData deserializeBuildingData(byte[] serialized) {
    return ByggnadSerializer.getInstance().deserialize(serialized);
}
```

### Save to file

```java
import net.yuukosu.ByggnadLib;
import java.io.IOException;

public void save(ByggnadData byggnadData) throws IOException {
    ByggnadLib.save(byggnadData);
}
```

### Load from file

```java
import net.yuukosu.ByggnadLib;
import net.yuukosu.data.ByggnadData;
import java.io.File;
import java.io.IOException;

public ByggnadData loadBuildingDataFromFile(File file) throws IOException {
    return ByggnadLib.load(file);
}
```

### Generate a building

```java
import net.yuukosu.data.ByggnadData;
import org.bukkit.Location;

public void generateBuildingData(ByggnadData byggnadData, Location center) {
    byggnadData.byggnad(center);
}
```