package com.AntonSibgatulin.main;

import com.AntonSibgatulin.db.Database;
import com.AntonSibgatulin.server.Server;

import java.net.URISyntaxException;

public class Main {

    public static Server server = null;

    public static void main(String...args) throws URISyntaxException {


        Database databasea = new Database();
         server = new Server(databasea);
        server.start();

    }
}
