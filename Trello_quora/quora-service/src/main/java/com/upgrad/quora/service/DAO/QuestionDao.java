package com.upgrad.quora.service.DAO;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthToken;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    // Repo function to get the user authantication object
    public UserAuthToken getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthToken.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    // Repo function for creating question by using persist method
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    // Repo function to get all questions from db
    public List<QuestionEntity> getAllQuestions(String authorization) {
        return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();
    }

    // Repo function to get all questions from db based on provided user uuid.
    public List<QuestionEntity> getAllQuestionsByUser(String userId, String authorization) {
        return entityManager.createNamedQuery("questionsByUser", QuestionEntity.class).setParameter("uuid", userId).getResultList();
    }

    // Repo function to get all questions from db based on provided question uuid
    public QuestionEntity getQuestionById(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Repo function for editing existing question content by using merge.
    public QuestionEntity editQuestionContent(final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }

    // Repo function for deleting question from db using remove method.
    public QuestionEntity deleteQuestion (QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
        return questionEntity;
    }
}
