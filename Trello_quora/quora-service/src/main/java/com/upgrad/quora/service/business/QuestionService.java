package com.upgrad.quora.service.business;

import com.upgrad.quora.service.DAO.QuestionDao;
import com.upgrad.quora.service.DAO.UserDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthToken;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDAO userDao;

    // Service methods for creating question. It verifies authorization checks and if user is authorized it calls repository layer to insert value in db.
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String authorizationToken) throws AuthorizationFailedException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorizationToken);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUser(userAuthToken.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    // Service func for getting all questions if user is authorized
    public List<QuestionEntity> getAllQuestions(String authorization) throws AuthorizationFailedException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return questionDao.getAllQuestions(authorization);
    }

    // Service funtion for getting all questions based on user id.
    public List<QuestionEntity> getAllQuestionsByUser(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        User user = userDao.userFromUuid(userId);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        } else if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(userId, authorization);
    }

    // Service function for editing existing question content. Only authorized user can edit the question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity, String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        QuestionEntity existingQuestion = questionDao.getQuestionById(questionEntity.getUuid());
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        } else if (userAuthToken.getUser().getId() != existingQuestion.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        } else if (existingQuestion == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        questionEntity.setId(existingQuestion.getId());
        questionEntity.setUuid(existingQuestion.getUuid());
        questionEntity.setDate(existingQuestion.getDate());
        questionEntity.setUser(existingQuestion.getUser());
        return questionDao.editQuestionContent(questionEntity);
    }

    // This service method deletes the question if user is authorized
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String questionUuid, String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthToken userAuthToken = questionDao.getUserAuthToken(authorization);
        QuestionEntity questionEntity = questionDao.getQuestionById(questionUuid);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else if (userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        } else if (userAuthToken.getUser().getId() != questionEntity.getUser().getId() && !userAuthToken.getUser().getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        return questionDao.deleteQuestion(questionEntity);
    }
}
