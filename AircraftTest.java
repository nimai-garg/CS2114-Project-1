import student.TestCase;

/**
 * This is the class to test methods in Aircraft class.
 */
public class AircraftTest extends TestCase
{
    //~ Fields ................................................................
    private Aircraft aircraft;

    //~ Constructors ..........................................................

    /**
     * This is setting up a new Aircraft object for testing.
     */
    @Override
    public void setUp() {
        aircraft = new Aircraft("Airbus A220", 5000.00, 5700.00, 200);
    }

    //~Public  Methods ........................................................
    /**
     * This is testing if .getName() returns correct name.
     */
    public void testGetName() {
        assertEquals("Airbus A220", aircraft.getName());
    }
    
    /**
     * This is testing if .getFuelAmount() returns correct fuel amount.
     */
    public void testGetFuelAmount() {
        assertEquals(5000.00, aircraft.getFuelAmount(), 0.001);
    }
    
    /**
     * This is testing if .getFuelCapacity() returns correct fuel capacity.
     */
    public void testGetFuelCapacity() {
        assertEquals(5700.00, aircraft.getFuelCapacity(), 0.001);
    }
    
    /**
     * This is testing if .getPassengerCapacity() returns correct passenger capacity.
     */
    public void testGetPassengerCapacity() {
        assertEquals(200, aircraft.getPassengerCapacity());
    }
    
    /**
     * This is testing if .hasEnoughFuel() returns true when fuelAmount is more
     * than fuelNeeded and false otherwise.
     */
    public void testHasEnoughFuel() {
        assertTrue(aircraft.hasEnoughFuel(4800));
        assertTrue(aircraft.hasEnoughFuel(5000));
        assertFalse(aircraft.hasEnoughFuel(5200));
    }
    
    /**
     * This is testing if .canCarry() returns true when passengerCount is less than
     * passengerCapacity and false otherwise.
     */
    public void testCanCarry() {
        assertTrue(aircraft.canCarry(150));
        assertTrue(aircraft.canCarry(200));
        assertFalse(aircraft.canCarry(220));
    }
    
    /**
     * This is testing if .addFuel() adds correct fuel depending on whether amount
     * entered by user would cause fuel amount to be above capacity
     */
    public void testAddFuel() {
        aircraft.addFuel(200.00);
        assertEquals(5200.00, aircraft.getFuelAmount(), 0.001);
        
        aircraft.addFuel(900.00);
        assertEquals(5700.00, aircraft.getFuelAmount(), 0.001);
        
        aircraft.addFuel(-100.00);
        assertEquals(5700.00, aircraft.getFuelAmount(), 0.001);
    }

    /** This is testing that nonpositive fuel amounts leave a partially full tank alone. */
    public void testAddNonpositiveFuel() {
        aircraft.addFuel(-100.00);
        assertEquals(5000.00, aircraft.getFuelAmount(), 0.001);
        aircraft.addFuel(0.00);
        assertEquals(5000.00, aircraft.getFuelAmount(), 0.001);
    }

    /** This is testing filling the tank exactly and adding fuel to a full tank. */
    public void testAddFuelToCapacity() {
        aircraft.addFuel(700.00);
        assertEquals(5700.00, aircraft.getFuelAmount(), 0.001);
        aircraft.addFuel(100.00);
        assertEquals(5700.00, aircraft.getFuelAmount(), 0.001);
    }
}
