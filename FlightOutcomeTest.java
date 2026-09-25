import student.TestCase;

// -------------------------------------------------------------------------

/**
 * Tests FlightOutcome class
 *
 * @author Nolan Ayodeji
 * @version Sep 24, 2026
 */
public class FlightOutcomeTest
    extends TestCase
{
    /**
     * Creates a Flight object for testing.
     *
     * @return a Flight object with test values
     */
    private Flight flight()
    {
        return new Flight(
            1,
            "ROA to ATL",
            50,
            500,
            "Clear",
            60,
            new Aircraft("A", 500, 1000, 100));
    }


    // ----------------------------------------------------------

    /**
     * Tests the FlightOutcome getters.
     */
    public void testGetters()
    {
        FlightOutcome outcome = new FlightOutcome(10, -5, true, "Result");

        assertEquals(10, outcome.getCashChange());
        assertEquals(-5, outcome.getReputationChange());
        assertTrue(outcome.doesRoundEnd());
        assertEquals("Result", outcome.getMessage());
    }


    // ----------------------------------------------------------

    /**
     * Tests the effects of different flight decisions.
     */
    public void testDecisionEffects()
    {
        Flight flight = flight();

        FlightOutcome dispatch =
            FlightOutcome.calculateOutcome(" DISPATCH ", flight);

        assertEquals(4200, dispatch.getCashChange());
        assertEquals(10, dispatch.getReputationChange());
        assertTrue(dispatch.doesRoundEnd());
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(500.0, flight.aircraft.getFuelAmount(), 0.001);

        FlightOutcome delay = FlightOutcome.calculateOutcome("Delay", flight);

        assertEquals(0, delay.getCashChange());
        assertEquals(-4, delay.getReputationChange());
        assertTrue(delay.doesRoundEnd());

        FlightOutcome cancel = FlightOutcome.calculateOutcome("Cancel", flight);

        assertEquals(0, cancel.getCashChange());
        assertEquals(-12, cancel.getReputationChange());
        assertTrue(cancel.doesRoundEnd());
    }


    // ----------------------------------------------------------

    /**
     * Tests fuel costs and ensures the flight is not mutated.
     */
    public void testFuelCostAndNoMutation()
    {
        Flight flight = flight();

        FlightOutcome enough =
            FlightOutcome.calculateOutcome("Add Fuel", flight);

        assertEquals(0, enough.getCashChange());
        assertFalse(enough.doesRoundEnd());

        flight.aircraft.fuelAmount = 499;

        FlightOutcome fuel =
            FlightOutcome.calculateOutcome("Add Fuel", flight);

        assertEquals(-2, fuel.getCashChange());
        assertEquals(0, fuel.getReputationChange());
        assertFalse(fuel.doesRoundEnd());
        assertEquals(499.0, flight.aircraft.getFuelAmount(), 0.001);
    }


    // ----------------------------------------------------------

    /**
     * Tests invalid decisions and flight states.
     */
    public void testInvalidOutcomes()
    {
        for (String decision : new String[] { null, "", "Other" })
        {
            try
            {
                FlightOutcome.calculateOutcome(decision, flight());
                fail("Expected invalid decision rejection");
            }
            catch (IllegalArgumentException exception)
            {
                assertNotNull(exception.getMessage());
            }
        }

        try
        {
            FlightOutcome.calculateOutcome("Dispatch", null);
            fail("Expected null flight rejection");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
        }

        Flight flight = flight();
        flight.weather = "Rain";
        flight.aircraft.fuelAmount = 0;

        try
        {
            FlightOutcome.calculateOutcome("Dispatch", flight);
            fail("Expected unsafe departure rejection");
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals("Scheduled", flight.getStatus());
        }

        flight.cancelFlight();

        try
        {
            FlightOutcome.calculateOutcome("Delay", flight);
            fail("Expected resolved flight rejection");
        }
        catch (IllegalArgumentException exception)
        {
            assertEquals("Cancelled", flight.getStatus());
        }
    }
}