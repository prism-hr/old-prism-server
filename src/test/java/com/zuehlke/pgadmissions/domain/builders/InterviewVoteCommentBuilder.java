package com.zuehlke.pgadmissions.domain.builders;

import java.util.Date;

import com.zuehlke.pgadmissions.domain.InterviewParticipant;
import com.zuehlke.pgadmissions.domain.InterviewVoteComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewVoteCommentBuilder {

    private Integer id;
    private RegisteredUser user;
    private Date createdTimeStamp;
    private InterviewParticipant interviewParticipant;

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
        interviewVoteComment.setDate(createdTimeStamp);
        interviewVoteComment.setInterviewParticipant(interviewParticipant);
        return interviewVoteComment;
    }


	public InterviewVoteCommentBuilder interviewParticipant(InterviewParticipant interviewParticipant) {
		this.interviewParticipant = interviewParticipant;
		return this;
	}

}
