import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import student.TestCase;

public class IntegrationTest extends TestCase
{
    private Dispatcher airline;
    private FlightGame game;

    public void setUp() {
        airline = new Dispatcher(10000, 100, 0);
        game = new FlightGame(airline, new Random(2114), new Scanner(""));
    }

    private void rejects(Runnable action) {
        try {
            action.run();
            fail("Expected invalid action to be rejected");
        }
        catch (IllegalArgumentException | IllegalStateException exception) {
            assertNotNull(exception.getMessage());
        }
    }

    private Flight readyFlight() {
        Flight flight = game.generateFlight();
        flight.weather = "Clear";
        flight.passengerCount = 50;
        flight.flightTime = 60;
        flight.aircraft.fuelAmount = flight.getFuelNeeded();
        return flight;
    }

    public void testAircraftValidation() {
        rejects(() -> new Aircraft(null, 0, 100, 20));
        rejects(() -> new Aircraft(" ", 0, 100, 20));
        rejects(() -> new Aircraft("A", -1, 100, 20));
        rejects(() -> new Aircraft("A", 101, 100, 20));
        rejects(() -> new Aircraft("A", 0, 0, 20));
        rejects(() -> new Aircraft("A", 0, 100, 0));
        rejects(() -> new Aircraft("A", Double.NaN, 100, 20));
        rejects(() -> new Aircraft("A", 0, Double.POSITIVE_INFINITY, 20));
        Aircraft aircraft = new Aircraft("A", 50, 100, 20);
        assertFalse(aircraft.hasEnoughFuel(-1));
        assertFalse(aircraft.hasEnoughFuel(Double.NaN));
        assertFalse(aircraft.hasEnoughFuel(Double.POSITIVE_INFINITY));
        assertFalse(aircraft.canCarry(-1));
        rejects(() -> aircraft.addFuel(Double.NaN));
        rejects(() -> aircraft.addFuel(Double.POSITIVE_INFINITY));
        assertEquals(50.0, aircraft.getFuelAmount(), 0.001);
    }

    public void testFlightConstructorAndGetters() {
        Aircraft aircraft = new Aircraft("A", 50, 100, 20);
        Flight flight = new Flight(1, "ROA to ATL", 20, 50, "Clear", 60, aircraft);
        assertSame(aircraft, flight.getAircraft());
        assertEquals(1, flight.getFlightNumber());
        assertEquals("ROA to ATL", flight.getRoute());
        assertEquals(20, flight.getPassengerCount());
        assertEquals(50.0, flight.getFuelNeeded(), 0.001);
        assertEquals("Clear", flight.getWeather());
        assertEquals(60, flight.getFlightTime());
        assertEquals("Scheduled", flight.getStatus());
        rejects(() -> new Flight(0, "A", 20, 50, "Clear", 60, aircraft));
        rejects(() -> new Flight(1, null, 20, 50, "Clear", 60, aircraft));
        rejects(() -> new Flight(1, "A", -1, 50, "Clear", 60, aircraft));
        rejects(() -> new Flight(1, "A", 20, -1, "Clear", 60, aircraft));
        rejects(() -> new Flight(1, "A", 20, Double.NaN, "Clear", 60, aircraft));
        rejects(() -> new Flight(1, "A", 20, 101, "Clear", 60, aircraft));
        rejects(() -> new Flight(1, "A", 20, 50, null, 60, aircraft));
        rejects(() -> new Flight(1, "A", 20, 50, "Clear", -1, aircraft));
        rejects(() -> new Flight(1, "A", 20, 50, "Clear", 60, null));
        rejects(() -> new Flight(aircraft, 1, 60, "A", "Clear", 20, 50, "Other"));
    }

