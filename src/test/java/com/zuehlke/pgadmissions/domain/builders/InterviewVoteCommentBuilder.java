package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewVoteCommentBuilder {

    private Integer id;
    private RegisteredUser user;
    private Date createdTimeStamp;

    public InterviewVoteCommentBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public InterviewVoteCommentBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public InterviewVoteCommentBuilder date(Date createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
        return this;
    }

    public InterviewVoteComment build() {
        InterviewVoteComment interviewVoteComment = new InterviewVoteComment();
        interviewVoteComment.setId(id);
        interviewVoteComment.setUser(user);
        interviewVoteComment.setCreatedTimestamp(createdTimeStamp);
        return interviewVoteComment;
    }



}
