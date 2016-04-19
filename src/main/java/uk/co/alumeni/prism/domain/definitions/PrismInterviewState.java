package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_SCHEDULED_INTERVIEWER;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;

public enum PrismInterviewState {

    TAKEN_PLACE(APPLICATION_INTERVIEWER),
    SCHEDULED(APPLICATION_SCHEDULED_INTERVIEWER),
    TO_BE_SCHEDULED(APPLICATION_POTENTIAL_INTERVIEWER);

    private PrismRole interviewerRole;

    private PrismInterviewState(PrismRole interviewerRole) {
        this.interviewerRole = interviewerRole;
    }

    public PrismRole getInterviewerRole() {
        return interviewerRole;
    }

}
