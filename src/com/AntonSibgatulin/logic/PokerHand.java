package com.AntonSibgatulin.logic;

import com.AntonSibgatulin.exceptions.InvalidHandException;
import com.AntonSibgatulin.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * PokerHand is a class that stores a player's hand and determines the best 
 * hand that can be made with the player's given cards.
 *
 * @author Zach
 */
public class PokerHand {
    private static final int MAX_HAND_SIZE = 7;

    public User user = null;
    private List<Card> hand;
    private Card[] pocketCards; //Cards dealt to this player only
    
    private String bestHand; //Name of the best hand that can be made with given cards
    private int bestHandValue; //ranked from 1-9 with 9 being the best
    
    private boolean folded;
    private boolean winner;
    
    /**
     * Constructor that initializes the pocket cards, best hand, best hand value,
     * folded indication and winning indication of the given hand.
     */
    public PokerHand ()
    {
        hand = new ArrayList<Card>();
        pocketCards = new Card[2];

        bestHand = "";
        bestHandValue = 0;

        folded = false;
        winner = false;
    }

    public PokerHand (User user)
    {
        this.user=user;
        hand = new ArrayList<Card>();
        pocketCards = new Card[2];

        bestHand = "";
        bestHandValue = 0;

        folded = false;
        winner = false;
    }
    
    /**
     * Adds a new Card to the Poker Hand. Throws an InvalidHandException in the 
     * instance that the new card added exceeds the maximum number of cards allowed
     * by a standard Texas Hold 'Em hand.
     * 
     * @param nextCard 
     * @throws InvalidHandException
     */
    public void addCard(Card nextCard)
    {
        if(hand.size() >= MAX_HAND_SIZE)
            throw new InvalidHandException("The poker hand cannot exceed 7 cards");
        
        if(hand.isEmpty())
        {
            hand.add(nextCard);
            pocketCards[0] = nextCard;
        }
        else
        {
            if(hand.size() < 2)
                pocketCards[1] = nextCard;
            
            int indexToAddAt = 0;
        
            while(indexToAddAt < hand.size() && nextCard.getRankValue() < hand.get(indexToAddAt).getRankValue())
            {
                indexToAddAt++;
            }
            
            if(indexToAddAt == hand.size())
                hand.add(nextCard);
            else
                hand.add(indexToAddAt, nextCard);
        }

    }
    
    /**
     * Sets the folded condition of the Poker Hand
     * 
     * @param folded 
     */
    public void fold(boolean folded)
    {
        this.folded = folded;
    }
    
    /**
     * Returns the folded condition of the Poker Hand.
     * 
     * @return folded
     */
    public boolean hasFolded()
    {
        return folded;
    }
    
    /**
     * Returns the Poker Hand 
     * 
     * @return hand
     */
    public List<Card> getHand()
    {
        return hand;
    }
    
    /**
     * Initializes the best hand possible given the cards contained by the hand.
     */
    public void setBestHand()
    {
        bestHand = bestHand();
    }
    
    /**
     * Returns the string representation of the best hand that can be made with
     * the given cards provided in the hand.
     * 
     * @return bestHand
     */
    public String getBestHand()
    {
        return bestHand;
    }
    
    /**
     * Returns the numerical representation of the best hand that can be made 
     * with the given cards.
     * 
     * @return bestHandValue
     */
    public int getBestHandValue()
    {
        return bestHandValue;
    }
    
    /**
     * sets the winning indication of the hand.
     * 
     * @param winner 
     */
    public void setWinner(boolean winner)
    {
        this.winner = winner;
    }
    
    /**
     * Returns the winning indication of the hand.
     * 
     * @return winner
     */
    public boolean isWinner()
    {
        return winner;
    }
    
    /**
     * Returns an array of Cards containing the Cards dealt to the player
     * that can only be used by the player to construct the best hand.
     * 
     * @return pocketCards
     */
    public Card[] getPocketCards()
    {
        return pocketCards;
    }
    
