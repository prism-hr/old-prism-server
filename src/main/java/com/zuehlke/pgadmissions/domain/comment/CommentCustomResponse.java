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

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionConfigurationProperty;

@Entity
@Table(name = "COMMENT_CUSTOM_RESPONSE")
public class CommentCustomResponse {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @Column(name = "action_property_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionConfigurationProperty propertyType;

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

    public final PrismActionConfigurationProperty getPropertyType() {
        return propertyType;
    }

    public final void setPropertyType(PrismActionConfigurationProperty propertyType) {
        this.propertyType = propertyType;
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

    public CommentCustomResponse withPropertyType(PrismActionConfigurationProperty propertyType) {
        this.propertyType = propertyType;
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
