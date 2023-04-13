package com.AntonSibgatulin.player;

import com.AntonSibgatulin.logic.PokerHand;
import com.AntonSibgatulin.table.TableModel;

public class Player {
    public int win = 0;
    public int lose = 0;
    public int money = 0;

    public int call = 0;
    public boolean moved = false;

    public String lastAction = "null";
    public boolean active = false;
    public TableModel tableModel = null;


    public PokerHand pokerHand = null;



    public Player (int money,int win,int lose){
       this.money = money;
       this.win = win;
       this.lose = lose;

    }
}
