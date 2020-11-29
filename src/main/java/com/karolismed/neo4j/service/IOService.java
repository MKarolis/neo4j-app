package com.karolismed.neo4j.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.karolismed.neo4j.model.ConnectionType;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class IOService {
    private final BufferedReader reader;

    public IOService() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public int readInteger(int min, int max) {
        while(true) {
            String input = readLine();

            Integer value;

            try {
                value = Integer.valueOf(input);
            } catch (NumberFormatException e) {
                value = null;
            }

            if (isNull(value) || value < min || value > max) {
                System.out.print(
                    String.format("Please enter a number between %s and %s: ", min, max)
                );
                continue;
            }

            return value;
        }
    }

    public void enterToContinue() {
        System.out.print("<Enter> to continue");
        readLine();
    }

    public ConnectionType readConnectionType() {
        while(true) {
            String input = readLine();
            ConnectionType type = ConnectionType.fromString(input);

            if (nonNull(type)) {
                return type;
            }

            System.out.println(
                "Please enter a valid connection type! Possible values: "
                    + StringUtils.join(ConnectionType.values(), ", "));
            System.out.print("Enter connection type: ");
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Fatal error while reading input data!");
            e.printStackTrace();
            System.exit(1);
        }

        return "";
    }

    @PreDestroy
    private void destroy() throws IOException {
        reader.close();
    }
}
