package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "question")
@NamedQueries({
        @NamedQuery(name = "allQuestions", query = "select q from QuestionEntity q"),
        @NamedQuery(name = "questionByUuid", query = "select q from QuestionEntity q where q.uuid =:uuid"),
        @NamedQuery(name = "questionsByUser", query = "select q from QuestionEntity q,User u where q.user = u.id AND u.uuid =:uuid")
})
public class QuestionEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "CONTENT")
    @Size(max = 500)
    private String content;

    @Column(name = "DATE")
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
