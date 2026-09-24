import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import student.TestCase;

public class FinalBuildTest extends TestCase
{
    private void rejects(Runnable action) {
        try {
            action.run();
            fail("Expected invalid data to be rejected");
        }
        catch (IllegalArgumentException | IllegalStateException exception) {
            assertNotNull(exception.getMessage());
        }
    }

    private Flight flight() {
        return new Flight(1, "ROA to ATL", 50, 500, "Clear", 60,
            new Aircraft("Regional", 490, 1000, 100));
    }

    private String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            action.run();
        }
        finally {
            System.setOut(original);
        }
        return output.toString();
    }

    public void testFuelPriceValidationAndResolvedFlightGuard() {
        Flight flight = flight();
        assertEquals(1.50, flight.getFuelPrice(), 0.001);
        flight.setFuelPrice(2.25);
        assertEquals(2.25, flight.getFuelPrice(), 0.001);
        for (double price : new double[] {-1, 0, 11, Double.NaN, Double.POSITIVE_INFINITY}) {
            rejects(() -> flight.setFuelPrice(price));
            assertEquals(2.25, flight.getFuelPrice(), 0.001);
        }
        flight.cancelFlight();
        rejects(() -> flight.setFuelPrice(3));
    }

    public void testSpikeQuoteRoundingAndAccounting() {
        Flight flight = flight();
        flight.setFuelPrice(2.25);
        FlightOutcome outcome = FlightOutcome.calculateOutcome("Add Fuel", flight);
        assertEquals(-23, outcome.getCashChange());
        assertEquals(0, outcome.getReputationChange());
        assertFalse(outcome.doesRoundEnd());
        assertEquals(490.0, flight.getAircraft().getFuelAmount(), 0.001);
        Dispatcher airline = new Dispatcher(23, 100, 0);
        airline.applyOutcome(outcome);
        assertEquals(0, airline.getCash());
    }

    public void testGeneratedPricesIncludeAllSupportedPrices() {
        FlightGame game = new FlightGame(new Dispatcher(10000, 100, 0),
            new Random(2114), new Scanner(""));
        boolean normal = false;
        boolean medium = false;
        boolean high = false;
        for (int i = 0; i < 1000; i++) {
            double price = game.generateFlight().getFuelPrice();
            assertTrue(price == 1.50 || price == 2.25 || price == 3.00);
            normal |= price == 1.50;
            medium |= price == 2.25;
            high |= price == 3.00;
        }
        assertTrue(normal && medium && high);
    }

    public void testOutcomeHelperValidation() {
        Flight flight = flight();
        assertEquals(4200, FlightOutcome.getDispatchNet(flight));
        assertEquals(0, FlightOutcome.getLateCompensation(flight, 0));
        assertEquals(2725, FlightOutcome.getLateCompensation(flight, 15));
        rejects(() -> FlightOutcome.getDispatchNet(null));
        rejects(() -> FlightOutcome.getLateCompensation(null, 15));
        rejects(() -> FlightOutcome.getLateCompensation(flight, -1));
        rejects(() -> new FlightOutcome(0, 0, false, null));
        rejects(() -> FlightOutcome.calculateOutcome("Delay", flight, null));
    }

    public void testOversizedFuelQuoteRejected() {
        Flight huge = new Flight(1, "A to B", 50, 2000000000.0, "Clear", 60,
            new Aircraft("A", 0, 2000000000.0, 100));
        rejects(() -> FlightOutcome.calculateOutcome("Add Fuel", huge));
        assertEquals(0.0, huge.getAircraft().getFuelAmount(), 0.001);
    }

    public void testOptionsDisplayAndInvalidEntries() {
        Options options = new Options();
        ArrayList<String> menu = options.getAvailableOptions(new ArrayList<String>());
        String display = capture(() -> options.displayOptions(menu));
        assertTrue(display.contains("[1] Dispatch"));
        assertTrue(display.contains("[2] Delay"));
        assertTrue(display.contains("[3] Cancel"));
        assertEquals("", capture(() -> options.displayOptions(new ArrayList<String>())));
        rejects(() -> options.displayOptions(null));
        ArrayList<String> invalid = new ArrayList<String>(Arrays.asList((String)null));
        rejects(() -> new Options(invalid));
        rejects(() -> options.displayOptions(invalid));
        assertFalse(options.isValidChoice("1", invalid));
        rejects(() -> new Options(new ArrayList<String>(Arrays.asList(" "))));
        menu.clear();
        assertEquals(3, options.getAvailableOptions(new ArrayList<String>()).size());
    }

    public void testNegativeCompletionDelayIsAtomic() {
        Flight flight = flight();
        flight.getAircraft().addFuel(10);
        rejects(() -> flight.completeFlight(-1));
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(0, flight.getArrivalDelayMinutes());
        assertEquals(500.0, flight.getAircraft().getFuelAmount(), 0.001);
        flight.completeFlight(25);
        assertEquals(25, flight.getArrivalDelayMinutes());
        assertFalse(flight.canDispatch());
    }

    public void testDispatcherRoundOverflowIsAtomic() {
        Dispatcher airline = new Dispatcher(10, 10, Integer.MAX_VALUE);
        rejects(() -> airline.applyOutcome(new FlightOutcome(5, 5, true, "Done")));
        assertEquals(10, airline.getCash());
        assertEquals(10, airline.getReputation());
        assertEquals(Integer.MAX_VALUE, airline.getRoundsCompleted());
    }

    public void testAccountingOverflowDoesNotCompleteFlight() {
        Dispatcher airline = new Dispatcher(Integer.MAX_VALUE, 100, 0);
        FlightGame game = new FlightGame(airline, new Random(1), new Scanner(""));
        Flight flight = game.generateFlight();
        flight.weather = "Clear";
        flight.passengerCount = 50;
        flight.flightTime = 60;
        flight.aircraft.fuelAmount = flight.fuelNeeded;
        String message = capture(() -> game.processDecision("Dispatch", flight));
        assertTrue(message.contains("accounting limits"));
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(Integer.MAX_VALUE, airline.getCash());
        assertEquals(flight.fuelNeeded, flight.aircraft.fuelAmount, 0.001);
        assertEquals(0, airline.getRoundsCompleted());
    }

    public void testGameConstructorRejectsMissingDependencies() {
        rejects(() -> new FlightGame(null, new Random(), new Scanner("")));
        rejects(() -> new FlightGame(new Dispatcher(0, 0, 0), null, new Scanner("")));
        rejects(() -> new FlightGame(new Dispatcher(0, 0, 0), new Random(), null));
        rejects(() -> new FlightGame(new Dispatcher(0, 0, 1), new Random(), new Scanner("")));
    }

    public void testScenarioRetriesAreBounded() {
        int[] attempts = {0};
        Random invalid = new Random(1) {
            private static final long serialVersionUID = 1L;
            public int nextInt(int bound) {
                attempts[0]++;
                throw new IllegalArgumentException("Invalid scenario source");
            }
        };
        FlightGame game = new FlightGame(new Dispatcher(0, 0, 0), invalid, new Scanner(""));
        rejects(() -> game.generateFlight());
        assertEquals(100, attempts[0]);
    }

    public void testMainRejectsInvalidArgumentsWithoutReadingInput() {
        assertTrue(capture(() -> FlightGame.main(null)).contains("Usage:"));
        assertTrue(capture(() -> FlightGame.main(new String[] {"unknown"})).contains("Usage:"));
        assertTrue(capture(() -> FlightGame.main(new String[] {"--seed", "NaN"}))
            .contains("Seed must be a whole number"));
    }

    private String seededPlay(String input) {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            return capture(() -> FlightGame.main(new String[] {"--seed", "2114"}));
        }
        finally {
            System.setIn(original);
        }
    }

    public void testSeededDemoIsRepeatableAndHandlesInvalidInput() {
        String input = "\nwrong\n-1\n99999999999999999\nDispatch\nAdd Fuel\nDispatch\nQuit\n";
        String first = seededPlay(input);
        assertEquals(first, seededPlay(input));
        assertTrue(first.contains("Please choose an available action"));
        assertTrue(first.contains("RESULT"));
        assertTrue(first.contains("Thanks for playing"));
        assertTrue(first.contains("Fuel price"));
    }

    public void testFinalSummaryAndQuitAreIdempotent() {
        Dispatcher airline = new Dispatcher(10000, 100, 0);
        FlightGame game = new FlightGame(airline, new Random(1), new Scanner(""));
        assertEquals("", capture(() -> game.endGame()));
        String output = capture(() -> {
            for (int i = 0; i < 10; i++) {
                game.processDecision("Cancel", game.generateFlight());
            }
        });
        assertTrue(output.contains("Cancelled: 10"));
        assertTrue(output.contains("FINAL RESULTS"));
        assertEquals("", capture(() -> game.endGame()));
        assertEquals("", capture(() -> game.quitGame()));
        assertEquals(10, airline.getRoundsCompleted());
    }
}
