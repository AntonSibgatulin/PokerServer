package com.AntonSibgatulin.logic;

import com.AntonSibgatulin.exceptions.InvalidDealException;
import com.AntonSibgatulin.table.TableModel;
import com.AntonSibgatulin.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The PokerTable class is the non-test version of the program that
 * deals randomly shuffled cards in one game of Texas Hold Em.
 *
 * @author Zach
 */
public class PokerTable {

    public TableModel tableModel = null;
    public List<PokerHand> players = null;

    private CardDeck deck;

    private Card[] flop;
    private Card turn;
    private Card river;

    private boolean canFlop;
    private boolean canTurn;
    private boolean canRiver;

    private List<PokerHand> winners;

    /**
     * Constructor that initializes each global variable to their default values.
     */
    public PokerTable() {
       init();

    }

    public PokerTable(TableModel tableModel){
        this.tableModel = tableModel;
        init();
    }

    public void init(){
        players = new ArrayList<>();
        winners = new ArrayList<>();

        flop = new Card[3];
        deck = new CardDeck();

        canFlop = false;
        canTurn = false;
        canRiver = false;
    }

    /**
     * Sets the PokerHand fold variable to true to indicate the hand is no
     * longer in consideration for winning the hand.
     *
     * @param playerIndex
     */
    public void foldPlayer(int playerIndex) {
        players.get(playerIndex).fold(true);
    }

    /**
     * @return The number of folded PokerHands at the table
     */
    public int getNumFolds() {
        int numFolds = 0;
        for (PokerHand currPlayer : players) {
            if (currPlayer.hasFolded())
                numFolds++;
        }
        return numFolds;
    }

