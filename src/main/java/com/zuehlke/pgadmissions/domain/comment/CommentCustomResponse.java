package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.workflow.ActionCustomQuestionConfiguration;

@Entity
@Table(name = "COMMENT_CUSTOM_RESPONSE")
public class CommentCustomResponse {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "action_custom_question_configuration_id", nullable = false)
    private ActionCustomQuestionConfiguration actionCustomQuestionConfiguration;

    @Lob
    @Column(name = "property_value", nullable = false)
    private String propertyValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ActionCustomQuestionConfiguration getActionCustomQuestionConfiguration() {
        return actionCustomQuestionConfiguration;
    }

    public void setActionCustomQuestionConfiguration(ActionCustomQuestionConfiguration actionCustomQuestionConfiguration) {
        this.actionCustomQuestionConfiguration = actionCustomQuestionConfiguration;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public CommentCustomResponse withPropertyValue(String value) {
        this.propertyValue = value;
        return this;
    }

    public CommentCustomResponse withActionCustomQuestionConfiguration(ActionCustomQuestionConfiguration actionCustomQuestionConfiguration) {
        this.actionCustomQuestionConfiguration = actionCustomQuestionConfiguration;
        return this;
    }

}
