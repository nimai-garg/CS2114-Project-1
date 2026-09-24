public class FlightOutcome
{
    private int cashChange;
    private int reputationChange;
    private boolean roundEnds;
    private String message;
    private int arrivalDelayMinutes;

    public FlightOutcome(
        int cashChange,
        int reputationChange,
        boolean roundEnds,
        String message)
    {
        if (message == null) {
            throw new IllegalArgumentException("Feedback is required.");
        }
        this.cashChange = cashChange;
        this.reputationChange = reputationChange;
        this.roundEnds = roundEnds;
        this.message = message;
    }


    public int getCashChange()
    {
        return this.cashChange;
    }


    public int getReputationChange()
    {
        return this.reputationChange;
    }


    public boolean doesRoundEnd()
    {
        return this.roundEnds;
    }


    public String getMessage()
    {
        return this.message;
    }


    public static int getDispatchNet(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight is required.");
        }
        return Math.subtractExact(Math.multiplyExact(flight.getPassengerCount(), 150),
            Math.addExact(1800, Math.multiplyExact(flight.getFlightTime(), 25)));
    }

    public static int getLateCompensation(Flight flight, int minutes) {
        if (flight == null || minutes < 0) {
            throw new IllegalArgumentException("A flight and nonnegative delay are required.");
        }
        if (minutes == 0) {
            return 0;
        }
        return Math.addExact(Math.multiplyExact(flight.getPassengerCount(), 50),
            Math.multiplyExact(minutes, 15));
    }

    public int getArrivalDelayMinutes() {
        return arrivalDelayMinutes;
    }

    public static FlightOutcome calculateOutcome(String decision, Flight flight)
    {
        return calculateOutcome(decision, flight, new java.util.Random());
    }

    public static FlightOutcome calculateOutcome(String decision, Flight flight,
        java.util.Random random)
    {
        if (random == null) {
            throw new IllegalArgumentException("A random source is required.");
        }
        if (decision == null || flight == null)
        {
            throw new IllegalArgumentException("Decision and flight are required.");
        }

        if (!"Scheduled".equals(flight.getStatus())) {
            throw new IllegalArgumentException("This flight has already been resolved.");
        }
        String action = decision.trim().toLowerCase(java.util.Locale.ROOT);

        switch (action)
        {
            case "dispatch":
                if (!flight.canDispatch()) {
                    throw new IllegalArgumentException("Resolve all dispatch problems first.");
                }
                int revenue = getDispatchNet(flight);
                int lateMinutes = flight.hasWeatherProblem() && random.nextBoolean()
                    ? 15 + random.nextInt(76) : 0;
                FlightOutcome arrival = new FlightOutcome(
                    lateMinutes == 0 ? revenue
                        : revenue - getLateCompensation(flight, lateMinutes),
                    lateMinutes == 0 ? 10 : -(5 + lateMinutes / 10),
                    true,
                    "Flight " + flight.getFlightNumber()
                        + (lateMinutes == 0 ? " arrived on time."
                            : " arrived " + lateMinutes + " minutes late due to weather."));
                arrival.arrivalDelayMinutes = lateMinutes;
                return arrival;

            case "add fuel":
                Aircraft ac = flight.getAircraft();
                double needed = flight.getFuelNeeded() - ac.getFuelAmount();
                if (needed > 0)
                {
                    double quotedCost = Math.ceil(needed * flight.getFuelPrice());
                    if (!Double.isFinite(quotedCost) || quotedCost > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("Fuel purchase exceeds the supported cash range.");
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
                throw new IllegalArgumentException("Unknown decision: " + decision);
        }
    }
}