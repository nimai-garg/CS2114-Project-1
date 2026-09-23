// -------------------------------------------------------------------------
/**
 * Class that created an Aircraft object that holds information about the
 * aircraft's name, current fuel amount, fuel capacity, and passenger capacity.
 * 
 * @author Sarah Beatty
 * @version Sep 22, 2026
 */
public class Aircraft
{
    private String name;
    double fuelAmount;
    private double fuelCapacity;
    private int passengerCapacity;

    // ----------------------------------------------------------
    /**
     * Create a new Aircraft object.
     * 
     * @param name
     *            name of the aircraft.
     * @param fuelAmount
     *            current amount of fuel in the aircraft.
     * @param fuelCapacity
     *            total amount of fuel the aircraft can hold.
     * @param passengerCapacity
     *            total number of passengers the aircraft can hold.
     */
    public Aircraft(
        String name,
        double fuelAmount,
        double fuelCapacity,
        int passengerCapacity)
    {
        if (name == null || name.trim().isEmpty()
            || !Double.isFinite(fuelAmount) || fuelAmount < 0
            || !Double.isFinite(fuelCapacity) || fuelCapacity <= 0
            || fuelAmount > fuelCapacity || passengerCapacity <= 0) {
            throw new IllegalArgumentException("Invalid aircraft name or capacity.");
        }
        this.name = name;
        this.fuelAmount = fuelAmount;
        this.fuelCapacity = fuelCapacity;
        this.passengerCapacity = passengerCapacity;
    }


    // ----------------------------------------------------------
    /**
     * Name of the aircraft.
     * 
     * @return Returns name of aircraft.
     */
    public String getName()
    {
        return name;
    }


    // ----------------------------------------------------------
    /**
     * Current amount of fuel in the aircraft.
     * 
     * @return Returns fuel amount.
     */
    public double getFuelAmount()
    {
        return fuelAmount;
    }


    // ----------------------------------------------------------
    /**
     * Total amount of fuel the aircraft can hold.
     * 
     * @return Returns fuel capacity.
     */
    public double getFuelCapacity()
    {
        return fuelCapacity;
    }


    // ----------------------------------------------------------
    /**
     * Total number of passengers the aircraft can hold.
     * 
     * @return Returns passenger capacity.
     */
    public int getPassengerCapacity()
    {
        return passengerCapacity;
    }
    // ----------------------------------------------------------
    /**
     * Checks if the fuel amount is more than fuel amount needed.
     * 
     * @param fuelNeeded
     *            amount of fuel needed for flight.
     * @return Returns true if aircraft has enough fuel and false if not.
     */
    
    public boolean hasEnoughFuel(double fuelNeeded) {
        if (Double.isFinite(fuelNeeded) && fuelNeeded >= 0
            && fuelAmount >= fuelNeeded) {
            return true;
        }
        return false;
    }
    // ----------------------------------------------------------
    /**
     * Checks if passenger count is at or below capacity.
     * 
     * @param passengerCount
     *            total number of passengers on flight.
     * @return Returns true if aircraft can carry passengers and false if not.
     */
    
    public boolean canCarry(int passengerCount) {
        if (passengerCount >= 0 && passengerCount <= passengerCapacity) {
            return true;
        }
        return false;
    }
    // ----------------------------------------------------------
    /**
     * Adds fuel amount given into aircraft up to capacity.
     * 
     * @param amount
     *            amount of fuel to add to aircraft.
     */
    
    public void addFuel(double amount) {
        if (!Double.isFinite(amount) || amount <= 0
            || amount > fuelCapacity - fuelAmount) {
            throw new IllegalArgumentException("Fuel must be positive and fit in the tank.");
        }
        fuelAmount += amount;
    }
}
