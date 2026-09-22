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
        if (fuelAmount >= fuelNeeded) {
            return true;
        }
        return false;
    }
    
    public boolean canCarry(int passengerCount) {
        if (passengerCount <= passengerCapacity) {
            return true;
        }
        return false;
    }
    
    public void addFuel(double amount) {
        if (amount <= 0) {
            return;
        }
        fuelAmount = Math.min(fuelCapacity, fuelAmount + amount);
    }
}
