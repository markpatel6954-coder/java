public class ParkingLot {

    private int twoWheelers;
    private int fourWheelers;

    private final int twoCap;
    private final int fourCap;

    private static long revenue = 0;

    
    public ParkingLot(int twoCap, int fourCap) {
        this.twoCap = twoCap;
        this.fourCap = fourCap;
        this.twoWheelers = 0;
        this.fourWheelers = 0;
    }

    // Park a vehicle
    public boolean park(String type) {

        if (type.equalsIgnoreCase("two")) {

            if (twoWheelers < twoCap) {
                twoWheelers++;
                revenue += 20;
                System.out.println("Two-wheeler parked.");
                return true;
            } else {
                System.out.println("Full");
                return false;
            }

        } else if (type.equalsIgnoreCase("four")) {

            if (fourWheelers < fourCap) {
                fourWheelers++;
                revenue += 40;
                System.out.println("Four-wheeler parked.");
                return true;
            } else {
                System.out.println("Full");
                return false;
            }

        } else {
            System.out.println("Invalid vehicle type.");
            return false;
        }
    }

    // Vehicle leaves
    public void leave(String type) {

        if (type.equalsIgnoreCase("two")) {

            if (twoWheelers > 0) {
                twoWheelers--;
                System.out.println("Two-wheeler left.");
            } else {
                System.out.println("No two-wheeler to leave.");
            }

        } else if (type.equalsIgnoreCase("four")) {

            if (fourWheelers > 0) {
                fourWheelers--;
                System.out.println("Four-wheeler left.");
            } else {
                System.out.println("No four-wheeler to leave.");
            }

        } else {
            System.out.println("Invalid vehicle type.");
        }
    }

    public static long getRevenue() {
        return revenue;
    }

    public void printOccupancy() {
        System.out.println("Two-wheelers: " + twoWheelers + "/" + twoCap);
        System.out.println("Four-wheelers: " + fourWheelers + "/" + fourCap);
    }

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(2, 2);

        
        lot.park("two");
        lot.park("two");

        
        lot.park("two");

        // Park four-wheelers
        lot.park("four");
        lot.park("four");

      
        lot.park("four");

        // Leave vehicles
        lot.leave("two");
        lot.leave("four");

        
        lot.park("two");
        lot.park("four");

   
        System.out.println("\nFinal Occupancy:");
        lot.printOccupancy();

        
        System.out.println("Revenue: ₹" + ParkingLot.getRevenue());
    }
}