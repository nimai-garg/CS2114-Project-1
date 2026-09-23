public class Aircraft
{
    // ~ Fields ................................................................
    String name;
    double fuelAmount;
    double fuelCapacity;
    int passengerCapacity;

    // ~ Constructors ..........................................................
    public Aircraft(
        String name, double fuelAmount, double fuelCapacity,
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
    // ~Public Methods ........................................................
    public String getName() {
        return name;
    }
    
    public double getFuelAmount() {
        return fuelAmount;
    }
    
    public double getFuelCapacity() {
        return fuelCapacity;
    }
    
    public int getPassengerCapacity() {
        return passengerCapacity;
    }
    
    public boolean hasEnoughFuel(double fuelNeeded) {
        if (Double.isFinite(fuelNeeded) && fuelNeeded >= 0
            && fuelAmount >= fuelNeeded) {
            return true;
        }
        return false;
    }
    
    public boolean canCarry(int passengerCount) {
        if (passengerCount >= 0 && passengerCount <= passengerCapacity) {
            return true;
        }
        return false;
    }
    
    public void addFuel(double amount) {
        if (!Double.isFinite(amount) || amount <= 0
            || amount > fuelCapacity - fuelAmount) {
            throw new IllegalArgumentException("Fuel must be positive and fit in the tank.");
        }
        fuelAmount += amount;
    }
}
