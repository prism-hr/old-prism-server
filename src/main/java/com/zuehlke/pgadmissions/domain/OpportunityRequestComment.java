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
import javax.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestCommentType;

@Entity(name = "OPPORTUNITY_REQUEST_COMMENT")
public class OpportunityRequestComment {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private RegisteredUser author;

    @Column(name = "comment_type")
    @Enumerated(EnumType.STRING)
    private OpportunityRequestCommentType type;

    @Column(name = "content")
    @Size(max = 50000, message = "A maximum of 50000 characters are allowed.")
    @Lob
    private String content;

    @Column(name = "created_timestamp", insertable = false)
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

    public OpportunityRequestCommentType getType() {
        return type;
    }

    public void setType(OpportunityRequestCommentType type) {
        this.type = type;
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