    public void testTerminalFlightGuardsAndFuelConsumption() {
        Flight flight = readyFlight();
        flight.completeFlight();
        assertEquals(0.0, flight.aircraft.getFuelAmount(), 0.001);
        rejects(() -> flight.completeFlight());
        rejects(() -> flight.cancelFlight());
        rejects(() -> flight.delayFlight());
        Flight cancelled = readyFlight();
        cancelled.cancelFlight();
        rejects(() -> cancelled.completeFlight());
        Flight delayed = readyFlight();
        delayed.delayFlight();
        rejects(() -> delayed.completeFlight());
        Flight unsafe = readyFlight();
        unsafe.weather = "Rain";
        unsafe.aircraft.fuelAmount = 0;
        rejects(() -> unsafe.completeFlight());
        assertEquals("Scheduled", unsafe.getStatus());
    }

    public void testDispatcherAtomicRejectionAndFloor() {
        rejects(() -> airline.applyOutcome(new FlightOutcome(-10001, 10, true, "")));
        rejects(() -> airline.applyOutcome(new FlightOutcome(Integer.MAX_VALUE, 0, true, "")));
        rejects(() -> airline.applyOutcome(new FlightOutcome(0, Integer.MAX_VALUE, true, "")));
        assertEquals(10000, airline.getCash());
        assertEquals(100, airline.getReputation());
        assertEquals(0, airline.getRoundsCompleted());
        airline.applyOutcome(new FlightOutcome(-10000, -200, true, ""));
        assertEquals(0, airline.getCash());
        assertEquals(0, airline.getReputation());
        assertEquals(1, airline.getRoundsCompleted());
    }

    public void testOptionsBadListsAndInput() {
        Options options = new Options();
        rejects(() -> new Options(null));
        rejects(() -> options.getAvailableOptions(null));
        rejects(() -> options.displayOptions(null));
        options.displayOptions(new ArrayList<String>());
        ArrayList<String> available = options.getAvailableOptions(new ArrayList<String>());
        for (String invalid : new String[] {"", " ", "-1", "1.5", "999999999999999999", "Quit"}) {
            assertFalse(options.isValidChoice(invalid, available));
        }
        assertFalse(options.isValidChoice("1", null));
        assertFalse(options.isValidChoice("1", new ArrayList<String>()));
    }

    public void testRejectedGameActionsAreAtomic() {
        Flight flight = readyFlight();
        flight.weather = "Rain";
        flight.aircraft.fuelAmount = 0;
        for (String input : new String[] {null, "", "-1", "999999999999999", "Dispatch", "quit please"}) {
            game.processDecision(input, flight);
        }
        game.processDecision("Delay", null);
        game.processDecision("Delay", new Flight(1, "A", 1, 0, "Clear", 1,
            new Aircraft("A", 0, 100, 20)));
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(0.0, flight.aircraft.getFuelAmount(), 0.001);
        assertEquals(10000, airline.getCash());
        assertEquals(100, airline.getReputation());
        assertEquals(0, airline.getRoundsCompleted());
    }

    public void testUnaffordableFuelAndCancellationThenRecovery() {
        airline = new Dispatcher(0, 100, 0);
        game = new FlightGame(airline, new Random(1), new Scanner(""));
        Flight flight = readyFlight();
        flight.aircraft.fuelAmount = 0;
        game.processDecision("Add Fuel", flight);
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(0.0, flight.aircraft.getFuelAmount(), 0.001);
        assertEquals(0, airline.getCash());
        assertEquals(100, airline.getReputation());
        assertEquals(0, airline.getRoundsCompleted());
        game.processDecision("Delay", flight);
        assertEquals("Delayed", flight.getStatus());
        assertEquals(1, airline.getRoundsCompleted());
    }

    public void testRefuelDispatchAndRepeatedActionAccounting() {
        Flight flight = readyFlight();
        flight.aircraft.fuelAmount = flight.getFuelNeeded() - 10;
        game.processDecision("Add Fuel", flight);
        assertEquals(9985, airline.getCash());
        assertEquals(0, airline.getRoundsCompleted());
        game.processDecision("Add Fuel", flight);
        assertEquals(9985, airline.getCash());
        game.processDecision("Dispatch", flight);
        assertEquals(14185, airline.getCash());
        assertEquals(110, airline.getReputation());
        assertEquals(1, airline.getRoundsCompleted());
        game.processDecision("Dispatch", flight);
        game.processDecision("Cancel", flight);
        assertEquals(1, airline.getRoundsCompleted());
    }

