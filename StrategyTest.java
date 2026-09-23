import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import student.TestCase;

public class StrategyTest extends TestCase
{
    private Flight flight(int passengers, int minutes, String weather) {
        return new Flight(1, "ROA to ATL", passengers, 500, weather, minutes,
            new Aircraft("A", 600, 1000, 100));
    }

    public void testDispatchAlwaysFirstForEveryProblemCombination() {
        for (int mask = 0; mask < 8; mask++) {
            ArrayList<String> problems = new ArrayList<String>();
            if ((mask & 1) != 0) { problems.add("Weather Problem"); }
            if ((mask & 2) != 0) { problems.add("Fuel Problem"); }
            if ((mask & 4) != 0) { problems.add("Passenger Problem"); }
            Options options = new Options();
            ArrayList<String> menu = options.getAvailableOptions(problems);
            assertEquals("Dispatch", options.getChoice("1", menu));
            assertTrue(menu.contains("Delay"));
            assertTrue(menu.contains("Cancel"));
        }
    }

    public void testScenarioEconomicsRewardDifferentDecisions() {
        Flight busyShort = flight(90, 40, "Clear");
        assertEquals(10700, FlightOutcome.getDispatchNet(busyShort));
        Flight quietLong = flight(20, 180, "Clear");
        assertEquals(-3300, FlightOutcome.getDispatchNet(quietLong));
        assertEquals(0, FlightOutcome.calculateOutcome("Cancel", quietLong).getCashChange());
        Flight storm = flight(50, 60, "Rain");
        assertEquals(4200, FlightOutcome.getDispatchNet(storm));
        assertEquals(350, FlightOutcome.getDispatchNet(storm)
            - FlightOutcome.getLateCompensation(storm, 90));
    }

    public void testDelayLeavesCashAndFuelUnchanged() {
        Dispatcher airline = new Dispatcher(10000, 100, 0);
        FlightGame game = new FlightGame(airline, new Random(5), new Scanner(""));
        Flight flight = game.generateFlight();
        flight.passengerCount = 50;
        flight.flightTime = 60;
        flight.aircraft.fuelAmount = 0;
        flight.weather = "Rain";
        game.processDecision("Delay", flight);
        assertEquals("Delayed", flight.getStatus());
        assertEquals(10000, airline.getCash());
        assertEquals(96, airline.getReputation());
        assertEquals(1, airline.getRoundsCompleted());
        assertEquals(0.0, flight.aircraft.getFuelAmount(), 0.001);
    }

    public void testDispatchDoesNotRequireWorstCaseCash() {
        Random random = new Random(8) {
            private static final long serialVersionUID = 1L;
            public boolean nextBoolean() { return false; }
        };
        Dispatcher airline = new Dispatcher(0, 100, 0);
        FlightGame game = new FlightGame(airline, random, new Scanner(""));
        Flight flight = game.generateFlight();
        flight.passengerCount = 30;
        flight.flightTime = 60;
        flight.weather = "Rain";
        flight.aircraft.fuelAmount = flight.fuelNeeded;
        game.processDecision("1", flight);
        assertEquals("Completed", flight.getStatus());
        assertEquals(1200, airline.getCash());
        assertEquals(110, airline.getReputation());
        assertEquals(1, airline.getRoundsCompleted());
    }

    public void testActualUnaffordableOutcomeStillRejected() {
        Random random = new Random(8) {
            private static final long serialVersionUID = 1L;
            public boolean nextBoolean() { return true; }
        };
        Dispatcher airline = new Dispatcher(0, 100, 0);
        FlightGame game = new FlightGame(airline, random, new Scanner(""));
        Flight flight = game.generateFlight();
        flight.passengerCount = 30;
        flight.flightTime = 60;
        flight.weather = "Rain";
        flight.aircraft.fuelAmount = flight.fuelNeeded;
        game.processDecision("1", flight);
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(0, airline.getCash());
        assertEquals(100, airline.getReputation());
        assertEquals(0, airline.getRoundsCompleted());
        assertEquals(flight.fuelNeeded, flight.aircraft.fuelAmount, 0.001);
        game.processDecision("Delay", flight);
        assertEquals("Delayed", flight.getStatus());
        assertEquals(96, airline.getReputation());
    }

    public void testNumericDispatchWithFuelProblemChangesNothing() {
        Dispatcher airline = new Dispatcher(10000, 100, 0);
        FlightGame game = new FlightGame(airline, new Random(8), new Scanner(""));
        Flight flight = game.generateFlight();
        flight.aircraft.fuelAmount = 0;
        game.processDecision("1", flight);
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(10000, airline.getCash());
        assertEquals(0, airline.getRoundsCompleted());
        assertEquals(0.0, flight.aircraft.getFuelAmount(), 0.001);
    }
}
