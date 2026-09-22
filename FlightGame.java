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
    private int completedFlights;
    private boolean quit;
    private boolean gameOver;

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
        completedFlights = 0;
        gameOver = false;
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
     * changes. Adding fuel fills the deficit needed by this flight.
     *
     * @param decision the selected number or action
     * @param flight the flight being managed
     */
    public void processDecision(String decision, Flight flight)
    {
        if (gameOver)
        {
            return;
        }
        if (decision == null || decision.trim().isEmpty())
        {
            System.out.println("Please enter a decision.");
            return;
        }
        if ("quit".equalsIgnoreCase(decision.trim()))
        {
            quitGame();
            return;
        }
        if (flight == null || !flights.contains(flight))
        {
            System.out.println("Please choose a current flight.");
            return;
        }
        if (!"Scheduled".equalsIgnoreCase(flight.getStatus()))
        {
            System.out.println("This flight has already been resolved.");
            return;
        }

        ArrayList<String> available = options.getAvailableOptions(
            flight.getDispatchProblems());
        String action = options.getChoice(decision, available);
        if (action == null)
        {
            System.out.println("Please choose an available action.");
            return;
        }
        if ("add fuel".equalsIgnoreCase(action))
        {
            double fuelNeeded = flight.getFuelNeeded();
            double fuelAmount = flight.getAircraft().getFuelAmount();
            double fuelToAdd = fuelNeeded - fuelAmount;
            if (fuelToAdd <= 0)
            {
                System.out.println("This flight does not need more fuel.");
                return;
            }

            FlightOutcome outcome = calculateOutcome(action, flight);
            if (!canApply(outcome))
            {
                return;
            }
            flight.getAircraft().addFuel(fuelToAdd);
            dispatcher.applyOutcome(outcome);
            System.out.println("Added enough fuel for flight "
                + flight.getFlightNumber() + ".");
            displayAirlineStatus();
            return;
        }
        if ("delay".equalsIgnoreCase(action))
        {
            FlightOutcome outcome = calculateOutcome(action, flight);
            if (!canApply(outcome))
            {
                return;
            }
            flight.delayFlight();
            dispatcher.applyOutcome(outcome);
            completedFlights++;
            System.out.println("Flight " + flight.getFlightNumber()
                + " was delayed.");
            displayAirlineStatus();
            endGame();
            return;
        }
        if ("cancel".equalsIgnoreCase(action))
        {
            FlightOutcome outcome = calculateOutcome(action, flight);
            if (!canApply(outcome))
            {
                return;
            }
            flight.cancelFlight();
            dispatcher.applyOutcome(outcome);
            completedFlights++;
            System.out.println("Flight " + flight.getFlightNumber()
                + " was cancelled.");
            displayAirlineStatus();
            endGame();
            return;
        }
        if (!"dispatch".equalsIgnoreCase(action))
        {
            System.out.println("Choose Dispatch, Add Fuel, Delay, or Cancel.");
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

        FlightOutcome outcome = calculateOutcome(action, flight);
        if (!canApply(outcome))
        {
            return;
        }
        flight.completeFlight();
        dispatcher.applyOutcome(outcome);
        completedFlights++;
        System.out.println("Flight " + flight.getFlightNumber()
            + " dispatched successfully.");
        displayAirlineStatus();
        endGame();
    }

    /** Returns the outcome calculated by the shared outcome class. */
    private FlightOutcome calculateOutcome(String action, Flight flight)
    {
        FlightOutcome calculator = new FlightOutcome(0, 0, false, "");
        return calculator.calculateOutcome(action, flight);
    }

    /** Checks affordability before an action changes the flight or airline. */
    private boolean canApply(FlightOutcome outcome)
    {
        int cost = -outcome.getCashChange();
        if (cost > 0 && !dispatcher.canAfford(cost))
        {
            System.out.println("Hokie Air cannot afford that action.");
            return false;
        }
        return true;
    }

    /** Prints the airline values that are carried into later flights. */
    private void displayAirlineStatus()
    {
        System.out.println("Cash: $" + dispatcher.getCash()
            + " | Reputation: " + dispatcher.getReputation());
    }

    /**
     * This is ending the game only once ten flight rounds have been completed
     */
    public void endGame()
    {
        if (!gameOver && completedFlights >= MAX_FLIGHTS)
        {
            gameOver = true;
            System.out.println("Hokie Air has completed 10 flights.");
            displayAirlineStatus();
        }
    }

    /** 
     * This is stopping the loop without terminating the JVM or closing System.in
     */
    public void quitGame()
    {
        if (!gameOver)
        {
            quit = true;
            gameOver = true;
            System.out.println("Thanks for playing Hokie Air.");
        }
    }

    /**
     * This is running the console loop, which retains the same flight after rejected
     * input
     */
    private void play()
    {
        System.out.println("Welcome to Hokie Air: Cleared for Departure!");
        Flight currentFlight = null;
        while (!quit && !gameOver && completedFlights < MAX_FLIGHTS)
        {
            if (currentFlight == null
                || !"Scheduled".equals(currentFlight.getStatus()))
            {
                currentFlight = generateFlight();
            }
            System.out.println("Flight " + currentFlight.getFlightNumber()
                + ": " + currentFlight.getRoute());
            System.out.println("Weather: " + currentFlight.getWeather());
            System.out.println("Passengers: " + currentFlight.getPassengerCount()
                + "/" + currentFlight.getAircraft().getPassengerCapacity());
            System.out.println("Fuel: " + currentFlight.getAircraft().getFuelAmount()
                + "; needed: " + currentFlight.getFuelNeeded());
            displayAirlineStatus();
            for (String problem : currentFlight.getDispatchProblems())
            {
                System.out.println(problem);
            }
            options.displayOptions(options.getAvailableOptions(
                currentFlight.getDispatchProblems()));
            System.out.println("Choose an action, or type Quit.");
            if (!scanner.hasNextLine())
            {
                quitGame();
                return;
            }
            String decision = scanner.nextLine().trim();
            if ("quit".equalsIgnoreCase(decision))
            {
                quitGame();
                return;
            }
            processDecision(decision, currentFlight);
        }
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
