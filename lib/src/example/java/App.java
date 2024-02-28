import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import dev.nathanrsxtn.csv.CSV;

public class App {
    /**
     * Read the CSV file and return a list cars newer than 2022 (inclusive), ordered alphabetically by
     * state.
     */
    public List<Vehicle> getVehicles() throws IOException {
        // Use a try block to ensure reader is closed.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("Electric_Vehicle_Population_Data.csv")))) {

            // Create a CSV instance by providing the constructor for the record class.
            CSV<Vehicle> csv = new CSV<>(Vehicle::new);

            // Create a stream of CSV records from the file.
            // The records are not read from the file until a terminal stream operation is performed.
            Stream<Vehicle> recordStream = csv.stream(reader);

            // Perform any necessary intermediary stream operations.

            // Filter out any vehicles older than 2022.
            recordStream = recordStream.filter(vehicle -> vehicle.modelYear >= 2022);

            // Sort by vehicle electric range.
            recordStream = recordStream.sorted(Comparator.comparing(vehicle -> vehicle.state));

            // Create a list from the record stream.
            // Performing a terminal stream operation will read, parse, and close the CSV file.
            List<Vehicle> vehicles = recordStream.toList();

            // Return the list of vehicles.
            return vehicles;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Vehicle> vehicles = new App().getVehicles();
        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }
}
