import java.util.Scanner;
/**
 * A simple scanner module to get string input from the user.
 * 
 * @author Isaac Blasiman 
 * @version 2/16/16
 */
public class SimpleScanner
{
    private Scanner terminalReader;
    
    /**
     * Create a scanner to read input.
     */
    public SimpleScanner(){
        terminalReader = new Scanner(System.in);
    }
    
    /**
     * Get input from the user by printing the question given as an argument, and return that input.
     * @param question  A string (usually a sentence) that will be printed to ask the user to enter
     *                  input
     */
    public String getInput(String question)
    {
        System.out.println(question);
        System.out.print("> ");
        String inputLine = terminalReader.nextLine();
        Scanner stringReader = new Scanner(inputLine);
        String returnString = "";
        while(stringReader.hasNext()) {
            returnString += stringReader.next() + " ";
        }
        return returnString.trim();
        
    }
}
