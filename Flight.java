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
    private Aircraft aircraft;
    private int flightNumber;
    private int flightTime;
    private String route;
    private String weather;
    private int passengerCount;
    private double fuelNeeded;
    private String status;

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
        this.aircraft = aircraft;
        this.flightNumber = flightNumber;
        this.flightTime = flightTime;
        this.route = route;
        this.weather = weather;
        this.passengerCount = passengerCount;
        this.fuelNeeded = fuelNeeded;
        this.status = status;
    }


    // ----------------------------------------------------------
    /**
     * The aircraft assigned to the flight.
     * 
     * @return Returns Aircraft object.
     */
    // ~Public Methods ........................................................
    public Aircraft getAircraft()
    {
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


    // ----------------------------------------------------------
    /**
     * Changes flight status after successfully completing flight.
     */
    public void completeFlight()
    {
        status = "Completed";
    }


    // ----------------------------------------------------------
    /**
     * Changes flight status after delaying flight.
     */
    public void delayFlight()
    {
        status = "Delayed";
    }


    // ----------------------------------------------------------
    /**
     * Changes flight status after canceling flight.
     */
    public void cancelFlight()
    {
        status = "Cancelled";
    }
}
