package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

@Entity
@Table(name = "SCORE")
public class Score implements Serializable {

    private static final long serialVersionUID = -8059348702240864331L;

    @Id
    @GeneratedValue
    private Integer id;

    @Basic
    @Column(name = "question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Basic
    @Column(name = "question")
    private String question;

    @Basic
    @Column(name = "text_response")
    private String textResponse;

    @Basic
    @Temporal(value = TemporalType.DATE)
    @Column(name = "date_response")
    private Date dateResponse;

    @Basic
    @Temporal(value = TemporalType.DATE)
    @Column(name = "second_date_response")
    private Date secondDateResponse;

    @Basic
    @Column(name = "rating_response")
    private Integer ratingResponse;

    @Transient
    private Question originalQuestion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTextResponse() {
        return textResponse;
    }

    public void setTextResponse(String textResponse) {
        this.textResponse = textResponse;
    }

    public Date getDateResponse() {
        return dateResponse;
    }

    public void setDateResponse(Date dateResponse) {
        this.dateResponse = dateResponse;
    }

    public Date getSecondDateResponse() {
        return secondDateResponse;
    }

    public void setSecondDateResponse(Date secondDateResponse) {
        this.secondDateResponse = secondDateResponse;
    }

    public Integer getRatingResponse() {
        return ratingResponse;
    }

    public void setRatingResponse(Integer ratingResponse) {
        this.ratingResponse = ratingResponse;
    }

    public Question getOriginalQuestion() {
        return originalQuestion;
    }

    public void setOriginalQuestion(Question originalQuestion) {
        this.originalQuestion = originalQuestion;
    }

}
