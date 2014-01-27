package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "STATECHANGE_COMMENT")
public class StateChangeComment extends Comment {

    private static final long serialVersionUID = 7106729861627717600L;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_status")
    private ApplicationFormStatus nextStatus = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_administrator_id")
    private RegisteredUser delegateAdministrator;
    
    @Column(name = "use_custom_questions")
    private Boolean useCustomQuestions;
    
    @Column(name = "use_custom__reference_questions")
    private Boolean useCustomReferenceQuestions;
    
    @Transient
    private Boolean fastTrackApplication = null;

    public CommentType getType() {
        return type;
    }

    public void setType(CommentType type) {
        this.type = type;
    }

    public ApplicationFormStatus getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(ApplicationFormStatus nextStatus) {
        this.nextStatus = nextStatus;
    }

    public Boolean getFastTrackApplication() {
        return fastTrackApplication;
    }

    public void setFastTrackApplication(Boolean fastTrackApplication) {
        this.fastTrackApplication = fastTrackApplication;
    }

    public RegisteredUser getDelegateAdministrator() {
        return delegateAdministrator;
    }

    public void setDelegateAdministrator(RegisteredUser delegateAdministrator) {
        this.delegateAdministrator = delegateAdministrator;
    }

    public Boolean getUseCustomQuestions() {
        return useCustomQuestions;
    }

    public void setUseCustomQuestions(Boolean useCustomQuestions) {
        this.useCustomQuestions = useCustomQuestions;
    }

    public Boolean getUseCustomReferenceQuestions() {
        return useCustomReferenceQuestions;
    }

    public void setUseCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
    }

}