    public void testAffordableCancelAndDelayEffects() {
        Flight flight = readyFlight();
        flight.passengerCount = 20;
        game.processDecision("Cancel", flight);
        assertEquals(10000, airline.getCash());
        assertEquals(88, airline.getReputation());
        assertEquals(1, airline.getRoundsCompleted());
        Flight next = readyFlight();
        next.passengerCount = 20;
        game.processDecision("Delay", next);
        assertEquals(10000, airline.getCash());
        assertEquals(84, airline.getReputation());
        assertEquals(2, airline.getRoundsCompleted());
    }

    public void testTenSuccessfulFlightsAndEarlyEndGame() {
        game.endGame();
        int expectedCash = 10000;
        for (int i = 0; i < 10; i++) {
            Flight flight = readyFlight();
            expectedCash += 4200;
            game.processDecision("Dispatch", flight);
            assertEquals("Completed", flight.getStatus());
        }
        assertEquals(expectedCash, airline.getCash());
        assertEquals(200, airline.getReputation());
        assertEquals(10, airline.getRoundsCompleted());
        Flight extra = readyFlight();
        game.processDecision("Delay", extra);
        assertEquals("Scheduled", extra.getStatus());
        assertEquals(10, airline.getRoundsCompleted());
    }

    public void testQuitPreventsFurtherChanges() {
        Flight flight = readyFlight();
        game.processDecision("quit please", flight);
        game.processDecision("Dispatch", flight);
        assertEquals(1, airline.getRoundsCompleted());
        Flight next = readyFlight();
        game.quitGame();
        game.processDecision("Delay", next);
        assertEquals("Scheduled", next.getStatus());
        assertEquals(1, airline.getRoundsCompleted());
    }

    public void testScenarioRegeneration() {
        Random invalidOnce = new Random(7) {
            private static final long serialVersionUID = 1L;
            private boolean first = true;
            public int nextInt(int bound) {
                if (first) {
                    first = false;
                    return -100;
                }
                return super.nextInt(bound);
            }
        };
        FlightGame regenerated = new FlightGame(airline, invalidOnce, new Scanner(""));
        Flight flight = regenerated.generateFlight();
        assertEquals(1, flight.getFlightNumber());
        assertTrue(flight.getPassengerCount() >= 20);
        assertTrue(flight.getAircraft().getPassengerCapacity() >= 50);
        for (int i = 0; i < 10000; i++) {
            flight = regenerated.generateFlight();
            assertTrue(flight.getFuelNeeded() >= 200);
            assertTrue(flight.getFuelNeeded() <= flight.aircraft.getFuelCapacity());
            assertTrue(flight.aircraft.getFuelAmount() >= 300);
            assertTrue(flight.aircraft.getFuelAmount() <= flight.aircraft.getFuelCapacity());
            assertTrue(flight.getPassengerCount() >= 20);
            assertTrue(flight.getPassengerCount() <= flight.aircraft.getPassengerCapacity());
            assertTrue(flight.getFlightTime() >= 30 && flight.getFlightTime() <= 180);
        }
    }

    public void testDispatchFeedbackListsEveryProblem() {
        Flight flight = readyFlight();
        flight.weather = "Rain";
        flight.passengerCount = flight.aircraft.getPassengerCapacity() + 1;
        flight.aircraft.fuelAmount = 0;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            game.processDecision("Dispatch", flight);
        }
        finally {
            System.setOut(original);
        }
        assertTrue(output.toString().contains("Weather Problem"));
        assertTrue(output.toString().contains("Passenger Problem"));
        assertTrue(output.toString().contains("Fuel Problem"));
        assertEquals(0, airline.getRoundsCompleted());
    }
}