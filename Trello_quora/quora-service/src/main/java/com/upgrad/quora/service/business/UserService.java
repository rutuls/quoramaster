package com.upgrad.quora.service.business;

import com.upgrad.quora.service.DAO.UserDAO;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO repo;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    public User createUser(User user) throws SignUpRestrictedException {
        if(repo.getUserByEmail(user.getEmail()) != null){
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }
        if(repo.getUserByUserName(user.getUserName()) != null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        String[] encrypted = cryptographyProvider.encrypt(user.getPassword());
        user.setPassword(encrypted[1]);
        user.setSalt(encrypted[0]);
        return repo.CreateUser(user);
    }
}
