package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.IndexColumn;

import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "COMMENT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Comment implements Comparable<Comment>, Serializable {

    private static final long serialVersionUID = 2861325991249900547L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "created_timestamp", insertable = false)
    @Generated(GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private List<Document> documents = new ArrayList<Document>();

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 2000)
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    @IndexColumn(name = "score_position")
    private List<Score> scores = new ArrayList<Score>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegisteredUser user = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id")
    private ApplicationForm application = null;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date createdTimestamp) {
        this.date = createdTimestamp;
    }

    public CommentType getType() {
        return CommentType.GENERIC;
    }

    @Override
    public int compareTo(Comment otherComment) {
        int dateComparison = otherComment.getDate().compareTo(this.date);
        if (dateComparison != 0) {
            return dateComparison;
        }
        return otherComment.getId().compareTo(id);
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}
