package com.AntonSibgatulin.logic;

import java.util.Random;

/**
 * CardDeck is a wrapper class that contains all the cards that would be found
 * in a 52 card deck and allows for shuffling and dealing of the cards.
 * 
 * @author Zach Richardson
 */
public class CardDeck {
    private Card[] deck;
    private String ranks;
    private String suits;
    private int countDealt;
    
    /**
     * Constructor that initializes the size of the deck array and the 
     * valid suit and rank characters contained within the deck.
     */
    public CardDeck()
    {
        deck = new Card[52];
        ranks = "23456789TJQKA";
        suits = "shcd";
        countDealt = 0;
        createDeck();
    }
    
    /**
     * Returns a standard 52 card deck
     * 
     * @return deck
     */
    public Card[] getDeck()
    {
        return deck;
    }
    
    /**
     * Initializes each card of a standard 52 card deck
     */
    private void createDeck()
    {
        int cardNumber = 0;
        
        for(int j = 0; j < suits.length(); j++)
            for(int i = 0; i < ranks.length(); i++)
            {
                deck[cardNumber] = new Card(ranks.charAt(i), suits.charAt(j));
                cardNumber++;
            }
    }
    
    /**
     * Randomizes the 52 cards of a standard playing deck by 
     * switching cards in random order 1000 times.
     */
    public void shuffleDeck()
    {
        Random generator = new Random();
        for(int i = 0; i < 1000; i++)
        {
            int sourceCard = generator.nextInt(51);
            int destinationCard = generator.nextInt(51);
            
            Card tempCard = deck[destinationCard];
            deck[destinationCard] = deck[sourceCard];
            deck[sourceCard] = tempCard;
        }
    }
    
    /**
     * Distributes a card from the deck.
     * 
     * @return nextDeal
     */
    public Card dealCard()
    {
        deck[countDealt].setIsDealt(true);
        Card nextDeal = deck[countDealt];
        countDealt++;
        
        return nextDeal;
    }
    
    @Override
    public String toString()
    {
        StringBuilder cardDeckToString = new StringBuilder();
        
        cardDeckToString.append("[");
        cardDeckToString.append(deck[0]);
        
        for(int i = 1; i < deck.length; i++)
        {
            cardDeckToString.append(", ");
            cardDeckToString.append(deck[i]);
        }
        
        cardDeckToString.append("]");
        
        return cardDeckToString.toString();
    }
}
