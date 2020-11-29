package com.karolismed.neo4j.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Connection {

    private City from;
    private City to;
    private ConnectionType type;
    private double price;
}
