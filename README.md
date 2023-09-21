# Java CSV

Fast, zero-dependency, declarative CSV file reader for Java.

## Features

- Read CSV records directly into Java objects.
- Automatic type parsing.
- Returns records in a Stream.
- Based on the RFC 4180 CSV format.
- Support for quoted fields and escaped double quotes.

## Usage

Usage examples are using the [Electric Vehicle Population Data](https://catalog.data.gov/dataset/electric-vehicle-population-data) CSV data.

Extended example code can be found [here](lib/src/example/).

[`Electric_Vehicle_Population_Data.csv`](lib/src/example/resources/Electric_Vehicle_Population_Data.csv)

```csv
VIN (1-10) ,County ,City    ,State ,Postal Code ,Model Year ,Make    ,Model          ,Electric Vehicle Type                  ,Clean Alternative Fuel Vehicle (CAFV) Eligibility ,Electric Range ,Base MSRP ,Legislative District ,DOL Vehicle ID ,Vehicle Location                ,Electric Utility                              ,2020 Census Tract
KM8K33AGXL ,King   ,Seattle ,WA    ,      98103 ,      2020 ,HYUNDAI ,KONA           ,Battery Electric Vehicle (BEV)         ,Clean Alternative Fuel Vehicle Eligible           ,           258 ,        0 ,                  43 ,     249675142 ,POINT (-122.34301 47.659185)    ,CITY OF SEATTLE - (WA)|CITY OF TACOMA - (WA)  ,      53033004800
1C4RJYB61N ,King   ,Bothell ,WA    ,      98011 ,      2022 ,JEEP    ,GRAND CHEROKEE ,Plug-in Hybrid Electric Vehicle (PHEV) ,Not eligible due to low battery range             ,            25 ,        0 ,                   1 ,     233928502 ,POINT (-122.20578 47.762405)    ,PUGET SOUND ENERGY INC||CITY OF TACOMA - (WA) ,      53033021804
1C4RJYD61P ,Yakima ,Yakima  ,WA    ,      98908 ,      2023 ,JEEP    ,GRAND CHEROKEE ,Plug-in Hybrid Electric Vehicle (PHEV) ,Not eligible due to low battery range             ,            25 ,        0 ,                  14 ,     229675939 ,POINT (-120.6027202 46.5965625) ,PACIFICORP                                    ,      53077002900
```
[`App.java`](lib/src/example/java/App.java)

```java
BufferedReader reader = Files.newBufferedReader(Path.of("Electric_Vehicle_Population_Data.csv"));
List<Vehicle> vehicles = new CSV<>(Vehicle::new).stream(reader).toList();
```


[`Vehicle.java`](lib/src/example/java/Vehicle.java)

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

## To Do

- [x] Switch to VarHandles
- [x] Add unit testing
- [ ] Implement proper error handling
- [ ] Improve documentation
- [ ] Re-implement custom header parsing