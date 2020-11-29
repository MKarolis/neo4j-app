package com.karolismed.neo4j.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Route {

    private List<Connection> connectionList;
    private double totalPrice;
}
