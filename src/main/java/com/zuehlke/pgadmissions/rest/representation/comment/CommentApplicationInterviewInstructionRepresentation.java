package com.zuehlke.pgadmissions.rest.representation.comment;

public class CommentApplicationInterviewInstructionRepresentation {

    private String intervieweeInstructions;

    private String interviewerInstructions;

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
    
    public CommentApplicationInterviewInstructionRepresentation withIntervieweeInstructions(String intervieweeInstructions) {
        this.intervieweeInstructions = intervieweeInstructions;
        return this;
    }
    
    public CommentApplicationInterviewInstructionRepresentation withInterviewLocation(String interviewLocation) {
        this.interviewLocation = interviewLocation;
        return this;
    }

}
