package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COMMENT_CUSTOM_QUESTION")
public class CustomQuestionVersion {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_custom__id")
    private CustomQuestion customQuestion;

    @Column(name = "content")
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomQuestion getCustomQuestion() {
        return customQuestion;
    }

    public void setCustomQuestion(CustomQuestion customQuestion) {
        this.customQuestion = customQuestion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
