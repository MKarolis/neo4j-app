package com.karolismed.neo4j.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class CityWithConnectionCount extends City {

    private int outgoingConnections;
    private int incomingConnections;
}
