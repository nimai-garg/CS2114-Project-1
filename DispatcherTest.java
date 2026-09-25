import student.TestCase;

// -------------------------------------------------------------------------

/**
 * Tests Dispatcher class
 *
 * @author Nolan Ayodeji
 * @version Sep 23, 2026
 */
public class DispatcherTest
    extends TestCase
{
    private Dispatcher dispatcher;

    // ----------------------------------------------------------

    /**
     * Sets up the Dispatcher class for testing.
     */
    @Override
    public void setUp()
    {
        dispatcher = new Dispatcher(1000, 50, 0);
    }


    // ----------------------------------------------------------

    /**
     * Tests valid constructor values.
     */
    public void testConstructorValid()
    {
        assertEquals(1000, dispatcher.getCash());
        assertEquals(50, dispatcher.getReputation());
        assertEquals(0, dispatcher.getRoundsCompleted());
    }


    // ----------------------------------------------------------

    /**
     * Tests negative constructor values.
     */
    public void testConstructorNegativeValues()
    {
        try
        {
            new Dispatcher(-1, 50, 0);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }

        try
        {
            new Dispatcher(1000, -10, 0);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }

        try
        {
            new Dispatcher(1000, 50, -1);
            fail();
        }
        catch (IllegalArgumentException e)
        {
        }
    }


    // ----------------------------------------------------------

    /**
     * Tests if the airline can afford certain amounts of cash.
     */
    public void testCanAfford()
    {
        assertTrue(dispatcher.canAfford(500));
        assertTrue(dispatcher.canAfford(1000));
        assertTrue(dispatcher.canAfford(0));
        assertFalse(dispatcher.canAfford(1001));
        assertFalse(dispatcher.canAfford(-100));
    }


    // ----------------------------------------------------------

    /**
     * Tests a scenario where the outcome causes the round to end.
     */
    public void testApplyOutcomeRoundEnds()
    {
        FlightOutcome outcome =
            new FlightOutcome(500, 10, true, "Success");

        dispatcher.applyOutcome(outcome);

        assertEquals(1500, dispatcher.getCash());
        assertEquals(60, dispatcher.getReputation());
        assertEquals(1, dispatcher.getRoundsCompleted());
    }


    // ----------------------------------------------------------

    /**
     * Tests a scenario where the outcome causes the round to continue.
     */
    public void testApplyOutcomeRoundDoesNotEnd()
    {
        FlightOutcome outcome =
            new FlightOutcome(-200, -5, false, "Refueled");

        dispatcher.applyOutcome(outcome);

        assertEquals(800, dispatcher.getCash());
        assertEquals(45, dispatcher.getReputation());
        assertEquals(0, dispatcher.getRoundsCompleted());
    }


    // ----------------------------------------------------------

    /**
     * Tests a scenario where the outcome is null.
     */
    public void testApplyOutcomeNull()
    {
        try
        {
            dispatcher.applyOutcome(null);
            fail("Expected null outcome to be rejected");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
        }

        assertEquals(1000, dispatcher.getCash());
        assertEquals(50, dispatcher.getReputation());
        assertEquals(0, dispatcher.getRoundsCompleted());
    }
}