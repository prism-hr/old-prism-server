package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CommentApplicationInterviewInstruction {

	@Lob
    @Column(name = "application_interviewee_instructions")
    private String intervieweeInstructions;

	@Lob
    @Column(name = "application_interviewer_instructions")
    private String interviewerInstructions;

    @Column(name = "application_interview_location")
    private String interviewLocation;

    public final String getIntervieweeInstructions() {
        return intervieweeInstructions;
    }

    public final void setIntervieweeInstructions(String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
    }

    public final String getInterviewerInstructions() {
        return interviewerInstructions;
    }

    public final void setInterviewerInstructions(String interviewerInstructions) {
        this.interviewerInstructions = interviewerInstructions;
    }

    public final String getInterviewLocation() {
        return interviewLocation;
    }

    public final void setInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
    }

    public CommentApplicationInterviewInstruction withIntervieweeInstructions(String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
        return this;
    }

    public CommentApplicationInterviewInstruction withInterviewerInstructions(String interviewerInstructions) {
        this.interviewerInstructions = interviewerInstructions;
        return this;
    }

    public CommentApplicationInterviewInstruction withInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
        return this;
    }

}
