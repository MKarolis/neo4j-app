package com.karolismed.neo4j.service;

import com.karolismed.neo4j.model.City;
import com.karolismed.neo4j.model.CityWithConnectionCount;
import com.karolismed.neo4j.model.Connection;
import com.karolismed.neo4j.model.ConnectionType;
import com.karolismed.neo4j.model.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Repository;


@Repository
public class CityRepository {

    private Session session;

    public CityRepository(Driver driver) {
        this.session = driver.session();
    }

    public List<City> getCitiesByCountry(String country) {
        Map <String, Object> params = Map.of("country", country);

        return session.readTransaction(
            tx -> tx.run("MATCH (c:City {country: $country}) RETURN c", params)
                .list(r -> mapRecordToCity(r, "c"))
        );
    }

    public List<City> getCitiesConnectedBy(String originCityName, ConnectionType connectionType) {
        String query = String.format(
            "MATCH (A:City {name: \"%s\"})-[:%s]->(B:City) RETURN B", originCityName, connectionType
        );

        return session.readTransaction(
            tx -> tx.run(query).list(r -> mapRecordToCity(r, "B"))
        );
    }

    public Route getShortestRoute(String fromCityName, String toCityName) {
        Map <String, Object> params = Map.of(
            "from", fromCityName,
            "to", toCityName
        );
        String query = "MATCH (A:City {name: $from}) "
            + "MATCH (B:City {name: $to}) "
            + "CALL apoc.algo.dijkstra(A, B, 'TRAIN>|FERRY>|BUS>|PLANE>', 'price') "
            + "YIELD path, weight RETURN path, weight";

        List<Route> routes = session.readTransaction(
            tx -> tx.run(query, params).list(r -> mapRecordToRoute(r, "path"))
        );

        return routes.isEmpty() ? null : routes.get(0);
    }

    public List<City> getCitiesReachableBy(String originCityName, ConnectionType connectionType) {
        String query = String.format(
            "MATCH (origin:City {name: \"%s\"}) " +
            "CALL apoc.path.subgraphNodes(origin, { " +
            "    relationshipFilter: \"%s>\", " +
            "    labelFilter: \"City\", " +
            "    minLevel: 1 " +
            "}) " +
            "YIELD node " +
            "RETURN node ORDER BY node.country", originCityName, connectionType
        );

        return session.readTransaction(
            tx -> tx.run(query).list(r -> mapRecordToCity(r, "node"))
        );
    }

    public List<CityWithConnectionCount> getCitiesWithConnectionCount() {
        String query =
            "MATCH (A:City)-[rel]-(B: City) " +
            "RETURN A AS node, " +
            "       SUM(CASE WHEN STARTNODE(rel) = A THEN 1 ELSE 0 END) AS sum_out, " +
            "       SUM(CASE WHEN STARTNODE(rel) = B THEN 1 ELSE 0 END) AS sum_in " +
            "ORDER BY A.country DESC";

        return session.readTransaction(
            tx -> tx.run(query).list(r -> mapRecordToCityWithConnectionCount(r, "node"))
        );
    }

    private Route mapRecordToRoute(Record record, String alias) {
        Path path = record.get(alias).asPath();
        List<Connection> connections = new ArrayList<>();

        path.forEach(segment -> {
            Relationship relation = segment.relationship();
            Connection connection = Connection.builder()
                .from(mapNodeToCity(segment.start()))
                .to(mapNodeToCity(segment.end()))
                .price(relation.get("price").asDouble())
                .type(ConnectionType.fromString(relation.type()))
                .build();
            connections.add(connection);
        });

        return Route.builder()
            .connectionList(connections)
            .totalPrice(record.get("weight").asDouble())
            .build();
    }

    private City mapRecordToCity(Record record, String alias) {
        Node node = record.get(alias).asNode();
        return mapNodeToCity(node);
    }

    private CityWithConnectionCount mapRecordToCityWithConnectionCount(
        Record record, String nodeAlias
    ) {
        Node node = record.get(nodeAlias).asNode();
        return CityWithConnectionCount.builder()
            .name(node.get("name").asString())
            .country(node.get("country").asString())
            .incomingConnections(record.get("sum_in").asInt())
            .outgoingConnections(record.get("sum_out").asInt())
            .build();
    }

    private City mapNodeToCity(Node node) {
        return City.builder()
            .name(node.get("name").asString())
            .country(node.get("country").asString())
            .build();
    }
}
