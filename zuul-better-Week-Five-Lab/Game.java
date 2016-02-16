import java.util.HashMap;
import java.util.ArrayList;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2011.08.08
 */

public class Game 
{
    private Parser parser;
    private SimpleScanner scanner;
    private String currentRoomName;
    private HashMap<String, Room> roomList;
    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        parser = new Parser();
        scanner = new SimpleScanner();
        roomList = new HashMap<String, Room>();
        createRooms();
    }
    
    /**
     * Create a room, given a name and description.
     * @param name  The name of the room we want to create.
     * @param description  A string giving the description of the room.
     */
    private void createRoom(String name, String description)
    {
        roomList.put(name, new Room(description));
    }
    
    /**
     * Destroy a room, given the room's name.
     * @param name  The name of the room we want to destroy.
     */
    private void removeRoom(String name)
    {
        roomList.remove(name);
    }
    
    /**
     * Set an exit for a room, given the name of that room, a direction for the exit, and the room the exit leads to.
     * This avoids having to explicitly get the room object from the roomList every time that an exit is set.
     * @param roomName  The name of the room that an exit is being set for.
     * @param direction  The direction that the exit leads out of the room.
     * @param destination  The room that the exit leads to.
     */
    private void setRoomExit(String roomName, String direction, String destination)
    {
        roomList.get(roomName).setExit(direction, destination);
    }
    
    private void legitimateExit(String roomName, String direction)
    {
        
    }
    
    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // create the rooms
        createRoom("outside", "outside the main entrance of the university");
        createRoom("theater", "in a lecture theater");
        createRoom("pub", "in the campus pub");
        createRoom("lab", "in a computing lab");
        createRoom("office", "in the computing admin office");
        createRoom("overlook", "on top of the lecture building, admiring the beautiful view of campus");
        createRoom("dungeon", "in the 'dungeon,' a legendary work area under the computer lab");
        
        setRoomExit("outside", "east", "theater");
        setRoomExit("outside", "south", "lab");
        setRoomExit("outside", "west", "pub");
        
        setRoomExit("theater", "west", "outside");
        setRoomExit("theater", "up", "overlook");
        
        setRoomExit("pub", "east", "outside");
        
        setRoomExit("lab", "north", "outside");
        setRoomExit("lab", "east", "office");
        setRoomExit("lab", "down", "dungeon");
        
        setRoomExit("office", "west", "lab");
        
        setRoomExit("overlook", "down", "theater");
        
        setRoomExit("dungeon", "up", "lab");
        
        currentRoomName = "outside";
    }
    
    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    
    
    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        Room currentRoom = roomList.get(currentRoomName);
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("look")) {
            look();
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("add")) {
            addRoom(command);
        }
        else if (commandWord.equals("destroy")) {
            destroyRoom(command);
        }
       // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord(); // direction is a string that is the second word, such as "east" or "up"

        // Try to leave current room.
        Room currentRoom = roomList.get(currentRoomName);
        String nextRoomName = currentRoom.getExit(direction);
        Room nextRoom = roomList.get(nextRoomName);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoomName = nextRoomName; // The name of the current room is set to the name of the room we're going into, which sets the current room to the next room.
            System.out.println(nextRoom.getLongDescription());
        }
    }

    /**
     * "Look" was entered. Prints out the description of the room and the exits after they have been originally printed.
     */
    private void look()
    {
        Room currentRoom = roomList.get(currentRoomName);
        System.out.println(currentRoom.getLongDescription());
    }
    
    /**
     * "Add" was entered. Creates a room object that's connected to the current room in a user-specified direction.
     */
    private void addRoom(Command command)
    {
        if(!command.hasSecondWord()) { // Perform check that there's a room name associated with the command.
            System.out.println("Add what?");
            return;
        }
        else {
            String newRoomName = command.getSecondWord();
            String directionInto = scanner.getInput("Which direction is the exit from " + currentRoomName + " to " + newRoomName + "? (For example, 'east')");
            if (directionInto.equals("")) {
                System.out.println("Error: Empty direction! Please try again.");
                return;
            }
            String directionOut = scanner.getInput("Which direction is the exit from " + newRoomName + " to " + currentRoomName + "?\n(Usually the opposite of the " + 
                                                   "direction you just entered. For example,\nif you just entered 'east' to get into the new room, you could enter" +
                                                   "\n'west' to get back from the new room into the room you're currently in.)");
            if (directionOut.equals("")) {
                System.out.println("Error: Empty direction! Please try again.");
                return;
            }
            String description = scanner.getInput("What is the description of " + newRoomName + "? (For example, 'in an empty room')");
            if (description.equals("")) {
                System.out.println("Error: Empty description! Please try again.");
                return;
            }
            createRoom(newRoomName, description); // If the creation of the room was successful, then set the exits.
            setRoomExit(currentRoomName, directionInto, newRoomName);
            setRoomExit(newRoomName, directionOut, currentRoomName);
            look();
        }
    }
    
     /**
     * "Destroy" was entered. Destroys the room specified, provided that the room isn't the current room.
     */
    private void destroyRoom(Command command)
    {
       if(!command.hasSecondWord()) { // Perform check that there's a room name associated with the command.
            System.out.println("Destroy what?");
            return;
       }
       String roomToRemove = command.getSecondWord();
       if(roomToRemove.equals(currentRoomName)) { // Can't destroy the room we're in.
           System.out.println("Can't destroy the current room without dying!");
       }
       else {
           removeRoom(roomToRemove);
           System.out.println(roomToRemove + " has been destroyed!");
           look();
       }
           
    }
    
    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
