package com.upgrad.quora.api.controller;

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
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

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

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> editAnswerContent(final AnswerRequest answerRequest, @PathVariable("answerId") final String answerUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException, AnswerNotFoundException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerUuid);
        answerEntity.setAns(answerRequest.getAnswer());
        final AnswerEntity editAnswerContent = answerService.editAnswerContent(answerEntity, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(editAnswerContent.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer(@PathVariable("answerId") final String answerUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = answerService.deleteAnswer(answerUuid,authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }
}
