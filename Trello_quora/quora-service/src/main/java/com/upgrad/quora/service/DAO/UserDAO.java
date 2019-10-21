package com.upgrad.quora.service.DAO;

import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager manager;

    //persisting user to database
    @Transactional(propagation = Propagation.REQUIRED)
    public User CreateUser(User user){
        manager.persist(user);
        return user;
    }

    //retrieving based on email id
    public User getUserByEmail(final String email){
        try {
            return manager.createNamedQuery("byEmail", User.class).setParameter("email", email).getSingleResult();
        }catch (NoResultException r){
            return null;
        }
    }

    //retrieving based on user name
    public User getUserByUserName(final String userName){
        try {
            return manager.createNamedQuery("byUserName", User.class).setParameter("userName", userName).getSingleResult();
        }catch (NoResultException r){
            return null;
        }
    }

    //persisting user auth token to db
    public UserAuthToken createToken(UserAuthToken token){
        manager.persist(token);
        return token;
    }

    //retrieving based on jwt token
    public UserAuthToken fromJwtToken(final String token){
        try {
            return manager.createNamedQuery("byAuthToken",UserAuthToken.class).setParameter("accessToken",token).getSingleResult();
        }catch (NoResultException r){
            return null;
        }
    }

    //updating the token after logout
    public void update(UserAuthToken userAuthToken){
        manager.merge(userAuthToken);
    }

    //retrieving based on uuid
    public User userFromUuid(String uuid){
        try {
            return manager.createNamedQuery("byUuid",User.class).setParameter("uuid",uuid).getSingleResult();
        } catch (NoResultException r){
            return null;
        }
    }

    //removing user from database
    @Transactional(propagation = Propagation.REQUIRED)
    public User deleteUser(User user){
        manager.remove(user);
        return user;
    }

}
