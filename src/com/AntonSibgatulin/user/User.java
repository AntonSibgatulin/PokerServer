package com.AntonSibgatulin.user;

import com.AntonSibgatulin.player.Player;
import org.hibernate.annotations.Entity;
import org.java_websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public static final long maxTimeOnMoving = 60*1000L;



    public Player player = null;
    public Long timeStart = 0L;

    public WebSocket socket = null;
    public void send(String str){
if(socket.isClosed()==false && socket.isClosing()==false && socket.isOpen()==true)
        socket.send(str);
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id = null;
    @Column(name = "login")
    private String login = null;
    @Column(name = "password")
    private String password  = null;

    @Column(name = "type")
    private Integer type = null;

    @Column(name = "money")
    private Integer money = null;

    @Column(name = "score")
    private Integer score = null;

    @Column(name = "ban")
    private Integer ban = null;


    public User(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getBan() {
        return ban;
    }

    public void setBan(Integer ban) {
        this.ban = ban;
    }

    @Override
    public String toString(){
        return "id: "+id+" & login: "+login+" & password: "+password+" & type: "+type+" money: "+money;
    }



    public JSONObject getPlayerData(){
        if(player==null){
            return new JSONObject();
        }
        JSONObject json = new JSONObject();
        try {
            json.put("login",login);
            json.put("money",player.money);
            json.put("active",player.active);
            json.put("lastAction",player.lastAction);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;


    }




}
