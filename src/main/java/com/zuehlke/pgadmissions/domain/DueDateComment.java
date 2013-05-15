package com.zuehlke.pgadmissions.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "DUE_DATE_COMMENT")
public class DueDateComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}
