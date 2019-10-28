package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.models.AnswerDeleteResponse;
import com.upgrad.quora.api.models.AnswerDetailsResponse;
import com.upgrad.quora.api.models.AnswerRequest;
import com.upgrad.quora.api.models.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    // This function creates the answer for provided question id as path variable.It takes AnswerRequest model object,question uuid and authorization tokens input and returns json response using ResponseEntity which includes answer uuid and status
    // It uses Request Method POST. This function consumes and produces json format input and output
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(AnswerRequest answerRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        // answerEntity.setUuid(questionUuid);
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());
        final AnswerEntity createdAnswerEntity = answerService.createAnswer(answerEntity, questionUuid, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    // This function allows user to edit the answer based on provided answerid as path variable. It takes answerRequest as model object, answeruuid from path variable and authorization token as request header
    // This produces and consumes json request and response
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> editAnswerContent(final AnswerRequest answerRequest, @PathVariable("answerId") final String answerUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException, AnswerNotFoundException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerUuid);
        answerEntity.setAns(answerRequest.getAnswer());
        final AnswerEntity editAnswerContent = answerService.editAnswerContent(answerEntity, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(editAnswerContent.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    // This function allows user to delete answer based on answer id
    // Request method is delete. function params are PathVariable answerId, Request header as authorization token. This produces json response
    // This function returns answer uuid and status as output
    @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.deleteAnswer(answerUuid,authorization);
        AnswerDeleteResponse answerResponse = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerResponse, HttpStatus.OK);
    }

    // This function gets all the answers for provided question id as path variable,question uuid and authorization tokens input and returns json response using ResponseEntity which includes answer uuid and status
    // It uses Request Method GET. This function produces json format output
    @RequestMapping(method = RequestMethod.GET,path = "answer/all/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllAnswersToQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        List<AnswerEntity> answers = answerService.getAllAnswersToQuestion(questionId,authorization);
        List<AnswerDetailsResponse> responses = new ArrayList<>();
        for(AnswerEntity a : answers){
            responses.add(new AnswerDetailsResponse().id(a.getUuid()).answerContent(a.getAns()).questionContent(a.getQuestionEntity().getContent()));
        }
        return new ResponseEntity<ArrayList>((ArrayList)responses,HttpStatus.OK);
    }
}
