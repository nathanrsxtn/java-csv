/**
 * The class used to store data from a CSV record.
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
     * Enums are supported automatically, as they inherit a static valueOf(String) function from
     * java.lang.Enum.
     */
    public enum State {
        AK, AL, AP, AR, AZ, BC, CA, CO, CT, DC, DE, FL, GA, HI, ID, IL, IN, KS, KY, LA, MA, MD, MN, MO, MS, MT, NC, NE, NH, NJ, NV, NY, OH, OR, PA, SC, TX, UT, VA, WA, WY
    }

    @Override
    public String toString() {
        return String.format("Vehicle [postalCode=%s, modelYear=%s, make=%s, model=%s, electricRange=%s, vehicleLocation=%s, state=%s]", postalCode, modelYear, make, model, electricRange, vehicleLocation, state);
    }
}