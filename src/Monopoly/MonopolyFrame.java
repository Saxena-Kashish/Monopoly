package Monopoly;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class MonopolyFrame extends JFrame implements MonopolyView, Serializable {

    private Game game;
    private static ArrayList<JLabel> propertyPictures;
    private BoardGUI boardGUI;
    JFrame frame = new JFrame("Monopoly");
    JPanel startingInfo;
    JPanel moreInfo;
    JButton rollButton;
    private int count;
    private int turn = 0;
    JLabel info;

    public  MonopolyFrame(Game game) throws IOException {
        super("Monopoly");

        this.game = game;
        propertyPictures = new ArrayList<>();


        handleInitialSetup();

        boardGUI = new BoardGUI(game.getTotalPlayers());


        frame.setLayout(new BorderLayout());

        //JPanel inputPanel = new JPanel();

        // main window
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        boardGUI = new BoardGUI(game.getTotalPlayers());

        //right panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setSize(200, 400);

        //add buttons
        JButton rollButton = createRollButton();
        JButton quitButton = createQuitButton();

        startingInfo = new JPanel();
        info = new JLabel("Current Player: "+ game.getCurrentPlayer().getPlayerName());
        info.setFont(new Font("sans serif", Font.BOLD, 18));
        startingInfo.add(info);
        //JLabel more = new JLabel("This player owns: "+game.getCurrentPlayer().getProperties());
        JLabel more = new JLabel("");
        more.setFont(new Font("sans serif", Font.PLAIN, 16));
        JButton statusButton = new JButton("Player status");
        statusButton.addActionListener(e -> {
            Player currentPlayer = game.getCurrentPlayer();
            more.setText("<html>Player name: " + currentPlayer.getPlayerName() +
                    "<br>Owned Properties: <br>" + currentPlayer.getPropertyNames()+
                    "<br>Money in the bank: " + currentPlayer.getMoney() +
                    "<br>Current Position: " + currentPlayer.getCurrentPosition() + "</html>");
        });

        JButton clearStatusButton = new JButton("Clear status");
        clearStatusButton.addActionListener(e -> {
            more.setText("");
        });

        moreInfo = new JPanel();
        moreInfo.add(more);

        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> {
            saveGame();
        });
        JPanel frameButtonPanel = new JPanel();
        frameButtonPanel.setSize(500, 500);
        frameButtonPanel.add(rollButton);
        frameButtonPanel.add(statusButton);
        frameButtonPanel.add(clearStatusButton);
        frameButtonPanel.add(saveButton);
        frameButtonPanel.add(quitButton);

        rightPanel.add(frameButtonPanel);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(startingInfo, BorderLayout.NORTH);
        infoPanel.add(moreInfo, BorderLayout.CENTER);

        frame.add(boardGUI, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.PAGE_END);
        frame.add(infoPanel,BorderLayout.EAST);
        //frame.add(moreInfo,BorderLayout.EAST);
        frame.setSize(1000, 800);
        frame.setBackground(Color.lightGray);

        frame.setVisible(true);
        frame.setResizable(false);

        game.addView(this);
        frame.invalidate();


    }

    private void handleInitialSetup(){
        String str = "";
        String name = "";
        int numAI = 0;

        Object[] options = {"Load Game", "Start New Game"};
        int n = JOptionPane.showOptionDialog(frame,
                "Would you like to load an existing game or start a new one?",
                "Monopoly", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[1]);

        if (n == 0){
            this.loadGame();
        }

        else {
            while (!(game.getTotalPlayers() >= game.getMinPlayers() && game.getTotalPlayers() <= game.getMaxPlayers())) {

                try {
                    str = JOptionPane.showInputDialog("How many AI players would you like to create?");
                    if (str != null) {
                        numAI = Integer.parseInt(str);

                    }
                    for (int i = 1; i <= numAI; i++) {
                        game.addPlayer(new AIPlayer("COM" + i));
                    }
                } catch (NumberFormatException exception) {
                    game.setTotalPlayers(0);
                }

                try {
                    str = JOptionPane.showInputDialog("Enter Number of Players (2-" + (6 - numAI) + "):");
                    if (str != null) {
                        game.setTotalPlayers(Integer.parseInt(str) + numAI);
                    }
                } catch (NumberFormatException exception) {
                    game.setTotalPlayers(0);
                }
            }

            for (int i = 0; i < (game.getTotalPlayers() - numAI) ; i++) {
                name = "";
                while (name == null || name.equals("")) {
                    name = JOptionPane.showInputDialog("Enter Player " + (i + 1) + "'s name:");
                }
                Player player = new Player(name);
                game.addPlayer(player);
            }
        }
    }

    @Override
    public void handleMonopolyUpdate(MonopolyEvent e) {
        handleInitialSetup();
    }

    private JButton createRollButton() {
        rollButton = new JButton("roll");
        rollButton.addActionListener(e -> {

            rollButton.setEnabled(false);
            Player currentPlayer = game.getCurrentPlayer();
            if (currentPlayer.getJail()) {
                turn++;
                Object[] options = {"Pay $50", "Roll"};
            int input = JOptionPane.showOptionDialog(new JFrame(),"You may pay $50 to get out. " +
                            "Also, you may roll once per turn until you find doubles," +
                            " however after three turns you must pay the $50 fine. \n Turn " + turn + ".", "You are in jail!",
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

            if (input == JOptionPane.YES_OPTION) {
                if (currentPlayer.getMoney() < 50) {
                    JOptionPane.showMessageDialog(new JFrame(), "You do not have enough money to pay the fine.");
                    input = JOptionPane.NO_OPTION;
                } else {
                   currentPlayer.setMoney(currentPlayer.getMoney() - 50);
                    JOptionPane.showMessageDialog(new JFrame(), "You are out of jail!");
                    currentPlayer.setJail(false);
                }
            }
            if (input == JOptionPane.NO_OPTION) {
                int[] dices = game.rollDice();

                if (dices[0] == dices[1]) {
                    JOptionPane.showMessageDialog(new JFrame(), "You have rolled " + dices[0] + " and " + dices[1] + ". You are out of jail!");
                    currentPlayer.setJail(false);
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "You have rolled " + dices[0] + " and " + dices[1] + ".\nYou are not out of jail. Passing your turn.");
                    game.passTurn();
                    JLabel info = new JLabel("Current Player: "+ game.getCurrentPlayer().getPlayerName());
                    JLabel money = new JLabel("This player has "+game.getCurrentPlayer().getMoney()+" dollars!");
                    JLabel more = new JLabel("This player owns: "+game.getCurrentPlayer().getProperties());
                    startingInfo.removeAll();
                    startingInfo.add(info);
                    startingInfo.add(money);
                    startingInfo.add(more,BorderLayout.EAST);
                    startingInfo.revalidate();
                    startingInfo.repaint();
                    SwingUtilities.updateComponentTreeUI(frame);
                }
            }

            } else {
                boolean doubles = false;
                int[] dices = game.rollDice();
                int diceRoll = dices[0] + dices[1];
                if (!(dices[0] == dices[1])) {
                    rollButton.setEnabled(false);
                } else {
                    doubles = true;
                    rollButton.setEnabled(true);
                }


                if (count == 2) {
                    count = 0;
                    JOptionPane.showMessageDialog(new JFrame(), "You have rolled three doubles in a row. You are going to jail for speeding.");
                    currentPlayer.currentPosition = 10;
                    currentPlayer.setJail(true);

                } else {
                    if (doubles) {
                        JOptionPane.showMessageDialog(frame, "You have rolled " + dices[0] + " and " + dices[1] + " (" + diceRoll + "). They are doubles! You may roll again.");
                        count++;
                    } else {
                        JOptionPane.showMessageDialog(frame, "You have rolled " + dices[0] + " and " + dices[1] + " (" + diceRoll + ").");
                        count = 0;
                    }

                    int startingPos = currentPlayer.currentPosition;
                    int endingPos = startingPos + diceRoll;

                    if (endingPos > 39) {
                        int placesBefore39 = 39 - startingPos;
                        currentPlayer.currentPosition = (diceRoll - placesBefore39) - 1;
                        JOptionPane.showMessageDialog(new JFrame(), "You passed on GO! You have received $200");
                        currentPlayer.setMoney(currentPlayer.getMoney() + 200);

                    } else {
                        currentPlayer.currentPosition = startingPos + diceRoll;
                    }

                    if (currentPlayer.currentPosition == 30) {
                        JOptionPane.showMessageDialog(new JFrame(), "You landed on the go to jail square! You are going to jail.");
                        currentPlayer.currentPosition = 10;
                        currentPlayer.setJail(true);

                    } else {
                        try {
                            propertyPopUp();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        //Property currentProperty = game.getPropertyList().get(currentPlayer.currentPosition);
                    }
                }
            }
        });
        return rollButton;
    }

    public JButton createQuitButton() {
        JButton quitButton = new JButton("quit");
        quitButton.addActionListener(e -> {

            int code = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?");
            if (code == JOptionPane.OK_OPTION) {
                frame.dispose();
                System.exit(0);
            }

        });
        return quitButton;
    }

    public void propertyPopUp() throws IOException{
        JFrame popUpFrame = new JFrame("Property");
        popUpFrame.setSize(500, 500);
        JPanel panel = new JPanel();
        JButton buy = new JButton();
        JButton pass = new JButton();
        JButton house = new JButton("Buy House");
        buy.setText("BUY");
        pass.setText("Pass Turn");
        panel.setLayout(new BorderLayout());
        propertyPictures = new ArrayList<JLabel>();
        for(int i = 0; i < 40; i++) {
            BufferedImage property1 = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Monopoly/propertypictures/" + i + ".png")));
            JLabel label1 = new JLabel(new ImageIcon(property1));
            propertyPictures.add(label1);
        }

        ArrayList<Property> propertyList = game.getPropertyList();
        Player currentPlayer = game.getCurrentPlayer();
        Property currentProperty = propertyList.get(currentPlayer.currentPosition);

        JLabel label = new JLabel("Welcome to " + currentProperty.getName());

        panel.add(label,BorderLayout.PAGE_START);
        panel.add(buy,BorderLayout.PAGE_START);


        /*
        if (currentProperty.getColour().equals("none")) {
                System.out.println("You cannot buy this property.");
                break;
            } else if (currentProperty.getPurchasingCost() > currentPlayer.getMoney()) {
                System.out.println("You do not have enough funds to buy this property.");
            } else if (!currentProperty.checkAvailability()) {
                System.out.println("You cannot buy this property, it belongs to " + currentProperty.getOwner().getPlayerName() + ".");
            } else {
                System.out.printf("Are you sure you want to buy %s? Y/N\n", currentProperty.getName());
                Scanner buyScn = new Scanner(System.in);
                String buyAns = buyScn.nextLine();

                if (buyAns.equals("Y")) {


                    break;
                } else if (buyAns.equals("N")) {
                    break;
                } else {
                    System.out.println("Unknown answer, please try the command again.");
                    break;
                }
            }
         */
        buy.addActionListener(e -> {
            if (currentProperty.getColour().equals("none")) {
                JOptionPane.showMessageDialog(new JPanel(), "You cannot buy this property.");
                buy.setEnabled(false);
            } else if (currentProperty.getPurchasingCost() > currentPlayer.getMoney()) {
                JOptionPane.showMessageDialog(new JPanel(), "You do not have enough money to buy this property.");
                buy.setEnabled(false);
            } else if (!currentProperty.checkAvailability()) {
                JOptionPane.showMessageDialog(new JPanel(), "The property is already owned.");
                buy.setEnabled(false);
            } else {
                int code = JOptionPane.showConfirmDialog(frame, "Are you sure you would like to buy this property?");
                if (code == JOptionPane.OK_OPTION) {
                    currentPlayer.buyProperty(currentProperty);
                    currentProperty.setOwner(currentPlayer);
                    int propertyCost = currentProperty.getPurchasingCost();

                    currentPlayer.setMoney(currentPlayer.getMoney() - propertyCost);
                    buy.setEnabled(false);
                }
            }

        });
        panel.add(pass,BorderLayout.AFTER_LAST_LINE);
        pass.addActionListener(e -> {
            rollButton.setEnabled(true);
            game.passTurn();
            popUpFrame.setVisible(false);
            JLabel info = new JLabel("Current Player: "+ game.getCurrentPlayer().getPlayerName());
            info.setFont(new Font("sans serif", Font.BOLD, 18));
            JLabel money = new JLabel("This player has "+game.getCurrentPlayer().getMoney()+" dollars!");
            JLabel more = new JLabel("This player owns: "+game.getCurrentPlayer().getProperties());
            startingInfo.removeAll();
            startingInfo.add(info);
            //startingInfo.add(money);
            //startingInfo.add(more,BorderLayout.EAST);
            startingInfo.revalidate();
            startingInfo.repaint();
            SwingUtilities.updateComponentTreeUI(frame);




        });
        panel.add(house,BorderLayout.EAST);
        house.addActionListener(e -> {
            if(currentProperty.getColour()=="brown"||currentProperty.getColour()=="darkblue"){
                if(currentProperty.getColourCount(game.getCurrentPlayer().getProperties(), currentProperty.getColour()) == 2 && game.getCurrentPlayer().getMoney()> currentProperty.getHouseCost() ){
                    currentProperty.setColourSet();
                    currentProperty.buyHouse(currentProperty.getHouses());
                }
            }
           else if (currentProperty.getColour()=="lightblue"||currentProperty.getColour()=="pink"||currentProperty.getColour()=="orange"||currentProperty.getColour()=="red"||currentProperty.getColour()=="yellow"||currentProperty.getColour()=="green"){
               if(currentProperty.getColourCount(game.getCurrentPlayer().getProperties(), currentProperty.getColour()) == 3 && game.getCurrentPlayer().getMoney()> currentProperty.getHouseCost()){
                    currentProperty.setColourSet();
                   currentProperty.buyHouse(currentProperty.getHouses());
                }

            }
           else{
                JOptionPane.showMessageDialog(new JPanel(), "You do not own the colour set yet / You do not have enough money for this action");
                house.setEnabled(false);
            }
        });
        popUpFrame.add(panel);
        popUpFrame.add(propertyPictures.get(currentPlayer.currentPosition),BorderLayout.PAGE_END);


        popUpFrame.setLocationRelativeTo(null);
        //frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        popUpFrame.pack();
        popUpFrame.setVisible(true);
    }

    /**
     * save current game.
     */
    private void saveGame() {
        //prompt user for save file name
        String saveName = "";
        while (saveName == null || saveName.equals("")) {
            saveName = JOptionPane.showInputDialog("Enter a save name:");
        }

        //serialize game
        this.game.serializeGame(saveName);

        //serialize board
       // this.boardGUI.saveState();//telling board to save its static fields into normal non-static fields
        //this.boardGUI.serializeBoardGUI(saveName);
    }

    /**
     * load a game.
     */
    private void loadGame(){
        String loadName = "";
        while (loadName == null || loadName.equals("")) {
            loadName = JOptionPane.showInputDialog("Enter a load name (same name that was entered while saving):");
        }

        Game loadedGame = Game.deserializeGame(loadName);
       // BoardGUI loadedBoard = BoardGUI.deserializeBoardGUI(loadName);

        //loadedBoard.loadState();//telling board to load its non-static fields that were saved back into the original static fields

        game = loadedGame;
        //boardGUI = loadedBoard;

        //info.setText(game.getCurrentPlayer().getPlayerName() + "'s turn");
    }

    public static void main(String[] args) throws IOException {
        Game game = new Game();
        MonopolyFrame mainFrame = new MonopolyFrame(game);
    }
}