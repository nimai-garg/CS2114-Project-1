import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import student.TestCase;

public class WeatherDispatchTest extends TestCase
{
    private Random weatherRandom(boolean late, int minutes) {
        return new Random(2114) {
            private static final long serialVersionUID = 1L;
            public boolean nextBoolean() { return late; }
            public int nextInt(int bound) {
                return bound == 76 ? minutes - 15 : super.nextInt(bound);
            }
        };
    }

    private Flight flight(String weather) {
        return new Flight(1, "ROA to ATL", 50, 500, weather, 60,
            new Aircraft("A", 600, 1000, 100));
    }

    public void testWeatherMenuIncludesDispatchDelayCancel() {
        ArrayList<String> problems = new ArrayList<String>();
        problems.add("Weather Problem");
        Options options = new Options();
        ArrayList<String> available = options.getAvailableOptions(problems);
        assertEquals(3, available.size());
        assertEquals("Dispatch", options.getChoice("1", available));
        assertEquals("Delay", options.getChoice("2", available));
        assertEquals("Cancel", options.getChoice("3", available));
        problems.add("Fuel Problem");
        assertTrue(options.getAvailableOptions(problems).contains("Dispatch"));
        problems.remove("Fuel Problem");
        problems.add("Passenger Problem");
        assertTrue(options.getAvailableOptions(problems).contains("Dispatch"));
    }

    public void testWeatherOnTimeOutcome() {
        Flight flight = flight("Rain");
        FlightOutcome outcome = FlightOutcome.calculateOutcome("Dispatch", flight,
            weatherRandom(false, 15));
        assertEquals(0, outcome.getArrivalDelayMinutes());
        assertTrue(outcome.getMessage().contains("arrived on time"));
        assertEquals(4200, outcome.getCashChange());
        assertEquals(10, outcome.getReputationChange());
        assertTrue(outcome.doesRoundEnd());
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(600.0, flight.getAircraft().getFuelAmount(), 0.001);
    }

    public void testWeatherLateOutcomeBounds() {
        for (int minutes : new int[] {15, 45, 90}) {
            Flight flight = flight("Windy");
            FlightOutcome outcome = FlightOutcome.calculateOutcome("Dispatch", flight,
                weatherRandom(true, minutes));
            assertEquals(minutes, outcome.getArrivalDelayMinutes());
            assertTrue(outcome.getMessage().contains("arrived " + minutes + " minutes late"));
            assertEquals(1700 - minutes * 15, outcome.getCashChange());
            assertEquals(-(5 + minutes / 10), outcome.getReputationChange());
            assertTrue(outcome.doesRoundEnd());
            flight.completeFlight(outcome.getArrivalDelayMinutes());
            assertEquals("Completed", flight.getStatus());
            assertEquals(minutes, flight.getArrivalDelayMinutes());
            assertEquals(100.0, flight.getAircraft().getFuelAmount(), 0.001);
        }
    }

    public void testClearWeatherAlwaysOnTime() {
        FlightOutcome outcome = FlightOutcome.calculateOutcome("Dispatch", flight("Clear"),
            weatherRandom(true, 90));
        assertEquals(0, outcome.getArrivalDelayMinutes());
        assertTrue(outcome.getMessage().contains("arrived on time"));
    }

    public void testGameRecordsBothWeatherResults() {
        for (boolean late : new boolean[] {false, true}) {
            Dispatcher airline = new Dispatcher(10000, 100, 0);
            FlightGame game = new FlightGame(airline, weatherRandom(late, 45), new Scanner(""));
            Flight flight = game.generateFlight();
            flight.weather = "Rain";
            flight.aircraft.fuelAmount = flight.getFuelNeeded();
            game.processDecision("1", flight);
            assertEquals("Completed", flight.getStatus());
            assertEquals(late ? 45 : 0, flight.getArrivalDelayMinutes());
            assertEquals(0.0, flight.aircraft.getFuelAmount(), 0.001);
            assertEquals(1, airline.getRoundsCompleted());
            assertEquals(10000 + flight.getPassengerCount() * 150 - 1800 - flight.getFlightTime() * 25
                - (late ? flight.getPassengerCount() * 50 + 675 : 0), airline.getCash());
            game.processDecision("Dispatch", flight);
            assertEquals(1, airline.getRoundsCompleted());
        }
    }

    public void testFuelStillRequiredInBadWeather() {
        Dispatcher airline = new Dispatcher(10000, 100, 0);
        FlightGame game = new FlightGame(airline, weatherRandom(true, 90), new Scanner(""));
        Flight flight = game.generateFlight();
        flight.weather = "Rain";
        flight.setFuelPrice(1.50);
        flight.aircraft.fuelAmount = flight.getFuelNeeded() - 10;
        game.processDecision("Dispatch", flight);
        assertEquals("Scheduled", flight.getStatus());
        assertEquals(10000, airline.getCash());
        assertEquals(0, flight.getArrivalDelayMinutes());
        game.processDecision("Add Fuel", flight);
        game.processDecision("Dispatch", flight);
        assertEquals("Completed", flight.getStatus());
        assertEquals(90, flight.getArrivalDelayMinutes());
        assertEquals(9985 + flight.getPassengerCount() * 100 - 1800 - flight.getFlightTime() * 25 - 1350, airline.getCash());
    }

    public void testConsoleLayoutAndSummary() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(("Cancel\n".repeat(10)).getBytes()));
            System.setOut(new PrintStream(output));
            FlightGame.main(new String[0]);
        }
        finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        String text = output.toString();
        assertTrue(text.contains("FLIGHT 1 / 10"));
        assertTrue(text.contains("AVAILABLE ACTIONS"));
        assertTrue(text.contains("Your choice >"));
        assertTrue(text.contains("RESULT"));
        assertTrue(text.contains("FINAL RESULTS"));
        assertFalse(text.toLowerCase().contains("partner"));
        assertFalse(text.contains("TARGET MET"));
        assertFalse(text.contains("TARGET MISSED"));
        assertFalse(text.contains("Goal:"));
        assertFalse(text.contains("worst-case"));
        assertTrue(text.contains("Arrivals on time: 0 | Arrivals late: 0"));
        assertFalse(text.contains("FLIGHT 11"));
    }
}
