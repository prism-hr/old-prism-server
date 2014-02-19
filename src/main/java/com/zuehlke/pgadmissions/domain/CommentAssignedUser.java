package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "COMMENT_ASSIGNED_REGISTERED_USER")
public class CommentAssignedUser {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegisteredUser user;

    @Column(name = "is_primary")
    private boolean primary;

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

}
