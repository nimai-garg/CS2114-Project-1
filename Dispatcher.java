public class Dispatcher
{
    //~ Fields ................................................................
    /** current cash balance of the game's airline. */
    private int cash;

    /** current reputation score of the game's airline. */
    private int reputation;

    /** number of rounds successfully completed in the game. */
    private int roundsCompleted;
    // ----------------------------------------------------------
    /**
     * Create a new Dispatcher object.
     * @param cash
     * @param reputation
     * @param roundsCompleted
     */
    //~ Constructors ..........................................................
    public Dispatcher(int cash, int reputation, int roundsCompleted) {
        if (cash < 0 || reputation < 0 || roundsCompleted < 0) {
            throw new IllegalArgumentException("Starting values cannot be negative.");
        }
        this.cash = cash;
        this.reputation = reputation;
        this.roundsCompleted = roundsCompleted;
    }

    //~Public  Methods ........................................................

    /** Returns the airline's current cash balance. */
    public int getCash()
    {
        return cash;
    }

    /** Returns the airline's current reputation score. */
    public int getReputation()
    {
        return reputation;
    }

    /** Returns the number of resolved flight rounds. */
    public int getRoundsCompleted()
    {
        return roundsCompleted;
    }

    /** Returns whether the airline can pay an action's cost. */
    public boolean canAfford(int amount)
    {
        return amount >= 0 && cash >= amount;
    }

    /** Applies an action's financial, reputation, and round effects. */
    public void applyOutcome(FlightOutcome outcome)
    {
        if (outcome == null)
        {
            return;
        }
        cash += outcome.getCashChange();
        reputation = Math.max(0, reputation + outcome.getReputationChange());
        if (outcome.doesRoundEnd())
        {
            roundsCompleted++;
        }
    }
}
