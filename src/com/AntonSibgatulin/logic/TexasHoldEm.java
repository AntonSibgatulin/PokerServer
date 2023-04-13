package com.AntonSibgatulin.logic;

import com.AntonSibgatulin.exceptions.InvalidConsoleArgException;

import java.util.List;
//import Test.TexasHoldEmTester;


/**
 * The driver class of the Texas Hold Em program.
 * 
 * @author Zach Richardson
 */
public class TexasHoldEm {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InvalidConsoleArgException {

        int playerCount = 3;
for(int i =0;i<1000;i++) {

    PokerTable table = new PokerTable();

    table.shuffleUpAndDeal(playerCount);
    table.dealFlop();

    //table.players.get(0).setFolded(true);

    table.dealTurn();
    table.dealRiver();

    List<PokerHand> list = table.getWinningHand();

    System.out.println(list);
    System.out.println(table);
    System.out.println("\n\n\n");
}

    }
    
    /**
     * Displays the results of the tester class to the console
     */
    private static void testPoker()
    {
       // TexasHoldEmTester tester = new TexasHoldEmTester();
        //tester.runTests();
    }
    
    /**
     * Prints the results of a simulated poker hand to the console
     */
    private static void playPoker(String numPlayers)
    {
        int playerCount = 0;
        try {
            playerCount = Integer.parseInt(numPlayers);
        } catch (NumberFormatException e) {
            System.out.println("Please enter an integer value as the second argument.");
        }
        
        PokerTable table = new PokerTable();
        
        table.shuffleUpAndDeal(playerCount);
        table.dealFlop();
        table.dealTurn();
        table.dealRiver();
            
        table.getWinningHand();
            
        System.out.println(table);
    }
}
