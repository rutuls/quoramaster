package com.upgrad.quora.service.business;

import com.upgrad.quora.service.DAO.UserDAO;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthToken;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserDAO repo;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthToken authenticate(final String userName, final String password) throws AuthenticationFailedException {
        User user = repo.getUserByUserName(userName);
        if(user == null){
            throw  new AuthenticationFailedException("ATH-001","This username does not exist");
        }
        String encryptedPassword = cryptographyProvider.encrypt(password,user.getSalt());
        if(encryptedPassword.equals(user.getPassword())){
            JwtTokenProvider jwt = new JwtTokenProvider(encryptedPassword);
            UserAuthToken userAuthToken = new UserAuthToken();
            userAuthToken.setUser(user);
            userAuthToken.setUuid(UUID.randomUUID().toString());

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwt.generateToken(user.getUuid(),now,expiresAt));
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            repo.createToken(userAuthToken);

            return userAuthToken;

        }else {
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }

}
