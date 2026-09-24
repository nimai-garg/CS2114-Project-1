import java.util.ArrayList;

// -------------------------------------------------------------------------
/**
 * Class that creates a Flight object that holds information related to the
 * flight, including the aircraft, flight number, route, passenger count, etc.
 * Also creates list of flight problems by checking for problems with bad
 * weather conditions, too many passengers, and not enough fuel.
 * 
 * @author Sarah Beatty
 * @version Sep 22, 2026
 */
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
    private double fuelPrice = 1.50;

    // ----------------------------------------------------------
    /**
     * Create a new Flight object.
     * 
     * @param aircraft
     *            Aircraft assigned to flight.
     * @param flightNumber
     *            flight's identifying number.
     * @param flightTime
     *            flight length (in minutes).
     * @param route
     *            flight's origin and destination.
     * @param weather
     *            weather condition of origin/destination.
     * @param passengerCount
     *            total passengers on flight.
     * @param fuelNeeded
     *            fuel amount needed for flight.
     * @param status
     *            current status of flight.
     */
    // ~ Constructors ..........................................................
    public Flight(
        Aircraft aircraft,
        int flightNumber,
        int flightTime,
        String route,
        String weather,
        int passengerCount,
        double fuelNeeded,
        String status)
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

    // ----------------------------------------------------------
    /**
     * The aircraft assigned to the flight.
     * 
     * @return Returns Aircraft object.
     */
    public Aircraft getAircraft() {
        return aircraft;
    }


    // ----------------------------------------------------------
    /**
     * The flight's identifying number.
     * 
     * @return Returns flight number.
     */
    public int getFlightNumber()
    {
        return flightNumber;
    }


    // ----------------------------------------------------------
    /**
     * How much time the flight lasts (in minutes).
     * 
     * @return Returns the flight's time.
     */
    public int getFlightTime()
    {
        return flightTime;
    }


    // ----------------------------------------------------------
    /**
     * The flight's route as the origin and destination airports.
     * 
     * @return Returns the flight's route.
     */
    public String getRoute()
    {
        return route;
    }


    // ----------------------------------------------------------
    /**
     * Weather condition of the flight origin/destination.
     * 
     * @return Returns the weather condition.
     */
    public String getWeather()
    {
        return weather;
    }


    // ----------------------------------------------------------
    /**
     * Total number of passengers on the flight.
     * 
     * @return Returns number of passengers.
     */
    public int getPassengerCount()
    {
        return passengerCount;
    }


    // ----------------------------------------------------------
    /**
     * Amount of fuel needed to complete flight.
     * 
     * @return Returns fuel amount needed.
     */
    public double getFuelNeeded()
    {
        return fuelNeeded;
    }


    // ----------------------------------------------------------
    /**
     * Current status of a flight.
     * 
     * @return Returns the flight status.
     */
    public String getStatus()
    {
        return status;
    }


    // ----------------------------------------------------------
    /**
     * Checks for problems with weather, passenger count, and fuel and returns
     * list of problems to create options for user.
     * 
     * @return Returns ArrayList with flight problems.
     */
    public ArrayList<String> getDispatchProblems()
    {
        ArrayList<String> problems = new ArrayList<String>();
        if (!"Sunny".equalsIgnoreCase(weather)
            && !"Clear".equalsIgnoreCase(weather))
        {
            problems.add("Weather Problem");
        }
        if (passengerCount > aircraft.getPassengerCapacity())
        {
            problems.add("Passenger Problem");
        }
        if (aircraft.getFuelAmount() < fuelNeeded)
        {
            problems.add("Fuel Problem");
        }
        return problems;
    }
    
    public double getFuelPrice() {
        return fuelPrice;
    }

    void setFuelPrice(double price) {
        if (!Double.isFinite(price) || price <= 0 || price > 10) {
            throw new IllegalArgumentException("Fuel price must be above $0 and at most $10 per unit.");
        }
        if (!"Scheduled".equals(status)) {
            throw new IllegalStateException("Resolved flight prices cannot change.");
        }
        fuelPrice = price;
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

    // ----------------------------------------------------------
    /**
     * Changes flight status after successfully completing flight.
     */
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
    
    // ----------------------------------------------------------
    /**
     * Changes flight status after delaying flight.
     */
    public void delayFlight() {
        requireScheduled();
        status = "Delayed";
    }
    
    private void requireScheduled() {
        if (!"Scheduled".equals(status)) {
            throw new IllegalStateException("This flight has already been resolved.");
        }
    }

    // ----------------------------------------------------------
    /**
     * Changes flight status after canceling flight.
     */
    public void cancelFlight() {
        requireScheduled();
        status = "Cancelled";
    }
}
