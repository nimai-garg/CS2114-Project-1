import java.util.ArrayList;

public class Options
{
    private ArrayList<String> options

    public Options(ArrayList<String> options)
    {
        this.options = options;
    }


    /**
     * Gets available options from dispatchProblems and returns them
     */
    public ArrayList<String> getAvailableOptions(ArrayList<String> dispatchProblems))
    {
        // work on 
        return null;
    }


    /**
     * prints out options in a numbered list
     */
    public void displayOptions(ArrayList<String> options)
    {
        for (int i = 0; i < options.size(); i++)
        {
            System.out.println(i + "." + options[i]);
        }
    }


    /**
     * Checks if input matches any of the Array List string options or numbered value
     */
    public boolean isValidChoice(String input, ArrayList<String> options)
    {
        for (int i = 0; i < options.size(); i++)
        {
            if ((String)(options[i]) == input || input == (String)(i))
            {
                return true;
            }
        }
        return false;
    }
}
