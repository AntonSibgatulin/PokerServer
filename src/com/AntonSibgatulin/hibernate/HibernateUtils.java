package com.AntonSibgatulin.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class HibernateUtils {



    public static  SessionFactory sessionFactory = buildSessionFactory();

    public static SessionFactory buildSessionFactory(){
        return new Configuration().configure(new File("hibernate.cfg.xml")).buildSessionFactory();
    }

}
