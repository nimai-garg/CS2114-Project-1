import java.util.ArrayList;
import student.TestCase;

/** This is testing the actions and their displayed menu numbers. */
public class OptionsTest extends TestCase
{
    public void testClearFlightMenu()
    {
        Options options = new Options();
        ArrayList<String> available = options.getAvailableOptions(
            new ArrayList<String>());
        assertEquals(3, available.size());
        assertEquals("Dispatch", options.getChoice("1", available));
        assertEquals("Delay", options.getChoice("2", available));
        assertEquals("Cancel", options.getChoice("3", available));
        assertTrue(options.isValidChoice(" dispatch ", available));
        assertFalse(options.isValidChoice("0", available));
        assertFalse(options.isValidChoice("4", available));
        assertFalse(options.isValidChoice(null, available));
        assertFalse(options.isValidChoice("Add Fuel", available));
    }

    public void testProblemMenu()
    {
        Options options = new Options();
        ArrayList<String> problems = new ArrayList<String>();
        problems.add("Weather Problem");
        problems.add("Fuel Problem");
        ArrayList<String> available = options.getAvailableOptions(problems);
        assertFalse(available.contains("Dispatch"));
        assertEquals("Add Fuel", options.getChoice("1", available));
        problems.remove("Fuel Problem");
        available = options.getAvailableOptions(problems);
        assertEquals(2, available.size());
        assertEquals("Delay", options.getChoice("1", available));
    }

    public void testCustomMenuIsCopied()
    {
        ArrayList<String> custom = new ArrayList<String>();
        custom.add("Cancel");
        Options options = new Options(custom);
        custom.clear();
        assertEquals("Cancel", options.getAvailableOptions(
            new ArrayList<String>()).get(0));
    }
}
