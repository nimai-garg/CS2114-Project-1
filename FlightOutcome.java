public class FlightOutcome
{
    private int cashChange;
    private int reputationChange;
    private boolean roundEnds;
    private String message;

    public FlightOutcome(
        int cashChange,
        int reputationChange,
        boolean roundEnds,
        String message)
    {
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


    public static FlightOutcome calculateOutcome(String decision, Flight flight)
    {
        if (decision == null || flight == null)
        {
            return new FlightOutcome(
                0,
                0,
                false,
                "Invalid decision or flight data. Please check value of input fields");
        }

        String action = decision.trim().toLowerCase();

        switch (action)
        {
            case "dispatch":
                int revenue = flight.getPassengerCount() * 150;
                return new FlightOutcome(
                    revenue,
                    10,
                    true,
                    "Flight " + flight.getFlightNumber()
                        + " departed successfully!");

            case "add fuel":
                Aircraft ac = flight.getAircraft();
                double needed = flight.getFuelNeeded() - ac.getFuelAmount();
                if (needed > 0)
                {
                    int cost = (int)Math.ceil(needed * 1.50);
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
                int delayPenalty = flight.getPassengerCount() / 5;
                return new FlightOutcome(
                    0,
                    -delayPenalty,
                    true,
                    "Flight " + flight.getFlightNumber()
                        + " delayed (-" + delayPenalty + " Reputation).");

            case "cancel":
                int refund = flight.getPassengerCount() * 150;
                int cancelPenalty = flight.getPassengerCount() / 3;
                return new FlightOutcome(
                    -refund,
                    -cancelPenalty,
                    true,
                    "Flight " + flight.getFlightNumber()
                        + " cancelled (-$" + refund + ", -" + cancelPenalty
                        + " Reputation).");

            default:
                return new FlightOutcome(
                    0,
                    0,
                    false,
                    "Unknown decision: " + decision);
        }
    }
}