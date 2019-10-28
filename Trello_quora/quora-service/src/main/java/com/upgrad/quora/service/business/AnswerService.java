package com.upgrad.quora.service.business;

import com.upgrad.quora.service.DAO.AnswerDao;
import com.upgrad.quora.service.DAO.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthToken;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
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

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity, String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        AnswerEntity existingAnswer = answerDao.getAnswerById(answerEntity.getUuid());
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (existingAnswer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all answers");
        } else if (userAuthToken.getUser().getId() != existingAnswer.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        answerEntity.setId(existingAnswer.getId());
        answerEntity.setUuid(existingAnswer.getUuid());
        answerEntity.setDate(existingAnswer.getDate());
        answerEntity.setUser(existingAnswer.getUser());
        answerEntity.setQuestionEntity(existingAnswer.getQuestionEntity());
        return answerDao.editAnswerContent(answerEntity);
    }

    public AnswerEntity deleteAnswer(final String uuid,String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        AnswerEntity existingAnswer = answerDao.getAnswerById(uuid);
        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }
        if( existingAnswer == null){
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        else {
            if(userAuthToken.getUser().getRole() == "admin"| existingAnswer.getUser() == userAuthToken.getUser()){
                return answerDao.deleteAnswer(existingAnswer);
            }
            else {
                throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
            }
        }

    }
}