    /**
     * Returns a string representation of the best hand that can be made with the
     * given set of cards, and sets the numerical valuation of the best hand that
     * can be made with the given set of cards.
     * 
     * @return bestHand
     */
    private String bestHand()
    {
        if(hand.size() < 2)
            throw new InvalidHandException("The hand must contain at least two cards");
        
        if(isStraight())
        {
            if(isStraightFlush()) 
            {
                if(isRoyalFlush())
                {
                    bestHandValue = 9;
                    return "Royal Flush!!!";
                }
                bestHandValue = 8;
                return "Straight Flush";
            }
            bestHandValue = 4;
            return "Straight";
        }
        if(isFlush())
        {
            bestHandValue = 5;
            return "Flush";
        }
        if(isPair())
        {
            if(isTwoPair())
            {
                if(isFullHouse())
                {
                    if(isFourOfAKind())
                    {
                        bestHandValue = 7;
                        return "Four of a Kind";
                    }
                    
                    bestHandValue = 6;
                    return "Full House";
                }
                
                bestHandValue = 2;
                return "Two Pair";
            }
            
            if(isThreeOfAKind())
            {
                if(isFourOfAKind())
                {
                    bestHandValue = 7;
                    return "Four of a Kind";
                }
                bestHandValue = 3;
                return "Three of a Kind";
            }
            bestHandValue = 1;
            return "Pair";
        }
        bestHandValue = 0;
        return "High Card";
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a straight and rearranges the set of cards to indicate 
     * the straight.
     * 
     * @return isStraight
     */
    private boolean isStraight()
    {        
        if(hand.size() < 5)
            return false;
        
        int currIndex = 0;
        boolean isStraight = false;
        
        //A straight must be 5 cards long and the max size of a Poker Hand is 7 Cards
        while(!isStraight && currIndex < 3)
        {
            isStraight = (hand.get(currIndex).getRankValue() - hand.get(currIndex+1).getRankValue() == 1);
            int i = currIndex;
            
            //Iterate through the next 4 cards to see if they're sequential
            while(isStraight && i < (currIndex+3))
            {
                try {
                   i++;
                   isStraight = (hand.get(i).getRankValue() - hand.get(i+1).getRankValue() == 1);
                } catch (IndexOutOfBoundsException e) {
                    return false;
                }
            }
            currIndex++;
        }
        
        currIndex--;
        
        //move the straight to indexes 0-4
        if(isStraight)
        {
            List<Card> straight = new ArrayList<>();
            
            for(int i = currIndex; i <= currIndex+4; i++)
            {
                hand.get(i).setIsUsed(true);
                straight.add(hand.get(i));
            }
            
            for(int i = 0; i < hand.size(); i++)
            {
                if(!hand.get(i).getIsUsed())
                    straight.add(hand.get(i));
            }
            
            hand = straight;
        }
        
        return isStraight;
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a flush and rearranges the set of cards to indicate 
     * the flush.
     * 
     * @return isFlush
     */
    private boolean isFlush()
    {
        if(hand.size() < 5)
            return false;
        
        int spadeCount = 0, heartCount = 0, clubCount = 0, diamondCount = 0;
        
        //Determine if there are 5 cards of any suit
        for(int i = 0; i < hand.size(); i++)
        {
            switch(hand.get(i).getSuit())
            {
                case 's':
                    spadeCount++;
                    break;
                case 'h':
                    heartCount++;
                    break;
                case 'c':
                    clubCount++;
                    break;
                case 'd':
                    diamondCount++;
                    break;
            }
        }
        boolean isFlush = ((spadeCount >= 5) || (heartCount >= 5) || (clubCount >= 5) || (diamondCount >= 5));
        
        //Determine the suit of the flush
        if(isFlush)
        {
            char flushSuit;
            
            if(spadeCount >= 5)
                flushSuit = 's';
            else if(heartCount >= 5)
                flushSuit = 'h';
            else if(clubCount >= 5)
                flushSuit = 'c';
            else
                flushSuit = 'd';
            
            List<Card> flush = new ArrayList<>();
            
            //Move the flush to indices 0-4 in descending order
            for(int i = 0; i < hand.size(); i++)
            {
                if(hand.get(i).getSuit() == flushSuit)
                {
                    hand.get(i).setIsUsed(true);
                    flush.add(hand.get(i));
                }
            }
            
            for(int i = 0; i < hand.size(); i++)
            {
                if(!hand.get(i).getIsUsed())
                {
                    flush.add(hand.get(i));
                }
            }
            hand = flush;
        }
        return isFlush;
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a straight flush and rearranges the set of cards to indicate 
     * the straight flush.
     * 
     * @return isStraightFlush
     */
    private boolean isStraightFlush()
    {
        int currCard = 1;
        int lastCardOfStraight = 4;
        boolean isStraightFlush = hand.get(currCard).getSuit() == hand.get(currCard-1).getSuit();
        
        currCard++;
        
        //Check to see if the hand is a straight flush given it's current order
        while(isStraightFlush && currCard <= lastCardOfStraight)
        {
            isStraightFlush = hand.get(currCard).getSuit() == hand.get(currCard-1).getSuit();
            currCard++;            
        }
        currCard--;
        
        if(isStraightFlush)
            return isStraightFlush;
        else
        {
            if(lastCardOfStraight == hand.size()-1)
                return isStraightFlush;
            
            //Check to see if one of the Cards in the straight is paired with the card that 
            //would complete the straight flush and then swap them if so.
            int unusedCards = lastCardOfStraight+1;
            while(!isStraightFlush && unusedCards < hand.size())
            {
                if(hand.get(unusedCards).getRank() == hand.get(currCard).getRank())
                {
                    isStraightFlush = hand.get(unusedCards).getRank() == hand.get(currCard).getRank();
                }
                unusedCards++;         
            }
            unusedCards--;
            
            if(!isStraightFlush)
                return isStraightFlush;
            else
            {
                //recursively determine if the hand is a straight flush
                hand.get(currCard).setIsUsed(false);
                hand.get(unusedCards).setIsUsed(true);
                switchSets(currCard, 1, unusedCards, 1);
                
                return(isStraightFlush());
            }
        }
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a royal flush and rearranges the set of cards to indicate 
     * the flush.
     * 
     * @return isRoyalFlush
     */
    private boolean isRoyalFlush()
    {
        return hand.get(0).getRank() == 'A';
    }
    
    /**
     * @return Result of whether or not the PokerHand contains a pair
     */
    private boolean isPair()
    {
        int startingIndex = 0;
        
        int nextPairIndex = indexOfNextPair(startingIndex);
        
        if(nextPairIndex != -1)
            shiftNextPair(nextPairIndex);
        
        return nextPairIndex != -1;
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a three of a kind and rearranges the set of cards to indicate 
     * the flush.
     * 
     * @return isThreeOfAKind
     */
    private boolean isThreeOfAKind()
    {
        if(hand.size() < 3)
            return false;
        
        int pairLastIndex = 0;
        int thirdMatchIndex = indexOfNextMatch(pairLastIndex);
        
        if(thirdMatchIndex != -1)
            shiftNextMatch(pairLastIndex, thirdMatchIndex);
        
        return thirdMatchIndex != -1;
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a four of a kind and rearranges the set of cards to indicate 
     * the flush.
     * 
     * @return isFourOfAKind
     */
    private boolean isFourOfAKind()
    {
        if(hand.size() < 4)
            return false;
        
        int threeOfKindLastIndex = 1;
        int fourthMatchIndex = indexOfNextMatch(threeOfKindLastIndex);
        
        if(fourthMatchIndex != -1)
            shiftNextMatch(threeOfKindLastIndex, fourthMatchIndex);
        
        return fourthMatchIndex != -1;
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a two pair and rearranges the set of cards to indicate 
     * the flush.
     * 
     * @return isTwoPair
     */
    private boolean isTwoPair()
    {
        if(hand.size() < 4) 
            return false;
        
        int startingIndex = 2;
        int secondPairIndex = indexOfNextPair(startingIndex);
        
        if(secondPairIndex != -1)
        {
            if(hand.get(startingIndex-1).getRankValue() == hand.get(secondPairIndex).getRankValue())
                return false;
            
            shiftNextPair(secondPairIndex);
            
            if(hand.get(0).getRankValue() < hand.get(startingIndex).getRankValue())
                switchSets(0, 2, startingIndex, 2);
        }
        return secondPairIndex != -1;
    }
    
    /**
     * Returns an indication that the best hand that can be made with the given
     * set of cards is a flush and rearranges the set of cards to indicate 
     * the flush.
     * 
     * @return isFullHouse
     */
    private boolean isFullHouse()
    {
        if(hand.size() < 5)
            return false;
        
        int pairOneIndex = 0;
        int pairTwoIndex = 2;
        int pairThreeIndex = indexOfNextPair(pairTwoIndex + 2);
        
        int indexOfTripOne = indexOfNextMatch(pairOneIndex);
        int indexOfTripTwo = indexOfNextMatch(pairTwoIndex);
        
        if(indexOfTripOne != -1)
        {
            shiftNextMatch(pairOneIndex, indexOfTripOne);
            
            return indexOfTripOne != -1;
        }
        else if(indexOfTripTwo != -1)
        {
            shiftNextMatch(pairTwoIndex, indexOfTripTwo);
            switchSets(pairOneIndex, 2, pairTwoIndex, 3);
            
            return indexOfTripTwo != -1;
        }
        else if(pairThreeIndex != -1)
        {
            int indexOfTripThree = indexOfNextMatch(pairThreeIndex);
            if(indexOfTripThree != -1)
            {
               switchSets(pairTwoIndex, 2, pairThreeIndex, 3);
               pairThreeIndex = pairTwoIndex; //Since set two and three switched positions
               switchSets(pairOneIndex, 2, pairThreeIndex, 3);
               
               return indexOfTripThree != -1;
            }
        }
        return false;
    }
    
    /**
     * Returns the index that the next pair starts at proceeding the index 
     * provided as a parameter.
     * 
     * @param startingIndex
     * @return First index of the next Pair
     */
    private int indexOfNextPair(int startingIndex)
    {
        if(startingIndex == (hand.size()-1))
            return -1;
        
        boolean foundPair = hand.get(startingIndex).getRankValue() == hand.get(startingIndex+1).getRankValue();
        int currIndex = startingIndex+1;
        
        //Becuase the hand is sorted in descending order, it is safe to assume pairs will be together
        while(!foundPair && currIndex < hand.size()-1)
        {
            foundPair = hand.get(currIndex).getRankValue() == hand.get(currIndex+1).getRankValue();
            currIndex++;
        }
        
        if(foundPair)
            return (currIndex-1);
        else
            return -1;
    }
    
    /**
     * Moves the pair beginning at the given index, provided by the parameter, 
     * to the next available position in the Poker Hand.
     * 
     * @param indexToShiftFrom 
     */
    private void shiftNextPair(int indexToShiftFrom)
    {
        int currIndex = 0;
        //find the next available position to shift the next pair to
        //Any pairs prior to this pair will be of higher rank because the cards or ordered descending
        while(hand.get(currIndex).getIsUsed())
            currIndex++;
        
        List<Card> tempHand = new ArrayList<>();
        
        //Add all previous sets to retain order
        for(int i = 0; i < currIndex; i++)
            tempHand.add(hand.get(i));
        
        hand.get(indexToShiftFrom).setIsUsed(true);
        tempHand.add(hand.get(indexToShiftFrom));
        
        hand.get(indexToShiftFrom+1).setIsUsed(true);
        tempHand.add(hand.get(indexToShiftFrom+1));
        
        //Add all the kickers in their descending order
        while(currIndex < hand.size())
        {
            if(!hand.get(currIndex).getIsUsed())
                tempHand.add(hand.get(currIndex));
            
            currIndex++;
        }
        hand = tempHand;
    }
    
    /**
     * Returns the index of the next card whose rank matches the rank of the
     * provided parameter index; otherwise returns -1.
     * 
     * @param lastMatched
     * @return currIndex
     */
    private int indexOfNextMatch(int lastMatched)
    {
        if(lastMatched == hand.size()-2)
            return -1;
        
        lastMatched++;
        int currIndex = lastMatched+1;
        boolean hasNextMatch = hand.get(currIndex).getRankValue() == hand.get(lastMatched).getRankValue();
        
        currIndex++;
        
        while(!hasNextMatch && currIndex < hand.size())
        {
            hasNextMatch = hand.get(currIndex).getRankValue() == hand.get(lastMatched).getRankValue();
            currIndex++;
        }
        currIndex--;
        
        if(hasNextMatch)
            return currIndex;
        else
            return -1;
    }
    
    /**
     * Shifts the card at indexOfNextMatch to one position after the index of 
     * the lastMatched Card.
     * 
     * @param lastMatched
     * @param indexOfNextMatch 
     */
    private void shiftNextMatch(int lastMatched, int indexOfNextMatch)
    {
        lastMatched++;
        List<Card> tempHand = new ArrayList<>();
        for(int i = 0; i <= lastMatched; i++)
            tempHand.add(hand.get(i));
        
        hand.get(indexOfNextMatch).setIsUsed(true);
        tempHand.add(hand.get(indexOfNextMatch));
        lastMatched++;
        
        for(int i = lastMatched; i < hand.size(); i++)
        {
            if(i != indexOfNextMatch)
                tempHand.add(hand.get(i));
        }
        hand = tempHand;
    }
    
    /**
     * Switches the first set of matching Cards with the second set of matching cards.
     * 
     * @param setOneIndex
     * @param setOneLength
     * @param setTwoIndex
     * @param setTwoLength 
     */
    private void switchSets(int setOneIndex, int setOneLength, int setTwoIndex, int setTwoLength)
    {
        List<Card> tempHand = new ArrayList<>();
        int currIndex = 0;
        
        //Add all cards before the first set
        while(currIndex < setOneIndex)
        {
            tempHand.add(hand.get(currIndex));
            currIndex++;
        }
        
        //Add the second set to where the first set is currently positioned
        for(int i = 0; i < setTwoLength; i++)
        {
            tempHand.add(hand.get(setTwoIndex+i));
            currIndex++;
        }
        
        //Add all cards between the first and second set
        while(currIndex < setTwoIndex + (setTwoLength - setOneLength))
        {
            tempHand.add(hand.get(currIndex));
            currIndex++;
        }
        
        //Add set one where set two was originally positioned
        for(int i = 0; i < setOneLength; i++)
        {
            tempHand.add(hand.get(setOneIndex+i));
            currIndex++;
        }
        
        //Add the remaining Cards positioned after the second set
        while(currIndex < hand.size())
        {
            tempHand.add(hand.get(currIndex));
            currIndex++;
        }
        
        hand = tempHand;
    }
    
    @Override 
    public String toString()
    {
        StringBuilder handToString = new StringBuilder();
        
        for(int i = 0; i < hand.size(); i++)
        {
            handToString.append(hand.get(i));
            handToString.append(" ");
        }
       
        handToString.append(bestHand);
        
        if(isWinner())
        {
            handToString.append(" ");
            handToString.append("WINNER!");
        }
           
        return handToString.toString();
    }


    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void setPocketCards(Card[] pocketCards) {
        this.pocketCards = pocketCards;
    }

    public void setBestHand(String bestHand) {
        this.bestHand = bestHand;
    }

    public void setBestHandValue(int bestHandValue) {
        this.bestHandValue = bestHandValue;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }
}