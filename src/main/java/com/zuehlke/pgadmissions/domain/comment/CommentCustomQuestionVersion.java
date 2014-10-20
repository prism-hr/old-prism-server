package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION_VERSION")
public class CommentCustomQuestionVersion implements IUniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_custom_question_id", insertable = false, updatable = false)
    private CommentCustomQuestion commentCustomQuestion;
    
    @Column(name = "name", insertable = false, updatable = false)
    private String name;

    @Lob
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

    public void setCommentCustomQuestion(CommentCustomQuestion commentCustomQuestion) {
        this.commentCustomQuestion = commentCustomQuestion;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
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
    
    public CommentCustomQuestionVersion withName(String name) {
        this.name = name;
        return this;
    }
    
    public CommentCustomQuestionVersion withContent(String content) {
        this.content = content;
        return this;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("commentCustomQuestion", commentCustomQuestion).addProperty("name", "name");
    }

}
