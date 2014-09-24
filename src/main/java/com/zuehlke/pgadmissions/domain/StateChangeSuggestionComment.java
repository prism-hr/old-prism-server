package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Entity(name = "STATE_CHANGE_SUGGESTION_COMMENT")
public class StateChangeSuggestionComment extends Comment {

    private static final long serialVersionUID = 7106729861627717600L;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type")
    private CommentType type = CommentType.STATE_CHANGE_SUGGESTION;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_status")
    private ApplicationFormStatus nextStatus = null;

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
}