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

    @Transactional(propagation = Propagation.REQUIRED)
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

    public UserAuthToken createToken(UserAuthToken token){
        manager.persist(token);
        return token;
    }

    public UserAuthToken fromJwtToken(final String token){
        try {
            return manager.createNamedQuery("byAuthToken",UserAuthToken.class).setParameter("accessToken",token).getSingleResult();
        }catch (NoResultException r){
            return null;
        }
    }

    public void update(UserAuthToken userAuthToken){
        manager.merge(userAuthToken);
    }

    public User userFromUuid(String uuid){
        try {
            return manager.createNamedQuery("byUuid",User.class).setParameter("uuid",uuid).getSingleResult();
        } catch (NoResultException r){
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User deleteUser(User user){
        manager.remove(user);
        return user;
    }

}
