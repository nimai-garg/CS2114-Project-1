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
        this(new Dispatcher(10000, 100, 0), new Random(), new Scanner(System.in));
    }

    FlightGame(Dispatcher dispatcher, Random random, Scanner scanner)
    {
        if (dispatcher == null || random == null || scanner == null
            || dispatcher.getRoundsCompleted() != 0) {
            throw new IllegalArgumentException("A fresh airline, random source and input are required.");
        }
        flights = new ArrayList<Flight>();
        this.dispatcher = dispatcher;
        options = new Options();
        this.random = random;
        this.scanner = scanner;
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
        for (int attempt = 0; attempt < 100; attempt++) {
            try {
                Flight flight = createScenario();
                flights.add(flight);
                return flight;
            }
            catch (IllegalArgumentException exception) {
                if (attempt == 99) {
                    throw new IllegalStateException("Unable to generate a valid flight.", exception);
                }
            }
        }
        throw new IllegalStateException("Unable to generate a valid flight.");
    }

    private Flight createScenario()
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
        if (random.nextInt(4) == 0) {
            flight.setFuelPrice(random.nextBoolean() ? 2.25 : 3.00);
        }
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
            System.out.println("Please enter a displayed number or action, for example Delay.");
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

        if ("dispatch".equalsIgnoreCase(decision.trim())
            && !flight.canDispatch()) {
            System.out.println("This flight cannot be dispatched yet.");
            for (String problem : flight.getDispatchProblems()) {
                System.out.println(problem);
            }
            return;
        }
        ArrayList<String> available = options.getAvailableOptions(
            flight.getDispatchProblems());
        String action = options.getChoice(decision, available);
        if (action == null)
        {
            System.out.println("Please choose an available action. Enter 1 through "
                + available.size() + ", or an action name such as Delay.");
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
            displayResult("Added enough fuel for flight "
                + flight.getFlightNumber() + ".", outcome);
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
            displayResult(outcome.getMessage(), outcome);
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
            displayResult(outcome.getMessage(), outcome);
            endGame();
            return;
        }
        if (!"dispatch".equalsIgnoreCase(action))
        {
            System.out.println("Choose Dispatch, Add Fuel, Delay, or Cancel.");
            return;
        }

        ArrayList<String> problems = flight.getDispatchProblems();
        if (!flight.canDispatch())
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
        flight.completeFlight(outcome.getArrivalDelayMinutes());
        dispatcher.applyOutcome(outcome);
        completedFlights++;
        displayResult(outcome.getMessage(), outcome);
        endGame();
    }

    /** Returns the outcome calculated by the shared outcome class. */
    private FlightOutcome calculateOutcome(String action, Flight flight)
    {
        return FlightOutcome.calculateOutcome(action, flight, random);
    }

    /** Checks affordability before an action changes the flight or airline. */
    private boolean canApply(FlightOutcome outcome)
    {
        long cost = -(long)outcome.getCashChange();
        if (cost > Integer.MAX_VALUE
            || (cost > 0 && !dispatcher.canAfford((int)cost)))
        {
            System.out.println("Hokie Air cannot afford that action. Cost: $"
                + cost + "; available cash: $" + dispatcher.getCash() + ".");
            return false;
        }
        if ((long)dispatcher.getCash() + outcome.getCashChange() > Integer.MAX_VALUE
            || (long)dispatcher.getReputation() + outcome.getReputationChange() > Integer.MAX_VALUE) {
            System.out.println("This action exceeds the airline's supported accounting limits.");
            return false;
        }
        return true;
    }

    /** Prints the airline values that are carried into later flights. */
    private void displayAirlineStatus()
    {
        System.out.printf(java.util.Locale.US,
            "  Cash: $%,d   |   Reputation: %d   |   Resolved: %d / %d%n",
            dispatcher.getCash(), dispatcher.getReputation(), completedFlights, MAX_FLIGHTS);
    }

    /**
     * This is ending the game only once ten flight rounds have been completed
     */
    public void endGame()
    {
        if (!gameOver && completedFlights >= MAX_FLIGHTS)
        {
            gameOver = true;
            System.out.println("\n============================================================");
            System.out.println("  FINAL RESULTS");
            System.out.println("  Hokie Air has completed 10 flights.");
            displayAirlineStatus();
            int departed = 0;
            int delayed = 0;
            int cancelled = 0;
            int late = 0;
            for (Flight flight : flights) {
                if ("Completed".equals(flight.getStatus())) {
                    departed++;
                    if (flight.getArrivalDelayMinutes() > 0) { late++; }
                }
                if ("Delayed".equals(flight.getStatus())) { delayed++; }
                if ("Cancelled".equals(flight.getStatus())) { cancelled++; }
            }
            System.out.println("Departed: " + departed + " | Delayed: "
                + delayed + " | Cancelled: " + cancelled);
            System.out.println("  Arrivals on time: " + (departed - late)
                + " | Arrivals late: " + late);
            System.out.println("============================================================");
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
        System.out.println("\n============================================================");
        System.out.println("          HOKIE AIR: CLEARED FOR DEPARTURE");
        System.out.println("============================================================");
        System.out.println("  Manage 10 flights. Choose a number or an action name.");
        System.out.println("  Dispatch earns fares but pays operating costs and late compensation.");
        System.out.println("  Delay ends the round with no cash change and -4 reputation.");
        System.out.println("  Cancel protects cash, earns nothing, and costs 12 reputation.");
        System.out.println("  Fuel starts at $1.50/unit; market spikes may raise it to $2.25 or $3.00.");
        System.out.println("  Weather risk: 50% on time, 50% late by 15-90 minutes.");
        System.out.println("  Type Quit at any prompt to leave the game.");
        Flight currentFlight = null;
        while (!quit && !gameOver && completedFlights < MAX_FLIGHTS)
        {
            if (currentFlight == null
                || !"Scheduled".equals(currentFlight.getStatus()))
            {
                currentFlight = generateFlight();
            }
            displayFlight(currentFlight);
            System.out.print("\n  Your choice > ");
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

    private void displayResult(String message, FlightOutcome outcome)
    {
        System.out.println("\n------------------------- RESULT ---------------------------");
        System.out.println("  " + message);
        System.out.printf(java.util.Locale.US, "  Cash change: $%+,d | Reputation change: %+d%n",
            outcome.getCashChange(), outcome.getReputationChange());
        displayAirlineStatus();
        System.out.println("------------------------------------------------------------\n");
    }

    private void displayFlight(Flight flight)
    {
        System.out.println("\n============================================================");
        System.out.println("  FLIGHT " + flight.getFlightNumber() + " / " + MAX_FLIGHTS
            + "    " + flight.getRoute());
        System.out.println("============================================================");
        displayAirlineStatus();
        System.out.println("------------------------------------------------------------");
        System.out.printf("  %-15s %s%n", "Aircraft", flight.getAircraft().getName());
        System.out.printf("  %-15s %d minutes%n", "Flight time", flight.getFlightTime());
        System.out.printf("  %-15s %d / %d%n", "Passengers", flight.getPassengerCount(),
            flight.getAircraft().getPassengerCapacity());
        System.out.printf(java.util.Locale.US, "  %-15s %,.0f available / %,.0f needed%n", "Fuel",
            flight.getAircraft().getFuelAmount(), flight.getFuelNeeded());
        System.out.printf("  %-15s %s%n", "Weather", flight.getWeather());
        System.out.printf(java.util.Locale.US, "  %-15s $%.2f per unit%s%n", "Fuel price",
            flight.getFuelPrice(), flight.getFuelPrice() > 1.50 ? "  [PRICE SPIKE]" : "");
        System.out.println("------------------------------------------------------------");
        if (flight.hasWeatherProblem()) {
            System.out.println("  WEATHER RISK: Dispatch may arrive on time or 15-90 min late.");
        }
        if (flight.getDispatchProblems().contains("Fuel Problem")) {
            System.out.println("  FUEL REQUIRED: Add fuel before dispatching.");
        }
        if (flight.getDispatchProblems().contains("Passenger Problem")) {
            System.out.println("  OVER CAPACITY: Too many passengers to dispatch.");
        }
        if (flight.getDispatchProblems().isEmpty()) {
            System.out.println("  READY: All departure requirements are met.");
        }
        System.out.println("\n  AVAILABLE ACTIONS");
        options.displayOptions(options.getAvailableOptions(flight.getDispatchProblems()));
        System.out.println("  [Quit] End game");
        int fuelCost = (int)Math.ceil(Math.max(0,
            flight.getFuelNeeded() - flight.getAircraft().getFuelAmount()) * flight.getFuelPrice());
        if (fuelCost > 0) {
            System.out.printf(java.util.Locale.US, "\n  Fuel purchase: $%,d%n", fuelCost);
        }
        int net = FlightOutcome.getDispatchNet(flight);
        System.out.println("\n  DECISION TRADEOFFS (net cash after operating costs)");
        System.out.printf(java.util.Locale.US,
            "  Dispatch on time: $%+,d | +10 reputation%n", net);
        if (flight.hasWeatherProblem()) {
            System.out.printf(java.util.Locale.US,
                "  Dispatch late:    $%+,d to $%+,d | -6 to -14 reputation%n",
                net - FlightOutcome.getLateCompensation(flight, 90),
                net - FlightOutcome.getLateCompensation(flight, 15));
        }
        System.out.println("  Delay:             $0 | -4 reputation | ends the round");
        System.out.println("  Cancel:            $0 | -12 reputation | preserves cash");
        if (fuelCost > 0) {
            System.out.println("  Dispatch figures exclude the fuel purchase shown above.");
        }

    }

    /**
     * This is starting the game
     * 
     * @param args unused command-line arguments
     */
    public static void main(String[] args)
    {
        if (args == null || (args.length != 0
            && !(args.length == 2 && "--seed".equals(args[0])))) {
            System.out.println("Usage: java FlightGame [--seed whole-number], for example --seed 2114");
            return;
        }
        if (args.length == 2) {
            try {
                long seed = Long.parseLong(args[1]);
                new FlightGame(new Dispatcher(10000, 100, 0), new Random(seed),
                    new Scanner(System.in)).play();
            }
            catch (NumberFormatException exception) {
                System.out.println("Seed must be a whole number, for example --seed 2114.");
            }
            return;
        }
        new FlightGame().play();
    }
}
