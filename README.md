# dev.nathanrsxtn.csv

Fast, zero-dependency, declarative CSV file reader for Java.

## Features

- Read CSV records directly into Java objects.
- Automatic type parsing.
- Returns records in a Stream.
- Based on the RFC 4180 CSV format.
- Support for quoted fields and escaped double quotes.

## Usage

### [Full Example](https://github.com/nathanrsxtn/java-csv/tree/main/lib/src/example)

#### App.java

```java
Path path = Path.of("Electric_Vehicle_Population_Data.csv");
BufferedReader reader = Files.newBufferedReader(path);
CSV<Vehicle> csv = new CSV<>(Vehicle::new);
List<Vehicle> vehicles = csv.stream(reader).toList();
```

#### Vehicle.java

```java
public class Vehicle {
    public int postalCode;
    public int modelYear;
    public String make;
    public String model;
    public int electricRange;
    public Point vehicleLocation;
    public State state;
}
```

#### Electric_Vehicle_Population_Data.csv

Source: [Electric Vehicle Population Data](https://catalog.data.gov/dataset/electric-vehicle-population-data)
```csv
VIN (1-10) ,County ,City    ,State ,Postal Code ,Model Year ,Make    ,Model          ,Electric Vehicle Type                  ,Clean Alternative Fuel Vehicle (CAFV) Eligibility ,Electric Range ,Base MSRP ,Legislative District ,DOL Vehicle ID ,Vehicle Location                ,Electric Utility                              ,2020 Census Tract
KM8K33AGXL ,King   ,Seattle ,WA    ,      98103 ,      2020 ,HYUNDAI ,KONA           ,Battery Electric Vehicle (BEV)         ,Clean Alternative Fuel Vehicle Eligible           ,           258 ,        0 ,                  43 ,     249675142 ,POINT (-122.34301 47.659185)    ,CITY OF SEATTLE - (WA)|CITY OF TACOMA - (WA)  ,      53033004800
1C4RJYB61N ,King   ,Bothell ,WA    ,      98011 ,      2022 ,JEEP    ,GRAND CHEROKEE ,Plug-in Hybrid Electric Vehicle (PHEV) ,Not eligible due to low battery range             ,            25 ,        0 ,                   1 ,     233928502 ,POINT (-122.20578 47.762405)    ,PUGET SOUND ENERGY INC||CITY OF TACOMA - (WA) ,      53033021804
1C4RJYD61P ,Yakima ,Yakima  ,WA    ,      98908 ,      2023 ,JEEP    ,GRAND CHEROKEE ,Plug-in Hybrid Electric Vehicle (PHEV) ,Not eligible due to low battery range             ,            25 ,        0 ,                  14 ,     229675939 ,POINT (-120.6027202 46.5965625) ,PACIFICORP                                    ,      53077002900
```

## Installation

### [Repository GitHub Packages](https://github.com/nathanrsxtn/java-csv/packages/)

Releases are automatically published to GitHub Packages.
You can download the JAR and add it to your classpath or include it as a dependency using your preferred build tools.
