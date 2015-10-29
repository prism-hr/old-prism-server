package com.zuehlke.pgadmissions.rest.dto.comment;

import javax.validation.constraints.Size;

public class CommentInterviewInstructionDTO {

    @Size(max = 1000)
    private String intervieweeInstructions;

    @Size(max = 1000)
    private String interviewerInstructions;

    @Size(max = 2000)
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

}