    public PokerHand getDoesntFold(){
        for (PokerHand currPlayer : players) {
            if (!currPlayer.hasFolded())
                return currPlayer;
        }
        return null;
    }
    /**
     * Distributes two cards to each player at the table, as determined by the value passed in
     * as a parameter.
     *
     * @param numPlayers
     */
    public void shuffleUpAndDeal(int numPlayers) {
        deck.shuffleDeck();
        for (int i = 0; i < numPlayers; i++)
            players.add(new PokerHand());

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < numPlayers; j++) {
                Card card = deck.dealCard();
                players.get(j).addCard(card);
               // System.out.println(card);
            }
        canFlop = true;
    }

    public void shuffleUpAndDeal(List<User> play) {
        deck.shuffleDeck();

        for (User user : play) {
           // if (user.player == null || user.player.active == false)
            //    continue;
            players.add(user.player.pokerHand);
        }

        for (int j = 0; j < play.size(); j++) {
            String str = "";

            for (int i = 0; i < 2; i++) {

                Card card = deck.dealCard();
                players.get(j).addCard(card);
                str+=(card.toString()+";");
                   }
            players.get(j).user.send("game;me_card;"+str);

        }

        canFlop = true;
    }

    /**
     * Deals the top card to neither the community nor a player.
     */
    private void burnCard() {
        deck.dealCard();
    }

    /**
     * Deals three cards to the community that can be used by every player that
     * has not folded.
     *
     * @throws InvalidDealException if each player has not been dealt two cards or if there is
     *                              one player left in the hand.
     */
    public void dealFlop() {
        /*if (!canFlop)
            throw new InvalidDealException("You can not flop before dealing all players two cards");
        if (getNumFolds() == (players.size() - 1))
            throw new InvalidDealException("The hand is over. All but one player have folded.");
        */
        burnCard();
        flop[0] = deck.dealCard();
        flop[1] = deck.dealCard();
        flop[2] = deck.dealCard();

        for (int i = 0; i < players.size(); i++) {
            PokerHand pokerHand = players.get(i);

            for (Card currCard : flop) {
                if (!pokerHand.hasFolded()) {
                    pokerHand.addCard(copyCard(currCard));
                }
            }

            if(pokerHand.user!=null)
            pokerHand.user.send("game;main_card;"+flop[0].toString()+";"
                    +flop[1].toString()+";"
                    +flop[2].toString());
        }
        canTurn = true;
    }

    /**
     * Deals one card to the community that can be used by each player.
     *
     * @throws InvalidDealException if the flop has not been dealt or if there is one
     *                              player left in the hand.
     */
    public void dealTurn() {
        /*if (!canTurn)
            throw new InvalidDealException("You can not turn before the flop");
        if (getNumFolds() == (players.size() - 1))
            throw new InvalidDealException("The hand is over. All but one player have folded.");
        */
        burnCard();
        turn = deck.dealCard();

        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).hasFolded())
                players.get(i).addCard(copyCard(turn));
            if(players.get(i).user!=null)
            players.get(i).user.send("game;turn_card;"+turn.toString());
        }
        canRiver = true;
    }

    /**
     * Deals one card to the community that can be used by each player.
     *
     * @throws InvalidDealException if the turn has not been dealt or if there is
     *                              one player left in the hand.
     */
    public void dealRiver() {
        /*if (!canRiver)
            throw new InvalidDealException("You can not river before the turn");
        if (getNumFolds() == (players.size() - 1))
            throw new InvalidDealException("The hand is over. All but one player have folded.");
        */
        burnCard();
        river = deck.dealCard();

        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).hasFolded())
                players.get(i).addCard(copyCard(river));

            if(players.get(i).user!=null)
            players.get(i).user.send("game;river_card;"+river.toString());
        }
    }

    /**
     * Returns a copy of the Card passed in as a parameter.
     *
     * @param original
     * @return Copy of the Card passed in as a parameter
     */
    private Card copyCard(Card original) {
        Card copy = new Card(original.getRank(), original.getSuit());
        return copy;
    }

    /**
     * Returns a list of the best PokerHands that can be made and have not folded.
     *
     * @return A list of the best PokerHands
     */
    public List<PokerHand> getWinningHand() {
        setPlayersHands();
        int maxHandValue = bestPossibleHand();
        List<PokerHand> winningHands = findWinners(maxHandValue);

        winners = bestKicker(winningHands, 0);

        for (PokerHand winner : winners) {
            System.out.println("Winner: "+winner);
            winner.setWinner(true);
        }

        return winners;
    }

    /**
     * Sets the best hands that can be made by the given cards for each player that
     * has not yet folded
     */
    private void setPlayersHands() {
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).hasFolded())
                players.get(i).setBestHand();
        }
    }

    /**
     * Returns the numerical representation of the best hand that can be made
     * by any of the PokerHands that have not yet been folded.
     *
     * @return The bestHandValue of any of the PokerHands that are not folded
     */
    private int bestPossibleHand() {
        int maxHandValue = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getBestHandValue() > maxHandValue)
                maxHandValue = players.get(i).getBestHandValue();
        }
        return maxHandValue;
    }

    /**
     * Returns a list of the PokerHands whose bestHandValue is equal to the known
     * bestHandValue that can be made by any of the PokerHands that have not been folded.
     *
     * @param maxHandValue
     * @return A list of the best PokerHands
     */
    private List<PokerHand> findWinners(int maxHandValue) {
        List<PokerHand> winningHands = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).hasFolded()) {
                if (players.get(i).getBestHandValue() == maxHandValue) {
                    winningHands.add(players.get(i));
                }
            }
        }
        return winningHands;
    }

    /**
     * Returns a list of PokerHands that have the highest valid unused Card of
     * any of the PokerHands that have not been folded.
     *
     * @param winners
     * @param kickerStartingIndex
     * @return A list of the PokerHands with the highest-ranked valid unused Card
     */
    private List<PokerHand> bestKicker(List<PokerHand> winners, int kickerStartingIndex) {
        int kickerIndex = kickerStartingIndex;
        int maxUsableCards = 5;

        int[] kickers = new int[winners.size()];
        int i = 0;
        for (PokerHand currWinner : winners) {
            kickers[i] = currWinner.getHand().get(kickerIndex).getRankValue();
            i++;
        }

        List<PokerHand> updateWinners = new ArrayList<>();
        Arrays.sort(kickers);
        int j = 0;
        for (PokerHand currWinner : winners) {
            if (currWinner.getHand().get(kickerIndex).getRankValue() == kickers[kickers.length - 1])
                updateWinners.add(winners.get(j));
            j++;
        }

        kickerIndex++;

        if (updateWinners.size() == 1 || kickerIndex >= maxUsableCards)
            return updateWinners;
        else
            return bestKicker(updateWinners, kickerIndex);
    }

    @Override
    public String toString() {
        StringBuilder tableToString = new StringBuilder();
        for (PokerHand currPlayer : players) {
            for (Card currCard : currPlayer.getPocketCards()) {
                tableToString.append(currCard);
                tableToString.append(" ");
            }

            if (currPlayer.getHand().size() >= 5) {
                tableToString.append("\t");

                for (Card currCard : flop) {
                    tableToString.append(currCard);
                    tableToString.append(" ");
                }
            }

            if (currPlayer.getHand().size() >= 6) {
                tableToString.append(" ");
                tableToString.append(turn);
                tableToString.append(" ");
            }

            if (currPlayer.getHand().size() == 7) {
                tableToString.append(" ");
                tableToString.append(river);
                tableToString.append(" ");
            }

            if (currPlayer.hasFolded()) {
                tableToString.append(" ");
                tableToString.append("Folded");
            }
            if (!currPlayer.hasFolded()) {
                tableToString.append(" ");
                tableToString.append(currPlayer.getBestHand());
            }

            if (currPlayer.isWinner()) {
                tableToString.append("  ");
                if (winners.size() > 1)
                    tableToString.append("SPLIT WINNER!");
                else
                    tableToString.append("WINNER!");
            }

            tableToString.append("\n");
        }
        return tableToString.toString();
    }
}