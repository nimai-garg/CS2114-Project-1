import java.util.ArrayList;

/** This is listing the actions available for the current flight. */
public class Options
{
    private ArrayList<String> options;

    /** This is creating the standard menu. */
    public Options()
    {
        options = new ArrayList<String>();
        options.add("Dispatch");
        options.add("Add Fuel");
        options.add("Delay");
        options.add("Cancel");
    }

    /** This is creating a menu from the supplied actions. */
    public Options(ArrayList<String> options)
    {
        this.options = new ArrayList<String>(options);
    }

    /** This is returning  theactions that can be used with the current dispatch problems. */
    public ArrayList<String> getAvailableOptions(ArrayList<String> dispatchProblems)
    {
        ArrayList<String> available = new ArrayList<String>();
        for (String option : options)
        {
            if ("Dispatch".equalsIgnoreCase(option) && !dispatchProblems.isEmpty())
            {
                continue;
            }
            if ("Add Fuel".equalsIgnoreCase(option)
                && !dispatchProblems.contains("Fuel Problem"))
            {
                continue;
            }
            available.add(option);
        }
        return available;
    }

    /** This is printing the actions numbered from one. */
    public void displayOptions(ArrayList<String> available)
    {
        for (int i = 0; i < available.size(); i++)
        {
            System.out.println((i + 1) + ". " + available.get(i));
        }
    }

    /** This is accepting either the displayed number or the action name. */
    public boolean isValidChoice(String input, ArrayList<String> available)
    {
        return getChoice(input, available) != null;
    }

    /** This is returning the matching action, or null for invalid input. */
    public String getChoice(String input, ArrayList<String> available)
    {
        if (input == null)
        {
            return null;
        }
        String choice = input.trim();
        for (int i = 0; i < available.size(); i++)
        {
            if (available.get(i).equalsIgnoreCase(choice)
                || Integer.toString(i + 1).equals(choice))
            {
                return available.get(i);
            }
        }
        return null;
    }
}
