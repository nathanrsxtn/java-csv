# Java CSV

Fast, zero-dependency, declarative CSV file reader for Java.

## Usage

Usage examples are using the [Electric Vehicle Population Data](https://catalog.data.gov/dataset/electric-vehicle-population-data) CSV data.

`Electric_Vehicle_Population_Data.csv`

```csv
VIN (1-10) ,County ,City    ,State ,Postal Code ,Model Year ,Make    ,Model          ,Electric Vehicle Type                  ,Clean Alternative Fuel Vehicle (CAFV) Eligibility ,Electric Range ,Base MSRP ,Legislative District ,DOL Vehicle ID ,Vehicle Location                ,Electric Utility                              ,2020 Census Tract
KM8K33AGXL ,King   ,Seattle ,WA    ,      98103 ,      2020 ,HYUNDAI ,KONA           ,Battery Electric Vehicle (BEV)         ,Clean Alternative Fuel Vehicle Eligible           ,           258 ,        0 ,                  43 ,     249675142 ,POINT (-122.34301 47.659185)    ,CITY OF SEATTLE - (WA)|CITY OF TACOMA - (WA)  ,      53033004800
1C4RJYB61N ,King   ,Bothell ,WA    ,      98011 ,      2022 ,JEEP    ,GRAND CHEROKEE ,Plug-in Hybrid Electric Vehicle (PHEV) ,Not eligible due to low battery range             ,            25 ,        0 ,                   1 ,     233928502 ,POINT (-122.20578 47.762405)    ,PUGET SOUND ENERGY INC||CITY OF TACOMA - (WA) ,      53033021804
1C4RJYD61P ,Yakima ,Yakima  ,WA    ,      98908 ,      2023 ,JEEP    ,GRAND CHEROKEE ,Plug-in Hybrid Electric Vehicle (PHEV) ,Not eligible due to low battery range             ,            25 ,        0 ,                  14 ,     229675939 ,POINT (-120.6027202 46.5965625) ,PACIFICORP                                    ,      53077002900
...
```
`App.java`

```java
public class App {
    /**
     * Read the CSV file and return a list cars newer than 2022 (inclusive), ordered alphabetically by state.
     */
    public static List<Vehicle> getVehicles() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of("Electric_Vehicle_Population_Data.csv"))) {
            return new CSV<>(Vehicle::new).stream(reader)
                .filter(vehicle -> vehicle.modelYear >= 2022)
                .sorted(Comparator.comparing(vehicle -> vehicle.state))
                .toList();
        }
    }

    /**
     * A commented version of App#getVehicles()
     */
    public static List<Vehicle> getVehiclesCommented() throws IOException {
        // Use a try block to ensure reader is closed.
        try (BufferedReader reader = Files.newBufferedReader(Path.of("Electric_Vehicle_Population_Data.csv"))) {
    
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
}
```

`Vehicle.java`

```java
/**
 * A class representing a record from the CSV file.
 * Columns from the CSV file are associated with the public non-final fields in the record class. Not all columns from the CSV need to be present in the record class.
 * This class can be nested, but must remain accessible (public and static).
 */
public class Vehicle {
    public int postalCode;
    public int modelYear;
    public String make;
    public String model;
    public int electricRange;
    public Point vehicleLocation;
    public State state;

    /**
     * A parameterless constructor for the CSV reader to create record objects with. This will be passed
     * to the CSV constructor as a method reference.
     */
    public Vehicle() {
    }

    /**
     * The CSV reader will attempt to parse objects by locating a static valueOf(String) function. The
     * record keyword is used for brevity, classes are also supported.
     */
    public static record Point(double latitude, double longitude) {
        public static Point valueOf(String s) {
            String[] coordinates = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(" ");
            return new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
        }
    }

    /**
     * Enums are supported automatically, as they inherit a static valueOf(String) function from java.lang.Enum.
     */
    public enum State { AK, AL, AP, AR, AZ, BC, CA, CO, CT, DC, DE, FL, GA, HI, ID, IL, IN, KS, KY, LA, MA, MD, MN, MO, MS, MT, NC, NE, NH, NJ, NV, NY, OH, OR, PA, SC, TX, UT, VA, WA, WY }
}
```
