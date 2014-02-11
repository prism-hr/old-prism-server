package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "OPPORTUNITY_REQUEST_COMMENT")
public class OpportunityRequestComment {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private RegisteredUser author;

    @Column(name = "comment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private OpportunityRequestCommentType commentType;

    @Column(name = "content")
    @Lob
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 10)
    private String content;

    @Column(name = "created_timestamp", insertable = false, nullable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RegisteredUser getAuthor() {
        return author;
    }

    public void setAuthor(RegisteredUser author) {
        this.author = author;
    }

    public OpportunityRequestCommentType getCommentType() {
        return commentType;
    }

    public void setCommentType(OpportunityRequestCommentType commentType) {
        this.commentType = commentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

}
