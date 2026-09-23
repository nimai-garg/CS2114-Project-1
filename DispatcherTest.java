import student.TestCase;

public class DispatcherTest
    extends TestCase
{
    private Dispatcher dispatcher;

    @Override
    public void setUp()
    {
        dispatcher = new Dispatcher(1000, 50, 0);
    }


    public void testConstructorValid()
    {
        assertEquals(1000, dispatcher.getCash());
        assertEquals(50, dispatcher.getReputation());
        assertEquals(0, dispatcher.getRoundsCompleted());
    }


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


    public void testCanAfford()
    {
        assertTrue(dispatcher.canAfford(500));
        assertTrue(dispatcher.canAfford(1000));
        assertTrue(dispatcher.canAfford(0));
        assertFalse(dispatcher.canAfford(1001));
        assertFalse(dispatcher.canAfford(-100));
    }


    public void testApplyOutcomeRoundEnds()
    {
        FlightOutcome outcome = new FlightOutcome(500, 10, true, "Success");
        dispatcher.applyOutcome(outcome);

        assertEquals(1500, dispatcher.getCash());
        assertEquals(60, dispatcher.getReputation());
        assertEquals(1, dispatcher.getRoundsCompleted());
    }


    public void testApplyOutcomeRoundDoesNotEnd()
    {
        FlightOutcome outcome = new FlightOutcome(-200, -5, false, "Refueled");
        dispatcher.applyOutcome(outcome);

        assertEquals(800, dispatcher.getCash());
        assertEquals(45, dispatcher.getReputation());
        assertEquals(0, dispatcher.getRoundsCompleted());
    }


    public void testApplyOutcomeNull()
    {
        dispatcher.applyOutcome(null);

        assertEquals(1000, dispatcher.getCash());
        assertEquals(50, dispatcher.getReputation());
        assertEquals(0, dispatcher.getRoundsCompleted());
    }
}
