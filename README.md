# mcbyggnadlib

[![](https://jitpack.io/v/Yuukosu/mcbyggnadlib.svg)](https://jitpack.io/#Yuukosu/mcbyggnadlib)

A Spigot library for serializing and deserializing Minecraft buildings.  
Save block data from buildings and restore them in different locations.

## Features

- Capture block data from specified regions
- Binary serialization/deserialization
- Save to and load from files
- Generate buildings at any location
- Lightweight and fast processing

## Installation

### Maven

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
    <version>v3.0.2-SNAPSHOT</version>
</dependency>
```

## Usage Examples

### Creating Building Data

Capture block data from a specified region.

```java
import net.yuukosu.Byggnad;
import net.yuukosu.ByggnadFactory;
import net.yuukosu.ByggnadFactoryImpl;
import org.bukkit.Location;

// Create Byggnad from the region defined by corner1 and corner2, centered at center
ByggnadFactory factory = ByggnadFactoryImpl.getInstance();
Byggnad byggnad = factory.create(center, corner1, corner2);
```

### Serialization

Convert building data to a byte array.

```java
import net.yuukosu.ByggnadSerializer;
import net.yuukosu.ByggnadSerializerImpl;

ByggnadSerializer serializer = ByggnadSerializerImpl.getInstance();
byte[] serialized = serializer.toBytes(byggnad);
```

### Deserialization

Restore building data from a byte array.

```java
import net.yuukosu.ByggnadSerializer;
import net.yuukosu.ByggnadSerializerImpl;

ByggnadSerializer serializer = ByggnadSerializerImpl.getInstance();
Byggnad byggnad = serializer.parseFrom(serialized);
```

### Saving to File

```java
import net.yuukosu.ByggnadSerializer;
import net.yuukosu.ByggnadSerializerImpl;
import java.io.FileOutputStream;

ByggnadSerializer serializer = ByggnadSerializerImpl.getInstance();
try (FileOutputStream out = new FileOutputStream("building.dat")) {
    serializer.write(byggnad, out);
}
```

### Loading from File

```java
import net.yuukosu.ByggnadSerializer;
import net.yuukosu.ByggnadSerializerImpl;
import java.io.FileInputStream;

ByggnadSerializer serializer = ByggnadSerializerImpl.getInstance();
try (FileInputStream in = new FileInputStream("building.dat")) {
    Byggnad byggnad = serializer.parseFrom(in);
}
```

### Generating Buildings

Generate saved building data at a specified location.

```java
import net.yuukosu.Byggnad;
import net.yuukosu.ByggnadUtils;
import org.bukkit.Location;

// Generate with chunk updates
ByggnadUtils.generate(byggnad, center);

// Generate without chunk updates
ByggnadUtils.generate(byggnad, center, false);
```

## Reference
* [Javadoc](https://yuukosu.github.io/mcbyggnadlib/)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Bug reports and feature requests are welcome via GitHub Issues.
