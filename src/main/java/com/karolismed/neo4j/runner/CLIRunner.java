package com.karolismed.neo4j.runner;

import com.karolismed.neo4j.model.City;
import com.karolismed.neo4j.model.ConnectionType;
import com.karolismed.neo4j.model.Division;
import com.karolismed.neo4j.model.Route;
import com.karolismed.neo4j.service.CityRepository;
import com.karolismed.neo4j.service.IOService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import static java.util.Objects.isNull;

@Component
public class CLIRunner implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final IOService ioService;

    public CLIRunner(CityRepository cityRepository, IOService ioService) {
        this.cityRepository = cityRepository;
        this.ioService = ioService;
    }

    @Override
    public void run(String... args) throws IOException {
        System.out.println("<<<<<<<< Application started successfully >>>>>>>>");
        mainLoop: while (true) {
            showOperations();
            System.out.print("Enter operation's number: ");
            int operation = ioService.readInteger(1, 10);
            switch (operation){
                case 1:
                    findAllCityData();
                    break;
                case 2:
                    findCitiesByCountry();
                    break;
                case 3:
                    findConnectedCitiesByType();
                    break;
                case 4:
                    findReachableCitiesByType();
                    break;
                case 5:
                    findCheapestRoute();
                    break;
                case 6:
                    getDivisions();
                    break;
                case 7:
                    findCitiesManagedByADivision();
                    break;
                case 8:
                    break mainLoop;
            }
            ioService.enterToContinue();
        }
    }

    private void showOperations() throws IOException {
        System.out.println("\n<<<<<<<< Choose your next operation >>>>>>>>");
        System.out.println("1. Get all city data");
        System.out.println("2. Find cities by country");
        System.out.println("3. Find connected cities by transport type");
        System.out.println("4. Find all cities reachable from a city via transport type");
        System.out.println("5. Find cheapest route");
        System.out.println("6. Find all routes");
        System.out.println("7. Find cities managed by a division");
        System.out.println("8. Exit");
    }

    private void findAllCityData() {
        cityRepository.getCitiesWithConnectionCount().forEach(city -> {
            System.out.println(String.format(" - %-10s | %s", city.getName(), city.getCountry()));
            System.out.println(" ".repeat(5) + " - Incoming connections: " + city.getIncomingConnections());
            System.out.println(" ".repeat(5) + " - Outgoing connections: " + city.getOutgoingConnections());
        });
    }

    private void findCitiesByCountry() {
        System.out.print("Enter country's name: ");
        String input = ioService.readLine();

        List<City> cities = cityRepository.getCitiesByCountry(input);

        if (cities.isEmpty()) {
            System.out.println("No connected cities in " + input);
            return;
        }

        System.out.println(String.format("Cities in %s: ", input));
        cities.forEach(
            city -> System.out.println(String.format(" - %s", city.getName()))
        );
    }
    private void findConnectedCitiesByType() {
        System.out.print("Enter city's name: ");
        String cityName = ioService.readLine();

        System.out.print("Enter connection type: ");
        ConnectionType connectionType = ioService.readConnectionType();

        List<City> cities = cityRepository.getCitiesConnectedBy(cityName, connectionType);

        if (cities.isEmpty()) {
            System.out.println("No connected cities found");
        }

        System.out.println("Connecting cities: ");
        cities.forEach(
            city -> System.out.println(String.format(" - %-10s | %s", city.getName(), city.getCountry()))
        );
    }

    private void findCheapestRoute() {
        System.out.print("Enter source city's name: ");
        String source = ioService.readLine();

        System.out.print("Enter destination city's name: ");
        String destination = ioService.readLine();

        Route route = cityRepository.getShortestRoute(source, destination);

        if (isNull(route)) {
            System.out.println(String.format("No routes from %s to %s", source, destination));
            return;
        }

        System.out.println("Cheapest route price: "
            + BigDecimal.valueOf(route.getTotalPrice()).setScale(2, RoundingMode.HALF_UP)
        );
        route.getConnectionList().forEach(
            connection -> {
                String message = String.format(
                    "%-10s ==> %-10s | %6s %s€",
                    connection.getFrom().getName(),
                    connection.getTo().getName(),
                    connection.getType(),
                    connection.getPrice()
                );
                System.out.println(message);
            }
        );
    }

    private void findReachableCitiesByType() {
        System.out.print("Enter city's name: ");
        String cityName = ioService.readLine();

        System.out.print("Enter connection type: ");
        ConnectionType connectionType = ioService.readConnectionType();

        List<City> cities = cityRepository.getCitiesReachableBy(cityName, connectionType);
        if (cities.isEmpty()) {
            System.out.println(String.format("No cities reachable from %s via %s", cityName, connectionType));
            return;
        }

        System.out.println(String.format("Cities reachable from %s via %s: ", cityName, connectionType));
        cities.forEach(
            city -> System.out.println(String.format(" - %-10s | %s", city.getName(), city.getCountry()))
        );
    }

    private void findCitiesManagedByADivision() {
        System.out.print("Enter division's name: ");
        String divisionName = ioService.readLine();

        List<City> cities = cityRepository.getCitiesManagedBy(divisionName);
        if (cities.isEmpty()) {
            System.out.println("No cities managed by " + divisionName);
            return;
        }

        System.out.println(String.format("Cities managed by %s:", divisionName));
        cities.forEach(
            city -> System.out.println(String.format(" - %-10s | %s", city.getName(), city.getCountry()))
        );
    }

    private void getDivisions() {
        List<Division> divisions = cityRepository.getDivisions();

        System.out.println("Divisions: ");
        divisions.forEach(
            division -> System.out.println(String.format("%-7s | Manages %s cities", division.getName(), division.getManagedCityCount()))
        );
    }
}
