package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="CONFIRM_ELIGIBILITY_EVENT")
public class ConfirmEligibilityEvent extends Event {

    private static final long serialVersionUID = -6625096367302462157L;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admitter_comment_id")
    private AdmitterComment comment;
    
    public Comment getComment() {
        return comment;
    }
    
    public void setComment(AdmitterComment comment) {
        this.comment = comment;
    }

}
