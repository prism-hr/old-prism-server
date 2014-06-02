package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION_VERSION")
public class CustomQuestionVersion {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_custom__id", nullable = false)
    private CommentCustomQuestion customQuestion;

    @Column(name = "content", nullable = false)
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommentCustomQuestion getCustomQuestion() {
        return customQuestion;
    }

    public void setCustomQuestion(CommentCustomQuestion customQuestion) {
        this.customQuestion = customQuestion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public CustomQuestionVersion withCustomQuestion(CommentCustomQuestion customQuestion) {
        this.customQuestion = customQuestion;
        return this;
    }
    
    public CustomQuestionVersion withContent(String content) {
        this.content = content;
        return this;
    }

}
