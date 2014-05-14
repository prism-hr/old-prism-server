package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity
@Table(name = "APPLICATION_TRANSFER_COMMENT")
public class ApplicationTransferComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    @Column(name = "succeeded")
    private Boolean transferSucceeded;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_transfer_error_id")
    private ApplicationTransferError applicationFormTransferError = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", insertable = false, updatable = false)
    private CommentType type = CommentType.APPLICATION_TRANSFER_COMMENT;

    public ApplicationTransferComment() {
        super();
    }

    public ApplicationTransferComment(ApplicationForm application, User user) {
        super();
        setUser(user);
        setApplication(application);
        setTransferSucceeded(true);
    }

    public ApplicationTransferComment(ApplicationForm application, User user, ApplicationTransferError applicationFormTransferError) {
        super();
        setUser(user);
        setApplication(application);
        setTransferSucceeded(false);
        setApplicationFormTransferError(applicationFormTransferError);
    }

    public Boolean getTransferSucceeded() {
        return transferSucceeded;
    }

    public void setTransferSucceeded(Boolean transferSucceeded) {
        this.transferSucceeded = transferSucceeded;
    }

    public ApplicationTransferError getApplicationFormTransferError() {
        return applicationFormTransferError;
    }

    public void setApplicationFormTransferError(ApplicationTransferError applicationFormTransferError) {
        this.applicationFormTransferError = applicationFormTransferError;
    }

    public CommentType getType() {
        return type;
    }

}
