package com.zuehlke.pgadmissions.domain.comment;

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
public class CommentProperty {

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
    @Column(name = "property_value", nullable = false)
    private String propertyValue;

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

    public final String getPropertyValue() {
        return propertyValue;
    }

    public final void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public CommentProperty withComment(Comment comment) {
        this.comment = comment;
        return this;
    }

    public CommentProperty withPropertyType(ActionPropertyType propertyType) {
        this.propertyType = propertyType;
        return this;
    }

    public CommentProperty withPropertyValue(String value) {
        this.propertyValue = value;
        return this;
    }

}
