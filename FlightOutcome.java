// -------------------------------------------------------------------------
/**
 * Figures out what happens after the player makes a decision about a flight. It
 * calculates the stats to pass along to the dispatcher.
 * 
 * @author Nolan Ayodeji
 * @version Sep 23, 2026
 */
public class FlightOutcome
{
    private int cashChange;
    private int reputationChange;
    private boolean roundEnds;
    private String message;
    private int arrivalDelayMinutes;

    /**
     * Makes a new FlightOutcome holding the results of a player's choice.
     * 
     * @param cashChange
     *            how much money the player makes or loses
     * @param reputationChange
     *            how the player's reputation changes
     * @param roundEnds
     *            true if this choice ends the current round
     * @param message
     *            the feedback message shown to the player
     * @throws IllegalArgumentException
     *             if the message is null
     */
    public FlightOutcome(
        int cashChange,
        int reputationChange,
        boolean roundEnds,
        String message)
    {
        if (message == null)
        {
            throw new IllegalArgumentException("Feedback is required.");
        }
        this.cashChange = cashChange;
        this.reputationChange = reputationChange;
        this.roundEnds = roundEnds;
        this.message = message;
    }


    /**
     * Gets the change in the player's cash.
     * 
     * @return the cash change
     */
    public int getCashChange()
    {
        return this.cashChange;
    }


    /**
     * Gets the change in the player's reputation.
     * 
     * @return the reputation change
     */
    public int getReputationChange()
    {
        return this.reputationChange;
    }


    /**
     * Checks if this choice ends the current round.
     * 
     * @return true if the round is over, false if it keeps going
     */
    public boolean doesRoundEnd()
    {
        return this.roundEnds;
    }


    /**
     * Gets the feedback message to show the player.
     * 
     * @return the message string
     */
    public String getMessage()
    {
        return this.message;
    }


    /**
     * Figures out the profit for sending out a flight. It subtracts operating
     * costs from the ticket money made from passengers.
     * 
     * @param flight
     *            the flight we're checking
     * @return the total profit from the flight
     * @throws IllegalArgumentException
     *             if the flight is missing
     */
    public static int getDispatchNet(Flight flight)
    {
        if (flight == null)
        {
            throw new IllegalArgumentException("Flight is required.");
        }
        return Math.subtractExact(
            Math.multiplyExact(flight.getPassengerCount(), 150),
            Math.addExact(
                1800,
                Math.multiplyExact(flight.getFlightTime(), 25)));
    }


    /**
     * Calculates the penalty we have to pay passengers when a flight is late.
     * 
     * @param flight
     *            the late flight
     * @param minutes
     *            how many minutes late it was
     * @return the total penalty cost, or 0 if it wasn't late
     * @throws IllegalArgumentException
     *             if the flight is missing or minutes are negative
     */
    public static int getLateCompensation(Flight flight, int minutes)
    {
        if (flight == null || minutes < 0)
        {
            throw new IllegalArgumentException(
                "A flight and nonnegative delay are required.");
        }
        if (minutes == 0)
        {
            return 0;
        }
        return Math.addExact(
            Math.multiplyExact(flight.getPassengerCount(), 50),
            Math.multiplyExact(minutes, 15));
    }


    /**
     * Gets how many minutes late the flight was.
     * 
     * @return the delay in minutes
     */
    public int getArrivalDelayMinutes()
    {
        return arrivalDelayMinutes;
    }


    /**
     * Calculates what happens based on the player's choice for a flight. Uses a
     * standard random number generator to figure out weather delays.
     * 
     * @param decision
     *            the player's choice (like "dispatch" or "add fuel")
     * @param flight
     *            the flight the decision is for
     * @return a FlightOutcome with the results
     * @throws IllegalArgumentException
     *             if bad input is given
     */
    public static FlightOutcome calculateOutcome(String decision, Flight flight)
    {
        return calculateOutcome(decision, flight, new java.util.Random());
    }


    /**
     * Calculates what happens based on the player's choice for a flight, using
     * a specific random number generator for events like weather delays.
     * 
     * @param decision
     *            the player's choice ("dispatch", "add fuel", "delay",
     *            "cancel")
     * @param flight
     *            the flight the decision is for
     * @param random
     *            the random source for checking delays
     * @return a FlightOutcome with the results of the choice
     * @throws IllegalArgumentException
     *             if anything is null, the flight isn't scheduled, dispatch
     *             problems aren't fixed, fuel is too expensive, or the decision
     *             isn't recognized
     */
    public static FlightOutcome calculateOutcome(
        String decision,
        Flight flight,
        java.util.Random random)
    {
        if (random == null)
        {
            throw new IllegalArgumentException("A random source is required.");
        }
        if (decision == null || flight == null)
        {
            throw new IllegalArgumentException(
                "Decision and flight are required.");
        }

        if (!"Scheduled".equals(flight.getStatus()))
        {
            throw new IllegalArgumentException(
                "This flight has already been resolved.");
        }
        String action = decision.trim().toLowerCase(java.util.Locale.ROOT);

        switch (action)
        {
            case "dispatch":
                if (!flight.canDispatch())
                {
                    throw new IllegalArgumentException(
                        "Resolve all dispatch problems first.");
                }
                int revenue = getDispatchNet(flight);
                int lateMinutes =
                    flight.hasWeatherProblem() && random.nextBoolean()
                        ? 15 + random.nextInt(76)
                        : 0;
                FlightOutcome arrival =
                    new FlightOutcome(
                        lateMinutes == 0
                            ? revenue
                            : revenue
                                - getLateCompensation(flight, lateMinutes),
                        lateMinutes == 0 ? 10 : -(5 + lateMinutes / 10),
                        true,
                        "Flight " + flight.getFlightNumber()
                            + (lateMinutes == 0
                                ? " arrived on time."
                                : " arrived " + lateMinutes
                                    + " minutes late due to weather."));
                arrival.arrivalDelayMinutes = lateMinutes;
                return arrival;

            case "add fuel":
                Aircraft ac = flight.getAircraft();
                double needed = flight.getFuelNeeded() - ac.getFuelAmount();
                if (needed > 0)
                {
                    double quotedCost =
                        Math.ceil(needed * flight.getFuelPrice());
                    if (!Double.isFinite(quotedCost)
                        || quotedCost > Integer.MAX_VALUE)
                    {
                        throw new IllegalArgumentException(
                            "Fuel purchase exceeds the supported cash range.");
                    }
                    int cost = (int)quotedCost;
                    return new FlightOutcome(
                        -cost,
                        0,
                        false,
                        "Refueled aircraft for $" + cost + ".");
                }
                return new FlightOutcome(
                    0,
                    0,
                    false,
                    "Aircraft already has sufficient fuel.");

            case "delay":
                return new FlightOutcome(
                    0,
                    -4,
                    true,
                    "Flight " + flight.getFlightNumber()
                        + " was delayed. No cash change; reputation -4.");

            case "cancel":
                return new FlightOutcome(
                    0,
                    -12,
                    true,
                    "Flight " + flight.getFlightNumber()
                        + " was cancelled. No revenue or operating cost; reputation -12.");

            default:
                throw new IllegalArgumentException(
                    "Unknown decision: " + decision);
        }
    }
}
