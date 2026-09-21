import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This is the main controller for Hokie Air
 *
 * @author Nimai Garg
 */
public class FlightGame
{
    private static final int MAX_FLIGHTS = 10;
    private ArrayList<Flight> flights;
    private Dispatcher dispatcher;
    private Options options;
    private Random random;
    private Scanner scanner;

    /**
     * This creates a game that is reading from the console
     */
    public FlightGame()
    {
        flights = new ArrayList<Flight>();
        dispatcher = new Dispatcher(10000, 100, 0);
        options = new Options();
        random = new Random();
        scanner = new Scanner(System.in);
    }

    /**
     * This method is generating and remembering one valid scenario.
     * Insufficient current fuel and severe weather are intentional
     * dispatch problems; negative values or fuel requirements beyond
     * the tank are impossible
     *
     * @return a new scheduled flight
     */
    public Flight generateFlight()
    {
        return null;
    }

    /**
     * This is validating a menu number or action name before applying any
     * changes. FlightOutcome owns pricing, reputation effects, and roun
     * completion. The add Fuel fills the deficit needed by this flight
     *
     * @param decision the selected number or action
     * @param flight the flight being managed
     */
    public void processDecision(String decision, Flight flight)
    {
        if (decision == null || decision.trim().isEmpty())
        {
            System.out.println("Please enter a decision.");
        }
    }

    /**
     * This is ending the game only once ten flight rounds have been completed
     */
    public void endGame()
    {
        if (flights.size() >= MAX_FLIGHTS)
        {
            System.out.println("Hokie Air has completed 10 flights.");
        }
    }

    /** 
     * This is stopping the loop without terminating the JVM or closing System.in
     */
    public void quitGame()
    {
        System.out.println("Thanks for playing Hokie Air.");
    }

    /**
     * This is running the console loop, which retains the same flight after rejected
     * input
     */
    private void play()
    {
        System.out.println("Welcome to Hokie Air: Cleared for Departure!");
        while (flights.size() < MAX_FLIGHTS && scanner.hasNextLine())
        {
            String decision = scanner.nextLine().trim();
            if ("quit".equalsIgnoreCase(decision))
            {
                quitGame();
                return;
            }
        }
        endGame();
    }

    /**
     * This is starting the game
     * 
     * @param args unused command-line arguments
     */
    public static void main(String[] args)
    {
        new FlightGame().play();
    }
}