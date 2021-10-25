package Monopoly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    private InputParser parser;
    private CommandWord commandWord;
    private ArrayList<Integer> dice;
    private ArrayList<Player> players;
    private Player currentPlayer;
    ArrayList<Square> squares = new ArrayList<Square>(40);
    int currentTurn = 0;
	int totalPlayers;
    public HashMap<Player, ArrayList<Property>> ownedProperties;

    /**
     * Create the game and initialise its internal map.
     */
    public Game()
    {
        parser = new InputParser();
        dice = new ArrayList<>();
        players = new ArrayList<>();
        ownedProperties = new HashMap<>();
    }

    /**
     * Creates the Monopoly board
     */
    private void createBoard(){
        
    }

    public void play(){

        printWelcome();

        System.out.println("How many people are playing today?");
        Scanner sc = new Scanner(System.in);
        totalPlayers = sc.nextInt();
        for(int i = 1; i <= totalPlayers; i++){
            Scanner username = new Scanner(System.in);
            String playername= username.nextLine();
			players.add(new Player(playername));
        }

        boolean finished = false;
        while (!finished) {
            Command command = InputParser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing Monopoly!");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to Monopoly!");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        //System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command)
    {
        boolean wantToQuit = false;

        CommandWord commandWord = Command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("Unknown command.");
                break;

            case HELP:
                printHelp();
                break;

            case BUY_PROPERTY:
                System.out.println("Are you sure you want to buy this property? Y/N");
                Scanner buyScn = new Scanner(System.in);
                String buyAns = buyScn.nextLine();
                if (buyAns.equals("Y")) {
                    //currentPlayer.buyProperty(currentPlayer.currentPosition);
                    break;
                } else if (buyAns.equals("N")) {
                    break;
                } else {
                    System.out.println("Unknown answer, please try the command again.");
                    break;
                }

            case PASS_TURN:
                passTurn();
                break;

            case QUIT:
                System.out.println("Are you sure you want to quit? The game will end and your progress will be lost. Y/N");
                Scanner quitScn = new Scanner(System.in);
                String quitAns = quitScn.nextLine();
                if (quitAns.equals("Y")) {
                    //currentPlayer.buyProperty(currentPlayer.currentPosition);
                    break;
                } else if (quitAns.equals("N")) {
                    break;
                } else {
                    System.out.println("Unknown answer, please try the command again.");
                    break;
                }
        }
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
        System.out.println("You are lost");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    private int rollDice(){
        return ThreadLocalRandom.current().nextInt(1, 7);
    }

    private void passTurn(){

    }


}
