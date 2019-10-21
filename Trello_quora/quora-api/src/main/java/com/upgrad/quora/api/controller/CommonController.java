package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.models.UserDetailsResponse;
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
public class CommonController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.GET,path = "userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") String uuid, @RequestHeader("autharization") final String autharization) throws AuthenticationFailedException, UserNotFoundException {
        User user = authenticationService.getUser(uuid,autharization);

        UserDetailsResponse response = new UserDetailsResponse().firstName(user.getFirstName()).lastName(user.getLastName())
                .userName(user.getUserName()).emailAddress(user.getEmail()).contactNumber(user.getContactnumber())
                .country(user.getCountry()).aboutMe(user.getAboutMe()).dob(user.getDob());
        return new ResponseEntity<UserDetailsResponse>(response, HttpStatus.OK);
    }
}
