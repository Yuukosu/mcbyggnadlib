package net.yuukosu.data.serialization;

import net.yuukosu.data.ByggnadData;

import java.io.InputStream;

public interface ByggnadSerializable {
    byte[] serialize(ByggnadData byggnadData);
    ByggnadData deserialize(InputStream inputStream);
}