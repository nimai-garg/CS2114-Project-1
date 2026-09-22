public class FlightOutcome
{
    //~ Fields ................................................................
    private int cashChange;
    private int reputationChange;
    private boolean roundEnds;
    private String message;

    //~ Constructors ..........................................................
    /** Creates an outcome with the supplied effects and message. */
    public FlightOutcome(int cashChange, int reputationChange,
        boolean roundEnds, String message)
    {
        this.cashChange = cashChange;
        this.reputationChange = reputationChange;
        this.roundEnds = roundEnds;
        this.message = message;
    }

    //~Public  Methods ........................................................
    public int getCashChange()
    {
        return cashChange;
    }

    public int getReputationChange()
    {
        return reputationChange;
    }

    public boolean doesRoundEnd()
    {
        return roundEnds;
    }

    public String getMessage()
    {
        return message;
    }

    /** Calculates the effect of one valid action for a flight. */
    public FlightOutcome calculateOutcome(String action, Flight flight)
    {
        if (action == null || flight == null)
        {
            return new FlightOutcome(0, 0, false, "No action was taken.");
        }
        if ("Dispatch".equalsIgnoreCase(action))
        {
            return new FlightOutcome(flight.getPassengerCount() * 150, 5,
                true, "Flight dispatched successfully.");
        }
        if ("Add Fuel".equalsIgnoreCase(action))
        {
            double fuelToAdd = Math.max(0, flight.getFuelNeeded()
                - flight.getAircraft().getFuelAmount());
            return new FlightOutcome(-(int)Math.ceil(fuelToAdd * 2), 0,
                false, "Fuel added for the flight.");
        }
        if ("Delay".equalsIgnoreCase(action))
        {
            return new FlightOutcome(-250, -5, true,
                "Flight was delayed.");
        }
        if ("Cancel".equalsIgnoreCase(action))
        {
            return new FlightOutcome(-500, -10, true,
                "Flight was cancelled.");
        }
        return new FlightOutcome(0, 0, false, "No action was taken.");
    }

}
