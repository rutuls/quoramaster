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

    // Repository function creates the new answer in db using persist method.
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    // Repository function gets the answer from db based on uuid using NamedQuery.
    public AnswerEntity getAnswerById(final String uuid) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // Repository function edits the existing answer in db using merge method.
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    // Repository function deletes the answer in db using remove method.
    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    // Repository function gets the new answer for particular question in db using named query
    public List<AnswerEntity> getAllAnswersToQuestion(QuestionEntity questionEntity){
        try {
            return entityManager.createNamedQuery("getAllAnsToQuest",AnswerEntity.class).setParameter("questionEntity",questionEntity).getResultList();
        }catch (NoResultException nre) {
            return null;
        }
    }
}
