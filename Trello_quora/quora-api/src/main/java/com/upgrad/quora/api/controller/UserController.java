package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.models.SigninResponse;
import com.upgrad.quora.api.models.SignoutResponse;
import com.upgrad.quora.api.models.SignupUserRequest;
import com.upgrad.quora.api.models.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthToken;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST,path = "user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest request) throws SignUpRestrictedException {
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmailAddress());
        user.setPassword(request.getPassword());
        user.setCountry(request.getCountry());
        user.setAboutMe(request.getAboutMe());
        user.setDob(request.getDob());
        user.setContactnumber(request.getContactNumber());
        final User got = service.createUser(user);
        SignupUserResponse response = new SignupUserResponse().id(got.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST,path = "user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization")final String autharization) throws AuthenticationFailedException {
        byte[] authBytes = Base64.getDecoder().decode(autharization.split("Basic ")[1]);

        String authText = new String(authBytes);
        String[] auth = authText.split(":");

        final UserAuthToken token = authenticationService.authenticate(auth[0],auth[1]);
        User user = token.getUser();
        SigninResponse response = new SigninResponse().id(user.getUuid()).
                message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();

        headers.add("access-token",token.getAccessToken());

        return new ResponseEntity<>(response,headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST,path = "user/signout",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("autharization") final String autharization) throws SignOutRestrictedException {
        UserAuthToken token = authenticationService.verify(autharization);
        token.setLogoutAt(ZonedDateTime.now());
        authenticationService.update(token);
        SignoutResponse response = new SignoutResponse().id(token.getUser().getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(response,HttpStatus.OK);

    }
}
