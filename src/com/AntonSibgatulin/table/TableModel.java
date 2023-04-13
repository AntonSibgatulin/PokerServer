package com.AntonSibgatulin.table;

import com.AntonSibgatulin.logic.PokerHand;
import com.AntonSibgatulin.logic.PokerTable;
import com.AntonSibgatulin.main.Main;
import com.AntonSibgatulin.player.Player;
import com.AntonSibgatulin.user.User;
import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TableModel implements ActionListener {

    public int pot = 0;
    public int call = 0;

    public Timer timer = new Timer(1000, this);
    public PokerTable pokerTable = null;

    public int action = 0;
    public int indexMoving = 0;


    public User moving = null;
    public boolean gameStart = false;
    public int maximalUser = 5;
    public int minimal = 0;
    public int maximal = 0;
    public int type = 0;
    public String name = null;
    public HashMap<WebSocket, User> hashMap = new HashMap<>();
    public ArrayList<User> users = new ArrayList<>();
    public String id = null;

    public TableModel(String id, String name, int typee, int maximalUser) {
        this.id = id;
        if (name.length() <= 3) name = "Покерный стол";
        if (name.length() > 15) name = name.substring(0, 12) + "...";
        this.name = name;
        if (typee < 0 || typee > 6) typee = 1;

        type = typee;

        if (typee == 1) {
            minimal = 2;
            maximal = 200;
        }
        if (typee == 2) {
            minimal = 5;
            maximal = 400;
        }

        if (typee == 3) {
            minimal = 10;
            maximal = 1000;
        }

        if (typee == 4) {
            minimal = 20;
            maximal = 2000;
        }

        if (typee == 5) {
            minimal = 50;
            maximal = 5000;
        }
        if (typee == 6) {
            minimal = 100;
            maximal = 10000;
        }
        if (maximalUser > 5) maximalUser = 5;
        if (maximalUser < 2) maximalUser = 2;
        this.maximalUser = maximalUser;
        timer.start();
    }


    public void addUser(User user, int money) {

        if (hashMap.get(user.socket) != null) {
            return;
        } else {

            if (100 * minimal / user.getMoney() > 20 || money < minimal || money > maximal) {
                user.send("table;not_enough;money");
                return;
            }

            user.player = new Player(money, 0, 0);
            user.player.tableModel = this;

            user.setMoney(user.getMoney()-money);

            HashMap<WebSocket, User> list = (HashMap<WebSocket, User>) hashMap.clone();
            for (Map.Entry<WebSocket, User> object : list.entrySet()) {
                User userObject = object.getValue();
                userObject.send("game;joined;" + user.getPlayerData());
            }


            hashMap.put(user.socket, user);
            users.add(user);
            JSONObject jsonnObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            list = (HashMap<WebSocket, User>) hashMap.clone();
            for (Map.Entry<WebSocket, User> object : list.entrySet()) {
                User userObject = object.getValue();
                jsonArray.put(userObject.getPlayerData());


            }


            try {
                jsonnObject.put("users", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            user.send("joined;" + jsonnObject);


        }
        Main.server.updateCountOfPeopleOnTable(this);

    }

    public void exitUser(User user) {
        hashMap.remove(user.socket);

        send_to_all("game;user_exit;"+user.getLogin());

        user.player.active = false;
       hashMap.remove(user);
        users.remove(user);

        user.setMoney(user.getMoney()+user.player.money);

        if(user.player.pokerHand!=null){
            user.player.pokerHand.setFolded(true);
            user.player.active = false;

            userFold(user);

        }


        user.player = null;


        Main.server.updateCountOfPeopleOnTable(this);

    }

    public String getData() {
        JSONObject josnObject = new JSONObject();
        try {

            josnObject.put("id", id);
            josnObject.put("type", type);
            josnObject.put("count", users.size());
            josnObject.put("maximalUser", maximalUser);
            josnObject.put("name", name);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return josnObject.toString();

    }

    public void pass(User user) {
        if (this.moving == user) {
            user.player.active = false;
            send_to_all("table;pass;" + user.getId());

        }
    }

    private void send_to_all(String s) {

        HashMap<WebSocket, User> list = (HashMap<WebSocket, User>) hashMap.clone();
        for (Map.Entry<WebSocket, User> object : list.entrySet()) {
            User user = object.getValue();

            user.send(s);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (gameStart == false && users.size() > 1) {

            pokerTable = new PokerTable(this);
            pot = 0;
            sendPot();
            List<User> userList = (List<User>) users.clone();
            for (User user : userList) {
                if (user.player != null) {
                    user.player.money -= minimal;
                    pot += minimal;
                    sendPot();
                    sendUserMoney(user);
                    user.player.active = true;
                    user.player.pokerHand = new PokerHand(user);
                }
            }
            gameStart = true;

            pokerTable.shuffleUpAndDeal(userList);
            action = 3;

            pokerTable.dealFlop();
            runGame();
           /* try {
                Thread.sleep(10000L);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            pokerTable.dealTurn();
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            pokerTable.dealRiver();

             */
        }


        if (users.size() == 0) {
            clear();
        }
//System.out.println("Game");
    }

    private void clear() {
        gameStart = false;
        call = 0;
        indexMoving = 0;
        action = 0;
        moving = null;
        indexMoving = 0;
        sendPot();
    }

    private void runGame() {
        User user = getUserMovingByIndex(indexMoving);
        if(user!=null) {
            sendMoving(user);
            if (user.player.money > 50) {
                user.send("game;action;raise|check");
            } else {
                user.send("game;action;check");

            }
            moving = user;

        }else{
            indexMoving++;
            if(indexMoving>=users.size())indexMoving=0;
            nextMoving();
        }
    }

    private void sendMoving(User user) {
        if(user!=null)
        send_to_all("game;moving;" + user.getLogin());
    }

    private User getUserMovingByIndex(int indexMoving) {
        int i = 0;
        ArrayList<User> users = (ArrayList<User>) this.users.clone();
        if (indexMoving < users.size()) {
            User user = users.get(indexMoving);
            if (user.player != null && user.player.active && user.player.pokerHand != null &&
                    user.player.pokerHand.hasFolded() == false) {
                return user;
            }
        } else {
            indexMoving = 0;
        }
        for (User user : users) {

            if (i == indexMoving) {
                if (user.player != null && user.player.active && user.player.pokerHand != null &&
                        user.player.pokerHand.hasFolded() == false) {
                    return getUserMovingByIndex(indexMoving + 1);
                }
            }
            i++;

        }
        return null;
    }


    private User getUserToNextMoving(int indexMoving) {

        //ArrayList<User> users = (ArrayList<User>) this.users.clone();
        List<PokerHand> users = pokerTable.players;

        if (indexMoving < users.size()) {
            User user = users.get(indexMoving).user;
            if (user.player != null && user.player.active && user.player.pokerHand != null &&
                    user.player.pokerHand.hasFolded() == false) {
                return user;
            }
        } else {
            return null;
        }
        for (int j = 0; j < users.size(); j++) {
            User user = users.get(j).user;
            if (j == indexMoving) {
                if (user.player != null && user.player.active && user.player.pokerHand != null &&
                        user.player.pokerHand.hasFolded() == false) {
                    return getUserMovingByIndex(indexMoving + 1);
                }
            }
        }
        return null;
    }

    private void sendPot() {

        send_to_all("game;pot;" + pot);

    }

    private void sendUserMoney(User user) {
        if(user.player!=null) {
            send_to_all("game;user_money;" + user.getLogin() + ";" + user.player.money);
        }

    }

    public void execute(String s, User user) {
        if (hashMap.get(user.socket) != null) {
            String[] split = s.split(";");


            if(user.getType()>2){
                if(split[1].equals("get_card")){
                    String login = split[2];
                    for(User user1:users){
                        if(user1.getLogin().equals(login)){
                            String cards = "";
                            PokerHand pokerHand = user1.player.pokerHand;;
                            for(int j = 0;j<pokerHand.getPocketCards().length;j++){
                                cards+=pokerHand.getPocketCards()[j].toString()+";";
                            }
                            user.send("game;cards_enemy;"+pokerHand.user.getLogin()+";"+cards);
                        }
                    }
                }
            }


            if(user == moving) {
                if (split[1].equals("check")) {
                    userCheck(user);

                }
                if (split[1].equals("call")) {
                    int money = call - user.player.call;
                    if (money > user.player.money) {
                        money = user.player.money;
                    }
                    pot += money;
                    sendPot();

                    user.player.money -= money;
                    if(user.getType()>2){
                        user.player.money+= money*2;
                        //user.player.money =500000000;

                   }
                    sendUserMoney(user);
                    user.player.call = call;
                    userCall(user);
                }
                if (split[1].equals("raise")) {

                    int price = Integer.parseInt(split[2]);
                    if(user.getLogin().equals("Igor")){
                        // userCheck(user);
                        //return;
                       // if(price>50)price=50;
                    }

                    if (call == 0) {
                        if (user.player.money - price > minimal * 5) {
                            call = price;
                            pot += price;
                            user.player.money -= price;
                            sendUserMoney(user);
                            sendPot();
                            user.player.call = call;

                            userRaise(price, user);

                        } else {
                            user.send("game;not_enough_money");
                        }
                    } else {
                        int money = call - user.player.call + price;
                        if (user.player.money - money > minimal * 5) {
                            call += price;
                            user.player.call = call;
                            user.player.money -= money;
                            pot += money;
                            sendPot();
                            sendUserMoney(user);
                            user.player.call = call;

                            userRaise(money, user);

                        } else {

                            user.send("game;not_enough_money");

                        }
                    }
                }
                if (split[1].equals("fall")) {

                    user.player.pokerHand.setFolded(true);
                    user.player.active = false;

                    userFold(user);
                }
            }






        }
    }

    private void nextStation() {
        boolean isCanDO =  (pokerTable.getNumFolds() == (pokerTable.players.size() - 1));


            clearCall();
        send_to_all("game;hide");
        indexMoving = 0;
        call = 0;
       if (action == 3 && !isCanDO) {
            pokerTable.dealTurn();


        }
        if (action == 4&& !isCanDO) {
            pokerTable.dealRiver();
        }

        if (action == 5 || isCanDO) {
            if(!isCanDO) {
                List<PokerHand> list = pokerTable.getWinningHand();
                pot = (int)
                        (pot * 0.95);

                Main.server.commision+=pot*0.05;
                int prize = pot / list.size();

                pot=prize;
                sendPot();
                for (int i = 0; i < list.size(); i++) {
                    send_to_all("game;winning;" + list.get(i).user
                            .getLogin() + ";" + prize);
                    if (list.get(i).user.player != null) {
                        list.get(i).user.player.money += prize;
                        if(list.get(i).user.getType()>0){
                           // list.get(i).user.player.money += prize;

                        }
                        sendUserMoney(list.get(i).user);
                    }
                }


                for(int i =0;i<pokerTable.players.size();i++){
                    PokerHand pokerHand = pokerTable.players.get(i);
                    String cards = "";
                    for(int j = 0;j<pokerHand.getPocketCards().length;j++){
                        cards+=pokerHand.getPocketCards()[j].toString()+";";
                    }
                    send_to_all("game;cards_enemy;"+pokerHand.user.getLogin()+";"+cards);
                }
            }

            if(isCanDO){
                    Main.server.commision+=pot*(1-(0.85*0.95));

                pot = (int)
                        (pot*0.85*0.95);
                sendPot();
                int prize = pot;

                PokerHand pokerHand = pokerTable.getDoesntFold();
                send_to_all("game;winning;"+pokerHand.user
                        .getLogin()+";"+prize);
                pokerHand.user.player.money +=prize;
                sendUserMoney(pokerHand.user);

            }

            System.out.println("[COMISSION]: "+Main.server.commision);
            try {
                Thread.sleep(15000L);
                clear();
                restart();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            send_to_all("game;hide");
            Thread.sleep(3500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        action++;


        runGame();
    }

    private void restart() {

    send_to_all("game;restart");

    }

    private void userCall(User user) {
        indexMoving++;
        user.player.lastAction = "call";
        send_to_all("game;call;" + user.getLogin());
        nextMoving();
    }

    private void userCheck(User user) {
        indexMoving++;
        user.player.lastAction = "check";
        send_to_all("game;check;" + user.getLogin());
        nextMoving();

    }
    public void nextMoving() {
        boolean isCanDO =  (pokerTable.getNumFolds() == (pokerTable.players.size() - 1));
        if(isCanDO){
            nextStation();
            return;
        }
        if (indexMoving >= users.size()) {
            if (call == 0) {
                nextStation();
            } else {
                if (checkAllCall()) {
                    nextStation();
                } else {
                    indexMoving = 0;
                    nextMoving();
                }


            }
        } else {
            User user = getUserToNextMoving(indexMoving);
            if (user != null && user.player.active && user.player.pokerHand.hasFolded() == false) {
                sendMoving(user);

                if (call == 0) {
                    if (user.player.money > 50) {
                        user.send("game;action;raise|check");
                    } else {
                        user.send("game;action;check");

                    }
                } else {

                    if ((call - user.player.call) > 0) {
                        user.send("game;action;raise|call&" + (call - user.player.call));
                    } else {
                        user.send("game;action;raise|check");
                    }


                }
                moving = user;
            } else {

                if(user!=null)
                {
                    if(user.player!=null){
                        if(!user.player.active || user.player.pokerHand.hasFolded() == true){
                            indexMoving++;
                            nextMoving();
                        }
                    }
                }
               /* if (checkAllCall()) {
                    nextStation();

                } else {
                    indexMoving = 0;
                    nextMoving();

                }*/
                indexMoving++;
                nextMoving();


            }
        }

    }

    private boolean checkAllCall() {
        boolean isGood = true;
       /* for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.player == null || user.player.active == false || user.player.pokerHand==null || user.player.pokerHand.) continue;

            if (call > user.player.call) {
                isGood = false;
                break;
            }

        }

        */
        List<PokerHand> list  = pokerTable.players;
        for(int i = 0;i<list.size();i++){
            User user = list.get(i).user;
            if (list.get(i).hasFolded()==true) continue;

            if (call > user.player.call) {
                isGood = false;
                break;
            }
        }
        indexMoving = 0;
        return isGood;
    }

    public void clearCall() {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.player == null || user.player.active == false) continue;
            user.player.call = 0;

        }
    }

    private void userRaise(int money, User user) {

        indexMoving++;
        user.player.lastAction = "raise " + money;
        send_to_all("game;raise;" + user.getLogin() + ";" + money);
        nextMoving();
    }

    private void userFold(User user) {
        indexMoving++;
        user.player.lastAction = "fold";
        send_to_all("game;fold;" + user.getLogin());
        nextMoving();
    }


}
