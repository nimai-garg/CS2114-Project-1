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

}
