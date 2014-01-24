package com.zuehlke.pgadmissions.dto;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.Comment;

public class TimelineConfirmEligibility extends TimelineObject {
    
    private Comment comment;
    
    public void setComment(Comment comment) {
        this.comment = comment;
    }
    
    public Comment getComment() {
        return comment;
    }

    @Override
    public String getMessageCode() {
        return "timeline.eligibility.confirmed";
    }

    @Override
    public String getType() {
        return "confirmEligibility";
    }

    @Override
    public Date getMostRecentActivityDate() {
        return eventDate;
    }

    @Override
    public String getUserCapacity() {
        return "admitter";
    }

}
