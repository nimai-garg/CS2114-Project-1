import java.util.ArrayList;

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
    private int arrivalDelayMinutes;

    // ~ Constructors ..........................................................
    public Flight(
        Aircraft aircraft, int flightNumber, int flightTime, String route,
        String weather, int passengerCount, double fuelNeeded, String status)
    {
        if (aircraft == null || flightNumber <= 0 || flightTime <= 0
            || route == null || route.trim().isEmpty()
            || weather == null || weather.trim().isEmpty()
            || passengerCount < 0 || !Double.isFinite(fuelNeeded)
            || fuelNeeded < 0 || fuelNeeded > aircraft.getFuelCapacity()
            || !("Scheduled".equals(status) || "Delayed".equals(status)
                || "Cancelled".equals(status) || "Completed".equals(status))) {
            throw new IllegalArgumentException("Invalid flight data.");
        }
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
    public Flight(int flightNumber, String route, int passengerCount,
        double fuelNeeded, String weather, int flightTime, Aircraft aircraft) {
        this(aircraft, flightNumber, flightTime, route, weather,
            passengerCount, fuelNeeded, "Scheduled");
    }

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
        ArrayList<String> problems = new ArrayList<String>();
        if (!"Sunny".equalsIgnoreCase(weather)
            && !"Clear".equalsIgnoreCase(weather)) {
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
    
    public boolean hasWeatherProblem() {
        return getDispatchProblems().contains("Weather Problem");
    }

    public boolean canDispatch() {
        ArrayList<String> problems = getDispatchProblems();
        problems.remove("Weather Problem");
        return "Scheduled".equals(status) && problems.isEmpty();
    }

    public int getArrivalDelayMinutes() {
        return arrivalDelayMinutes;
    }

    public void completeFlight() {
        completeFlight(0);
    }

    public void completeFlight(int delayMinutes) {
        requireScheduled();
        if (delayMinutes < 0) {
            throw new IllegalArgumentException("Arrival delay cannot be negative.");
        }
        if (!canDispatch()) {
            throw new IllegalStateException("Flight cannot depart with dispatch problems.");
        }
        arrivalDelayMinutes = delayMinutes;
        aircraft.fuelAmount -= fuelNeeded;
        status = "Completed";
    }
    
    public void delayFlight() {
        requireScheduled();
        status = "Delayed";
    }
    
    private void requireScheduled() {
        if (!"Scheduled".equals(status)) {
            throw new IllegalStateException("This flight has already been resolved.");
        }
    }

    public void cancelFlight() {
        requireScheduled();
        status = "Cancelled";
    }
}
