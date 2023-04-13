package com.AntonSibgatulin.server;

import com.AntonSibgatulin.db.Database;
import com.AntonSibgatulin.table.TableModel;
import com.AntonSibgatulin.user.User;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Server extends WebSocketServer {


    public int commision = 0;

    public long tableId = 0;


    private HashSet<WebSocket> list = new HashSet<>();

    public HashMap<WebSocket,User> userHashMap = new HashMap<WebSocket, User>();

    public Database database = null;

    public HashMap<String, TableModel> tableModelHashMap = new HashMap<>();



    public void updateCountOfPeopleOnTable(TableModel tableModel){
        HashMap<WebSocket,User> list = (HashMap<WebSocket, User>) this.userHashMap.clone();
        for(Map.Entry<WebSocket,User> object:list.entrySet()) {
            if (object.getValue().player == null) {
                object.getValue().send("table_update;" + tableModel.getData());
            }
        }
    }

    public void sendTables(User... users) throws JSONException {
        for(int i = 0;i<users.length;i++){
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for(Map.Entry<String, TableModel> data:((HashMap<String, TableModel> )(tableModelHashMap.clone())).entrySet()){
                JSONObject jsonObject1 = new JSONObject(data.getValue().getData());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("tables",jsonArray);
            users[i].send("tables;"+jsonObject);
        }
    }

    public Server(Database database) throws URISyntaxException {
        super(new InetSocketAddress("192.168.0.107", 8080));
        this.database = database;
        System.out.println("Server was started ...");
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        list.add(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = userHashMap.get(webSocket);
                if(user!=null){
                    if(user.player !=null){
                        if(user.player.tableModel!=null){user.player.tableModel.exitUser(user);}
                    }
                    user.player = null;
                    userHashMap.remove(user.socket,user);
                    userHashMap.remove(user);

                }
                database.updateUser(user);
                list.remove(webSocket);

            }
        }).start();


    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(s==null || s.isEmpty()){
                    //should close connection
                    return;
                }
                User user = userHashMap.get(webSocket);
                if(user==null){

                    String[] split = s.split(";");
                    if(split[0].equals("auth")){
                        if(split.length>=2){
                            String login = split[1];
                            String password = split[2];

                            User userAuth = database.getUser(login,password);
                            if(userAuth!=null){
                                System.out.println(userAuth.toString());
                                webSocket.send("auth;true;"+userAuth.getMoney());
                                userHashMap.put(webSocket,userAuth);
                                userAuth.socket = webSocket;
                                try {
                                    sendTables(userAuth);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                webSocket.send("auth;false");
                            }
                        }
                    }


                }else{
                    String[] split = s.split(";");

                    if(split[0].equals("createtable")){
                        try {
                            JSONObject jsonObject = new JSONObject(s.replace(split[0]+";",""));
                            String name = jsonObject.getString("name");
                            int type = jsonObject.getInt("type");
                            int maximalUser = jsonObject.getInt("maximalUser");

                            tableId+=1;

                            String id = tableId+"@"+System.currentTimeMillis()+":"+System.currentTimeMillis();

                            TableModel tableModel = new TableModel(id,name,type,maximalUser);
                            tableModelHashMap.put(id,tableModel);
                            System.out.println(tableModel.getData());
                            sendAllUsers(tableModel);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    if(split[0].equals("join")){
                        String id = split[1];
                        TableModel tableModel = tableModelHashMap.get(id);
                        if(user.getMoney()-tableModel.maximal<0){
                            user.send("table;not_enough;money");
                            return;
                        }

                        tableModel.addUser(user,tableModel.maximal);

                    }


                    if(split[0].equals("game")){
                        if(user.player!=null && user.player.tableModel!=null){
                            user.player.tableModel.execute(s,user);
                        }
                    }
                }



            }
        }).start();

    }

    private void sendAllUsers(TableModel tableModel) {
        HashMap<WebSocket,User> list = (HashMap<WebSocket, User>) this.userHashMap.clone();
        for(Map.Entry<WebSocket,User> object:list.entrySet()) {
            if (object.getValue().player == null) {
                object.getValue().send("table;" + tableModel.getData());
            }
        }


    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }


    public HashSet<WebSocket> getList() {
        return list;
    }

    public void setList(HashSet<WebSocket> list) {
        this.list = list;
    }
}
