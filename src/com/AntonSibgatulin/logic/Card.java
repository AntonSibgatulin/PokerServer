package com.AntonSibgatulin.logic;

import com.AntonSibgatulin.exceptions.InvalidRankException;
import com.AntonSibgatulin.exceptions.InvalidSuitException;


/**
 * Card is a wrapper class that represents an individual card within a 
 * standard 52 card playing deck.
 * 
 * @author Zach Richardson
 */
public class Card {
    private char suit;
    private String suitDisplay;
    private char rank;
    private String rankDisplay;
    private int rankValue;
    private boolean isDealt;
    private boolean isUsed;
    
    /**
     * Constructor that initializes the playing card to the given
     * rank and suit values.
     * 
     * @param rank
     * @param suit 
     */
    public Card(char rank, char suit)
    {      
        setRankValue(rank);
        setSuit(suit);
        isDealt = false;
        isUsed = false;
    }
    
    /**
     * Accepts boolean value indicative as to whether or not the card has been
     * dealt.
     * @param isDealt 
     */
    public void setIsDealt(boolean isDealt)
    {
        this.isDealt = isDealt;
    }
    
    /**
     * returns a boolean expression as to whether or not the given card has
     * been dealt.
     * 
     * @return isDealt
     */
    public boolean getIsDealt()
    {
        return isDealt;
    }
    
    /**
     * Accepts boolean parameter that determines whether or not the given card
     * is used in determining the best hand that can be made with the given hand.
     * 
     * @param isUsed 
     */
    public void setIsUsed(boolean isUsed)
    {
        this.isUsed = isUsed;
    }
    
    /**
     * Returns whether or not the given card is used in determining the best hand
     * possible given the poker player's cards.
     * 
     * @return isUsed
     */
    public boolean getIsUsed()
    {
        return isUsed;
    }
    
    /**
     * Returns the numerical valuation of the rank of a card
     * 
     * @return rankValue
     */
    public int getRankValue()
    {
        return rankValue;
    }
    
    /**
     * Returns the character representation of the rank of a given card.
     * 
     * @return rank
     */
    public char getRank()
    {
        return rank;
    }
    
    /**
     * Returns the String representation of the rank of a given card.
     * 
     * @return rankDisplay
     */
    public String getRankDisplay()
    {
        return rankDisplay;
    }
    
    /**
     * Sets the numerical valuation and string representation of the rank of a given Card.
     * 
     * @param rank 
     */
    private void setRankValue(char rank)
    {
        validateRank(rank);
        this.rank = rank;
        
        switch(rank)
        {
            case '2':
                rankValue = 2;
                rankDisplay = "Two";
                break;           
            case '3':
                rankValue = 3;
                rankDisplay = "Three";
                break;                
            case '4':
                rankValue = 4;
                rankDisplay = "Four";
                break;            
            case '5':
                rankValue = 5;
                rankDisplay = "Five";
                break;               
            case '6':
                rankValue = 6;
                rankDisplay = "Six";
                break;               
            case '7':
                rankValue = 7;
                rankDisplay = "Seven";
                break;                
            case '8':
                rankValue = 8;
                rankDisplay = "Eight";
                break;               
            case '9':
                rankValue = 9;
                rankDisplay = "Nine";
                break;       
            case 'T':
                rankValue = 10;
                rankDisplay = "Ten";
                break;           
            case 'J':
                rankValue = 11;
                rankDisplay = "Jack";
                break;
            case 'Q':
                rankValue = 12;
                rankDisplay = "Queen";
                break;
            case 'K':
                rankValue = 13;
                rankDisplay = "King";
                break;
            case 'A':
                rankValue = 14;
                rankDisplay = "Ace";
                break;
        }
    }
    
    /**
     * Returns the character representation of the suit of a given Card.
     * 
     * @return suit
     */
    public char getSuit()
    {
        return suit;
    }
    
    /**
     * Returns the string representation of the suit of the given card.
     * 
     * @return suitDisplay
     */
    public String getSuitDisplay()
    {
        return suitDisplay;
    }
    
    /**
     * Sets the character and string representations of the suit of the given Card
     * 
     * @param suit 
     */
    private void setSuit(char suit)
    {
        validateSuit(suit);
        
        switch(suit)
        {
            case 's':
                this.suit = 's';
                suitDisplay = "Spades";
                break;
            case 'S':
                this.suit = 's';
                suitDisplay = "Spades";
                break;
            case 'h':
                this.suit = 'h';
                suitDisplay = "Hearts";
                break;
            case 'H':
                this.suit = 'h';
                suitDisplay = "Hearts";
                break;
            case 'c':
                this.suit = 'c';
                suitDisplay = "Clubs";
                break;
            case 'C':
                this.suit = 'c';
                suitDisplay = "Clubs";
                break;
            case 'd':
                this.suit = 'd';
                suitDisplay = "Diamonds";
                break;
            case 'D':
                this.suit = 'd';
                suitDisplay = "Diamonds";
                break;
        }
    }
    
    /**
     * Private method that verifies the given rank is contained within a given
     * 52 card deck.
     * 
     * @param rank 
     */
    private void validateRank(char rank)
    {
        String validRanks = "23456789TJQKA";
        int validRankIndex = 0;
        boolean valid = false;
        
        while(!valid && validRankIndex < validRanks.length())
        {
            valid = (validRanks.charAt(validRankIndex) == rank);
            validRankIndex++;
        }
        
        if(!valid)
            throw new InvalidRankException("The rank " + rank + " is not valid");
    }
    
    /**
     * Private method that verifies the given suit is contained within a given 
     * 52 card deck.
     * 
     * @param suit 
     */
    private void validateSuit(char suit)
    {
        String validSuits = "sShHcCdD";
        int validSuitIndex = 0;
        boolean valid = false;
        
        while(!valid && validSuitIndex < validSuits.length())
        {
            valid = (validSuits.charAt(validSuitIndex) == suit);
            validSuitIndex++;
        }
        
        if(!valid)
            throw new InvalidSuitException("The suit " + suit + " is not valid");
    }
    
    @Override
    public String toString()
    {
        StringBuilder cardToString = new StringBuilder();
        
        cardToString.append(rank);
        cardToString.append(suit);
        
        return cardToString.toString();
    }
}
