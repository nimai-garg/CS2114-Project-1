import java.util.ArrayList;
import student.TestCase;

/**
 * Tests dispatch checks and flight status changes.
 * 
 * @author Sarah Beatty
 * @version Sep 22, 2026
 */
public class FlightTest
    extends TestCase
{
    private Flight flight;
    private Aircraft aircraft;

    // ----------------------------------------------------------
    /**
     * Sets up new Flight object for testing.
     */
    public void setUp()
    {
        aircraft = new Aircraft("Test", 500.00, 1000.00, 100);
        flight = new Flight(
            aircraft,
            1,
            60,
            "ROA to ATL",
            "Sunny",
            90,
            500.00,
            "Scheduled");
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getAircraft() returns correct aircraft from flight.
     */
    public void testGetAircraft()
    {
        assertEquals(aircraft, flight.getAircraft());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getFlightNumber() returns correct number.
     */
    public void testGetFlightNumber()
    {
        assertEquals(1, flight.getFlightNumber());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getFlightTime() returns correct flight time.
     */
    public void testGetFlightTime()
    {
        assertEquals(60, flight.getFlightTime());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getRoute() returns correct flight route.
     */
    public void testGetRoute()
    {
        assertEquals("ROA to ATL", flight.getRoute());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getWeather() returns correct weather condition.
     */
    public void testGetWeather()
    {
        assertEquals("Sunny", flight.getWeather());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getPassengerCount() returns correct passenger count.
     */
    public void testGetPassengerCount()
    {
        assertEquals(90, flight.getPassengerCount());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getFuelNeeded() returns correct fuel amount needed.
     */
    public void testGetFuelNeeded()
    {
        assertEquals(500.00, flight.getFuelNeeded(), 0.01);
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getStatus() returns correct flight status.
     */
    public void testGetStatus()
    {
        assertEquals("Scheduled", flight.getStatus());
    }


    // ----------------------------------------------------------
    /**
     * Tests if .getDispatchProblems() returns correct ArrayList when flight has
     * one problem.
     */
    public void testGetDispatchOneProblem()
    {
        Flight flightBadWeather = new Flight(
            aircraft,
            1,
            60,
            "ROA to ATL",
            "Rain",
            90,
            500.00,
            "Scheduled");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Weather Problem");
        assertEquals(expected, flightBadWeather.getDispatchProblems());

        Flight flightTooManyPassengers = new Flight(
            aircraft,
            1,
            60,
            "ROA to ATL",
            "Sunny",
            150,
            500.00,
            "Scheduled");
        ArrayList<String> expected2 = new ArrayList<>();
        expected2.add("Passenger Problem");
        assertEquals(expected2, flightTooManyPassengers.getDispatchProblems());

        Aircraft aircraft2 = new Aircraft("Test", 400.00, 1000.00, 100);
        Flight flightNotEnoughFuel = new Flight(
            aircraft2,
            1,
            60,
            "ROA to ATL",
            "Sunny",
            90,
            500.00,
            "Scheduled");
        ArrayList<String> expected3 = new ArrayList<>();
        expected3.add("Fuel Problem");
        assertEquals(expected3, flightNotEnoughFuel.getDispatchProblems());

    }


    // ----------------------------------------------------------
    /**
     * Tests if .getDispatchProblems() returns correct ArrayList when flight has
     * two problems.
     */
    public void testGetDispatchTwoProblems()
    {
        Flight flightWeatherPassenger = new Flight(
            aircraft,
            1,
            60,
            "ROA to ATL",
            "Rain",
            150,
            500.00,
            "Scheduled");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Weather Problem");
        expected.add("Passenger Problem");
        assertEquals(expected, flightWeatherPassenger.getDispatchProblems());

        Aircraft aircraft3 = new Aircraft("Test", 400.00, 1000.00, 100);
        Flight flightPassengerFuel = new Flight(
            aircraft3,
            1,
            60,
            "ROA to ATL",
            "Sunny",
            150,
            500.00,
            "Scheduled");
        ArrayList<String> expected2 = new ArrayList<>();
        expected2.add("Passenger Problem");
        expected2.add("Fuel Problem");
        assertEquals(expected2, flightPassengerFuel.getDispatchProblems());

        Aircraft aircraft4 = new Aircraft("Test", 400.00, 1000.00, 100);
        Flight flightWeatherFuel = new Flight(
            aircraft4,
            1,
            60,
            "ROA to ATL",
            "Rain",
            90,
            500.00,
            "Scheduled");
        ArrayList<String> expected3 = new ArrayList<>();
        expected3.add("Weather Problem");
        expected3.add("Fuel Problem");
        assertEquals(expected3, flightWeatherFuel.getDispatchProblems());

    }


    // ----------------------------------------------------------
    /**
     * Tests if .getDispatchProblems() returns correct ArrayList when flight has
     * all three problems.
     */
    public void testDispatchAllProblems()
    {
        Aircraft aircraft5 = new Aircraft("Test", 400.00, 1000.00, 100);
        Flight flightAllProblems = new Flight(
            aircraft5,
            1,
            60,
            "ROA to ATL",
            "Rain",
            150,
            500.00,
            "Scheduled");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Weather Problem");
        expected.add("Passenger Problem");
        expected.add("Fuel Problem");
        assertEquals(expected, flightAllProblems.getDispatchProblems());
    }

// public void testSafeWeatherAndExactLimits()
// {
// assertTrue(Flight("Sunny", 100, 500)
// .getDispatchProblems().isEmpty());
// assertTrue(Flight("Clear", 100, 500).getDispatchProblems().isEmpty());
// }
//
// public void testAllDispatchProblems()
// {
// Flight f = Flight("Rain", 101, 501);
// assertEquals(3, f.getDispatchProblems().size());
// assertTrue(f.getDispatchProblems().contains("Weather Problem"));
// assertTrue(f.getDispatchProblems().contains("Passenger Problem"));
// assertTrue(f.getDispatchProblems().contains("Fuel Problem"));
// }

// public void testRefuelingResolvesFuelProblem()
// {
// Flight f = Flight("Clear", 50, 700);
// assertTrue(f.getDispatchProblems().contains("Fuel Problem"));
// f.getAircraft().addFuel(200);
// assertTrue(f.getDispatchProblems().isEmpty());
// }

// public void testStatuses()
// {
// Flight f = Flight("Clear", 50, 200);
// f.completeFlight();
// assertEquals("Completed", f.getStatus());
// f = Flight("Clear", 50, 200);
// f.delayFlight();
// assertEquals("Delayed", f.getStatus());
// f = Flight("Clear", 50, 200);
// f.cancelFlight();
// assertEquals("Cancelled", f.getStatus());
// }

    private Flight flight(String weather, int passengers, double fuel)
    {
        return new Flight(new Aircraft("Test", 500, 1000, 100),
            1, 60, "ROA to ATL", weather, passengers, fuel, "Scheduled");
    }

    public void testSafeWeatherAndExactLimits()
    {
        assertTrue(flight(new String("Sunny"), 100, 500)
            .getDispatchProblems().isEmpty());
        assertTrue(flight("Clear", 100, 500).getDispatchProblems().isEmpty());
    }

    public void testAllDispatchProblems()
    {
        Flight f = flight("Rain", 101, 501);
        assertEquals(3, f.getDispatchProblems().size());
        assertTrue(f.getDispatchProblems().contains("Weather Problem"));
        assertTrue(f.getDispatchProblems().contains("Passenger Problem"));
        assertTrue(f.getDispatchProblems().contains("Fuel Problem"));
    }

    public void testRefuelingResolvesFuelProblem()
    {
        Flight f = flight("Clear", 50, 700);
        assertTrue(f.getDispatchProblems().contains("Fuel Problem"));
        f.getAircraft().addFuel(200);
        assertTrue(f.getDispatchProblems().isEmpty());
    }

    public void testStatuses()
    {
        Flight f = flight("Clear", 50, 200);
        f.completeFlight();
        assertEquals("Completed", f.getStatus());
        f = flight("Clear", 50, 200);
        f.delayFlight();
        assertEquals("Delayed", f.getStatus());
        f = flight("Clear", 50, 200);
        f.cancelFlight();
        assertEquals("Cancelled", f.getStatus());
    }
}
