package com.AntonSibgatulin.db;

import com.AntonSibgatulin.hibernate.HibernateUtils;
import com.AntonSibgatulin.user.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Database {


    


    public Database(){
        HibernateUtils.sessionFactory.openSession();
    }



    public User getUser (String login,String password){


        Session session = HibernateUtils.sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM User u WHERE u.login=:login AND u.password = :password");
        query.setString("login",login);
        query.setString("password",password);
        query.setMaxResults(1);
        User user =(User) query.uniqueResult();
        session.getTransaction().commit();
        return user;
    }


    public User getUserById (Integer id){


        Session session = HibernateUtils.sessionFactory.getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM User u WHERE u.id=:id");
        query.setMaxResults(1);
        query.setInteger("id",id);
        User user =(User) query.uniqueResult();
        session.getTransaction().commit();
        return user;
    }




        public void updateUser(User user){
            Session session = HibernateUtils.sessionFactory.getCurrentSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();

        }


}
