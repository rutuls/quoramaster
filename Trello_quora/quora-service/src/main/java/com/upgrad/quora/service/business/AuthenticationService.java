package com.upgrad.quora.service.business;

import com.upgrad.quora.service.DAO.UserDAO;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthToken;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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

    public UserAuthToken verify(final String token) throws SignOutRestrictedException {
        UserAuthToken authToken = repo.fromJwtToken(token);
        if(authToken == null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }

        return authToken;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(UserAuthToken userAuthToken){
        repo.update(userAuthToken);
    }

    public User getUser(String uuid,String token) throws AuthenticationFailedException, UserNotFoundException {
        UserAuthToken authToken = repo.fromJwtToken(token);
        User user = repo.userFromUuid(uuid);
        if(authToken == null){
            throw new AuthenticationFailedException("ATHR-001","User has not signed in");
        }
        if(user == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }
        if(authToken.getLogoutAt() != null){
            throw new AuthenticationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        return user;
    }

    public User deleteUser(String uuid,String token) throws UserNotFoundException, AuthenticationFailedException {
        UserAuthToken authToken = repo.fromJwtToken(token);
        User user = repo.userFromUuid(uuid);
        if(authToken == null){
            throw new AuthenticationFailedException("ATHR-001","User has not signed in");
        }
        if(user == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }
        if(authToken.getLogoutAt() != null){
            throw new AuthenticationFailedException("ATHR-002","User is signed out");
        }
        if(authToken.getUser().getRole().equals("nonadmin")){
            throw new AuthenticationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        return repo.deleteUser(user);
    }

}
