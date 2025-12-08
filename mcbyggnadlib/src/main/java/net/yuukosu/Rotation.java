package net.yuukosu;

import java.util.Arrays;
import java.util.Optional;

public enum Rotation {
    EAST,
    WEST,
    SOUTH,
    NORTH;

    private static final Rotation[] VALUES;

    static {
        VALUES = Rotation.values();
    }

    public static Optional<Rotation> fromId(int ordinal) {
        if (ordinal < 0 || VALUES.length <= ordinal) return Optional.empty();

        return Optional.of(VALUES[ordinal]);
    }

    public static Optional<Rotation> fromName(String name) {
        return Arrays.stream(VALUES)
                .filter(rotation -> rotation.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
