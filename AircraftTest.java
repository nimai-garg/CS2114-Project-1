import student.TestCase;

/**
 * Class to test methods in Aircraft class.
 */
public class AircraftTest extends TestCase
{
    //~ Fields ................................................................
    private Aircraft aircraft;
    //~ Constructors ..........................................................
    /**
     * Sets up new Aircraft object for testing.
     */
    public void setUp() {
        aircraft = new Aircraft("Airbus A220", 5000.00, 5700.00, 200);
    }
    //~Public  Methods ........................................................
    /**
     * Tests if .getName() returns correct name.
     */
    public void testGetName() {
        assertEquals("Airbus A220", aircraft.getName());
    }
    
    /**
     * Tests if .getFuelAmount() returns correct fuel amount.
     */
    public void testGetFuelAmount() {
        assertEquals(5000.00, aircraft.getFuelAmount());
    }
    
    /**
     * Tests if .getFuelCapacity() returns correct fuel capacity.
     */
    public void testGetFuelCapacity() {
        assertEquals(5700.00, aircraft.getFuelCapacity());
    }
    
    /**
     * Tests if .getPassengerCapacity() returns correct passenger capacity.
     */
    public void testGetPassengerCapacity() {
        assertEquals(200, aircraft.getPassengerCapacity());
    }
    
    /**
     * Tests if .hasEnoughFuel() returns true when fuelAmount is more
     * than fuelNeeded and false otherwise.
     */
    public void testHasEnoughFuel() {
        assertTrue(aircraft.hasEnoughFuel(4800));
        assertFalse(aircraft.hasEnoughFuel(5200));
    }
    
    /**
     * Tests if .canCarry() returns true when passengerCount is less than
     * passengerCapacity and false otherwise.
     */
    public void testCanCarry() {
        assertTrue(aircraft.canCarry(150));
        assertFalse(aircraft.canCarry(220));
    }
    
    /**
     * Tests if .addFuel() adds correct fuel depending on whether amount
     * entered by user would cause fuel amount to be above capacity
     */
    public void testAddFuel() {
        aircraft.addFuel(200.00);
        assertEquals(5200.00, aircraft.getFuelAmount());
        
        aircraft.addFuel(900.00);
        assertEquals(5700.00, aircraft.getFuelAmount());
        
        aircraft.addFuel(-100.00);
        assertEquals(5000.00, aircraft.getFuelAmount());
    }
}
