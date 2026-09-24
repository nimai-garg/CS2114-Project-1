import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import student.TestCase;

/**
 * This is testing the real game classes and console interaction.
 */
public class FlightGameTest extends TestCase
{
    private String play(String input)
    {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            System.setOut(new PrintStream(output));
            FlightGame.main(new String[0]);
            return output.toString();
        }
        finally
        {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    public void testTenFlightsEndGame()
    {
        String input = "";
        for (int i = 0; i < 11; i++)
        {
            input += "Cancel\n";
        }
        String output = play(input);
        assertTrue(output.contains("Flight 10 was cancelled."));
        assertTrue(output.contains("Hokie Air has completed 10 flights."));
        assertFalse(output.contains("Flight 11"));
    }

    public void testQuitAndEndOfInput()
    {
        assertTrue(play("Quit\nCancel\n").contains("Thanks for playing"));
        assertFalse(play("Quit\nCancel\n").contains("was cancelled"));
        assertTrue(play("").contains("Thanks for playing"));
    }

    public void testInvalidInputKeepsCurrentFlight()
    {
        String output = play("nonsense\nDelay\nQuit\n");
        assertTrue(output.contains("Please choose an available action."));
        assertTrue(output.contains("Flight 1 was delayed."));
        assertFalse(output.contains("Flight 2 was delayed."));
    }

    public void testDecisionsAndRepeatedResolution()
    {
        FlightGame game = new FlightGame();
        Flight flight = game.generateFlight();
        flight.weather = "Clear";
        flight.fuelNeeded = flight.getAircraft().getFuelCapacity();
        flight.getAircraft().fuelAmount = 0;
        game.processDecision("Dispatch", flight);
        assertEquals("Scheduled", flight.getStatus());
        game.processDecision("Add Fuel", flight);
        assertEquals(flight.getFuelNeeded(),
            flight.getAircraft().getFuelAmount(), 0.001);
        game.processDecision("1", flight);
        assertEquals("Completed", flight.getStatus());
        game.processDecision("Cancel", flight);
        assertEquals("Completed", flight.getStatus());
    }

    public void testGeneratedFlightLimits()
    {
        FlightGame game = new FlightGame();
        for (int i = 0; i < 100; i++)
        {
            Flight flight = game.generateFlight();
            assertEquals(i + 1, flight.getFlightNumber());
            assertTrue(flight.getPassengerCount()
                <= flight.getAircraft().getPassengerCapacity());
            assertTrue(flight.getFuelNeeded()
                <= flight.getAircraft().getFuelCapacity());
            assertTrue(flight.getAircraft().getFuelAmount()
                <= flight.getAircraft().getFuelCapacity());
        }
    }
}