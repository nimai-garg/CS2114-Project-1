public class Flight
{
    // ~ Fields ................................................................
    Aircraft aircraft;
    int flightNumber;
    int flightTime;
    String route;
    String weather;
    int passengerCount;
    double fuelNeeded;
    String status;

    // ~ Constructors ..........................................................
    public Flight(
        Aircraft aircraft, int flightNumber, int flightTime, String route,
        String weather, int passengerCount, double fuelNeeded, String status)
    {
        this.aircraft = aircraft;
        this.flightNumber = flightNumber;
        this.flightTime = flightTime;
        this.route = route;
        this.weather = weather;
        this.passengerCount = passengerCount;
        this.fuelNeeded = fuelNeeded;
        this.status = status;
    }
    // ~Public Methods ........................................................
    public Aircraft getAircraft() {
        return aircraft;
    }
    
    public int getFlightNumber() {
        return flightNumber;
    }

    public int getFlightTime() {
        return flightTime;
    }
    
    public String getRoute() {
        return route;
    }
    
    public String getWeather() {
        return weather;
    }
    
    public int getPassengerCount() {
        return passengerCount;
    }
    
    public double getFuelNeeded() {
        return fuelNeeded;
    }
    
    public String getStatus() {
        return status;
    }
    
    public ArrayList<String> getDispatchProblems() {
        ArrayList<String> problems = new ArrayList<String>;
        if (weather == "Sunny") {
            problems.add("Weather Problem");
        }
        if (passengerCount > aircraft.getPassengerCapacity()) {
            problems.add("Passenger Problem");
        }
        if (aircraft.getFuelAmount() < fuelNeeded) {
            problems.add("Fuel Problem");
        }
        return problems;
    }
    
    public void completeFlight() {
        status = "Completed";
    }
    
    public void delayFlight() {
        status = "Delayed";
    }
    
    public void cancelFlight() {
        status = "Cancelled";
    }
}
