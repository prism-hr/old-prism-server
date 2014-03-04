package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class InterviewerBuilder {
    private Integer id;
    private RegisteredUser user;
    private Interview interview;
    private InterviewComment interviewComment;

    public InterviewerBuilder interviewComment(InterviewComment interviewComment) {
        this.interviewComment = interviewComment;
        return this;
    }

    public InterviewerBuilder interview(Interview interview) {
        this.interview = interview;
        return this;
    }

    public InterviewerBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public InterviewerBuilder user(RegisteredUser user) {
        this.user = user;
        return this;
    }

    public Interviewer build() {
        Interviewer interviewer = new Interviewer();
        interviewer.setId(id);
        interviewer.setUser(user);
        interviewer.setInterview(interview);
        interviewer.setInterviewComment(interviewComment);
        return interviewer;
    }
}
