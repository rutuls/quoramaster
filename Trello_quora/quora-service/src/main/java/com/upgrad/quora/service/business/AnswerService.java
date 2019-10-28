package com.upgrad.quora.service.business;

import com.upgrad.quora.service.DAO.AnswerDao;
import com.upgrad.quora.service.DAO.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthToken;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity, String questionUuid, String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        QuestionEntity questionEntity = questionDao.getQuestionById(questionUuid);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        } else if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        answerEntity.setUser(userAuthToken.getUser());
        answerEntity.setQuestionEntity(questionEntity);
        return answerDao.createAnswer(answerEntity);
    }
}
