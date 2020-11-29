package com.karolismed.neo4j.model;

import java.util.Arrays;

public enum ConnectionType {
    BUS, FERRY, PLANE, TRAIN;

    public static ConnectionType fromString(String str) {
        return Arrays.stream(ConnectionType.values())
            .filter(type -> type.toString().equalsIgnoreCase(str))
            .findFirst()
            .orElse(null);
    }
}
