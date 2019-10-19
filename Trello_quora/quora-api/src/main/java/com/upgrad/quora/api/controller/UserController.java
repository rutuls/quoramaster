package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.models.SignupUserRequest;
import com.upgrad.quora.api.models.SignupUserResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.POST,path = "user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest request) throws SignUpRestrictedException {
        User user = new User();
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
}
