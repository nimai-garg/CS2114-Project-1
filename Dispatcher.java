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

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * @return
     */
    //~Public  Methods ........................................................
    
    /** Returns the airline's current cash balance. */
    public int getCash() {
        return this.cash;
    }

    /**
     * Returns the airline's current reputation score.
     *
     * @return the current reputation
     */
    /** Returns the airline's current reputation score. */
    public int getReputation() {
        return this.reputation;
    }

    /**
     * Returns the number of completed flight rounds.
     *
     * @return the count of completed rounds
     */
    /** Returns the number of resolved flight rounds. */
    public int getRoundsCompleted() {
        return this.roundsCompleted;
    }

    /**
     * Determines whether the airline can afford a specified cost.
     *
     * @param amount the cost to be verified
     * @return true if amount is non-negative and affordable; false otherwise
     */
    /** Returns whether the airline can pay an action's cost. */
    public boolean canAfford(int amount) {
        if (amount < 0) {
            return false;
        }
        return this.cash >= amount;
    }

    /**
     * Applies the cash and reputation changes from a decision outcome.
     * Advances the round count if the outcome ends the round.
     *
     * @param outcome the FlightOutcome to process
     */
    /** Applies an action's financial, reputation, and round effects. */
    public void applyOutcome(FlightOutcome outcome) {
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome is required.");
        }
        long nextCash = (long)cash + outcome.getCashChange();
        long nextReputation = (long)reputation + outcome.getReputationChange();
        if (nextCash < 0 || nextCash > Integer.MAX_VALUE
            || nextReputation > Integer.MAX_VALUE
            || (outcome.doesRoundEnd() && roundsCompleted == Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("Outcome exceeds the airline's limits.");
        }
        this.cash = (int)nextCash;
        this.reputation = (int)Math.max(0, nextReputation);
        if (outcome.doesRoundEnd()) {
            this.roundsCompleted++;
        }
    }

}
