package com.zuehlke.pgadmissions.domain.comment;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismCustomQuestionType;

@Entity
@Table(name = "COMMENT_CUSTOM_RESPONSE")
public class CommentCustomResponse {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @Column(name = "custom_question_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismCustomQuestionType customQuestionType;

    @Lob
    @Column(name = "property_label", nullable = false)
    private String propertyLabel;

    @Lob
    @Column(name = "property_value", nullable = false)
    private String propertyValue;

    @Column(name = "property_weight")
    private BigDecimal propertyWeight;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Comment getComment() {
        return comment;
    }

    public final void setComment(Comment comment) {
        this.comment = comment;
    }

    public final PrismCustomQuestionType getCustomQuestionType() {
        return customQuestionType;
    }

    public final void setCustomQuestionType(PrismCustomQuestionType customQuestionType) {
        this.customQuestionType = customQuestionType;
    }

    public final String getPropertyLabel() {
        return propertyLabel;
    }

    public final void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public final String getPropertyValue() {
        return propertyValue;
    }

    public final void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public final BigDecimal getPropertyWeight() {
        return propertyWeight;
    }

    public final void setPropertyWeight(BigDecimal propertyWeight) {
        this.propertyWeight = propertyWeight;
    }

    public CommentCustomResponse withCustomQuestionType(PrismCustomQuestionType customQuestionType) {
        this.customQuestionType = customQuestionType;
        return this;
    }

    public CommentCustomResponse withPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
        return this;
    }

    public CommentCustomResponse withPropertyValue(String value) {
        this.propertyValue = value;
        return this;
    }

    public CommentCustomResponse withPropertyWeight(BigDecimal propertyWeight) {
        this.propertyWeight = propertyWeight;
        return this;
    }

}
