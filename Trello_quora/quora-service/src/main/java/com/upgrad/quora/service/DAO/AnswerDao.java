package com.upgrad.quora.service.DAO;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerById(final String uuid) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersToQuestion(QuestionEntity questionEntity){
        try {
            return entityManager.createNamedQuery("getAllAnsToQuest",AnswerEntity.class).setParameter("questionEntity",questionEntity).getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }
}
