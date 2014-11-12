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

import com.zuehlke.pgadmissions.domain.definitions.ActionPropertyType;

@Entity
@Table(name = "COMMENT_PROPERTY")
public class CommentPropertyAnswer {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @Column(name = "action_property_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionPropertyType propertyType;

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

    public final ActionPropertyType getPropertyType() {
        return propertyType;
    }

    public final void setPropertyType(ActionPropertyType propertyType) {
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

    public CommentPropertyAnswer withPropertyType(ActionPropertyType propertyType) {
        this.propertyType = propertyType;
        return this;
    }

    public CommentPropertyAnswer withPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
        return this;
    }

    public CommentPropertyAnswer withPropertyValue(String value) {
        this.propertyValue = value;
        return this;
    }

    public CommentPropertyAnswer withPropertyWeight(BigDecimal propertyWeight) {
        this.propertyWeight = propertyWeight;
        return this;
    }

}
