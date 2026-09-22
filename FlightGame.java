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
        String[] routes = {"ROA to ATL", "ROA to CLT", "ROA to IAD"};
        String[] weather = {"Clear", "Rain", "Windy"};

        int passengerCapacity = 50 + random.nextInt(51);
        int passengerCount = 20 + random.nextInt(
            passengerCapacity - 19);
        double fuelCapacity = 1000 + random.nextInt(1001);
        double fuelAmount = 300 + random.nextInt(
            (int)fuelCapacity - 299);
        double fuelNeeded = 200 + random.nextInt(
            (int)fuelCapacity - 199);
        int flightTime = 30 + random.nextInt(151);

        Aircraft aircraft = new Aircraft("Hokie Regional", fuelAmount,
            fuelCapacity, passengerCapacity);
        Flight flight = new Flight(aircraft, flights.size() + 1, flightTime,
            routes[random.nextInt(routes.length)],
            weather[random.nextInt(weather.length)], passengerCount,
            fuelNeeded, "Scheduled");
        flights.add(flight);
        return flight;
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
            return;
        }
        if (flight == null || !flights.contains(flight))
        {
            System.out.println("Please choose a current flight.");
            return;
        }

        String action = decision.trim();
        if (!"dispatch".equalsIgnoreCase(action))
        {
            System.out.println("Choose Dispatch or Quit.");
            return;
        }

        ArrayList<String> problems = flight.getDispatchProblems();
        if (!problems.isEmpty())
        {
            System.out.println("This flight cannot be dispatched yet.");
            for (String problem : problems)
            {
                System.out.println(problem);
            }
            return;
        }

        flight.completeFlight();
        System.out.println("Flight " + flight.getFlightNumber()
            + " dispatched successfully.");
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
