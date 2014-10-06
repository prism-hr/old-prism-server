package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION_VERSION")
public class CommentCustomQuestionVersion extends WorkflowResourceVersion {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_custom_question_id", nullable = false)
    private CommentCustomQuestion commentCustomQuestion;
    
    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "active", nullable = false)
    private Boolean active;
    
    @Column(name = "created_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommentCustomQuestion getCommentCustomQuestion() {
        return commentCustomQuestion;
    }

    public void setCommentCustomQuestion(CommentCustomQuestion commentCustomQuestion) {
        this.commentCustomQuestion = commentCustomQuestion;
    }

    @Override
    public final PrismLocale getLocale() {
        return locale;
    }

    @Override
    public final void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public final Boolean getActive() {
        return active;
    }

    @Override
    public final void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    
    public CommentCustomQuestionVersion withCommentCustomQuestion(CommentCustomQuestion commentCustomQuestion) {
        this.commentCustomQuestion = commentCustomQuestion;
        return this;
    }
    
    public CommentCustomQuestionVersion withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }
    
    public CommentCustomQuestionVersion withActive(Boolean active) {
        this.active = active;
        return this;
    }
    
    public CommentCustomQuestionVersion withContent(String content) {
        this.content = content;
        return this;
    }

}
