package com.upgrad.quora.service.DAO;

import com.upgrad.quora.service.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager manager;

    public User CreateUser(User user){
        manager.persist(user);
        return user;
    }

    public User getUserByEmail(final String email){
        try {
            return manager.createNamedQuery("byEmail", User.class).setParameter("email", email).getSingleResult();
        }catch (NoResultException r){
            return null;
        }
    }

    public User getUserByUserName(final String userName){
        try {
            return manager.createNamedQuery("byUserName", User.class).setParameter("userName", userName).getSingleResult();
        }catch (NoResultException r){
            return null;
        }
    }

}
