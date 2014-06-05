package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION_VERSION")
public class CommentCustomQuestionVersion {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_custom_question_id", nullable = false)
    private CommentCustomQuestion commentCustomQuestion;

    @Column(name = "content", nullable = false)
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommentCustomQuestion getCommentCustomQuestion() {
        return commentCustomQuestion;
    }

    public void setCommentCustomVersion(CommentCustomQuestion commentCustomQuestion) {
        this.commentCustomQuestion = commentCustomQuestion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public CommentCustomQuestionVersion withCommentCustomQuestion(CommentCustomQuestion commentCustomQuestion) {
        this.commentCustomQuestion = commentCustomQuestion;
        return this;
    }
    
    public CommentCustomQuestionVersion withContent(String content) {
        this.content = content;
        return this;
    }

}
