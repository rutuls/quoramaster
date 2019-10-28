package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.models.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // This function creates the question.It takes questionRequest model object,authorization tokens input and returns json response using ResponseEntity
    // It uses Request Method POST. Url path mapped is /question/create. This function consumes and produces json format input and output
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());

        final QuestionEntity createdQuestionEntity = questionService.createQuestion(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    // This function gets all the questions in list.It takes RequestHeader authorization token as input and returns json response using ResponseEntity
    // It uses Request Method GET. Url path mapped is /question/all. This function only produces json format input and output
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final List<QuestionEntity> questionEntities = questionService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionResponseList = new ArrayList<QuestionDetailsResponse>();
        for (int i = 0; i < questionEntities.size(); i++) {
            questionResponseList.add(new QuestionDetailsResponse().id(questionEntities.get(i).getUuid()).content(questionEntities.get(i).getContent()));
        }
        return new ResponseEntity<ArrayList>((ArrayList) questionResponseList, HttpStatus.OK);
    }

    // This function edits the question.It takes questionRequest model object,Path Variable Question ID and authorization tokens input and returns json response using ResponseEntity
    // It uses Request Method PUT. Url path mapped is /question/edit/questionid. This function consumes and produces json format input and output
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionRequest questionRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(questionUuid);
        questionEntity.setContent(questionRequest.getContent());
        final QuestionEntity editQuestionEntity = questionService.editQuestionContent(questionEntity, authorization);
        QuestionEditResponse questionResponse = new QuestionEditResponse().id(editQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionResponse, HttpStatus.OK);
    }

    // Deletes the question based on questionuuid and authorization token. This method uses request method as Delete.
    // This controller function in turn calls service funtion and returns ResponseEntity in json format
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        final QuestionEntity questionEntity = questionService.deleteQuestion(questionUuid, authorization);
        QuestionDeleteResponse questionResponse = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionResponse, HttpStatus.OK);
    }

    // This function gets all the questions in list based on provided user id as pathvariable.It takes Path Variable authorization token as input and returns json response using ResponseEntity
    // It uses Request Method GET. Url path mapped is /question/all/userid. This function only produces json format input and output
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllQuestionsByUser(@PathVariable("userId") String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        final List<QuestionEntity> questionEntities = questionService.getAllQuestionsByUser(userId, authorization);
        List<QuestionDetailsResponse> questionResponseList = new ArrayList<QuestionDetailsResponse>();
        for (int i = 0; i < questionEntities.size(); i++) {
            questionResponseList.add(new QuestionDetailsResponse().id(questionEntities.get(i).getUuid()).content(questionEntities.get(i).getContent()));
        }
        return new ResponseEntity<ArrayList>((ArrayList) questionResponseList, HttpStatus.OK);
    }
}
