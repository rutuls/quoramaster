package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.models.UserDeleteResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.DELETE,path = "admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") String uuid, @RequestHeader("authorization") final String autharization) throws AuthenticationFailedException, UserNotFoundException {
        User user = authenticationService.deleteUser(uuid,autharization);
        UserDeleteResponse response = new UserDeleteResponse().id(user.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(response, HttpStatus.OK);
    }
}