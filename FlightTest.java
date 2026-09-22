import student.TestCase;

/** Tests dispatch checks and flight status changes. */
public class FlightTest extends TestCase
{
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
