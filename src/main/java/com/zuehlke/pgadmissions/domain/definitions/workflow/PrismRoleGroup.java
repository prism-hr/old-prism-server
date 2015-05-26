package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADVERTISER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_SPONSOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_SPONSOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_SPONSOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;

public enum PrismRoleGroup {

    APPLICATION_PARENT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR), //
    APPLICATION_PARENT_APPROVER_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, PROJECT_ADMINISTRATOR), //
    APPLICATION_PARENT_SPONSOR_GROUP(INSTITUTION_SPONSOR, PROGRAM_SPONSOR, PROJECT_SPONSOR), //
    APPLICATION_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR, APPLICATION_ADMINISTRATOR), //
    APPLICATION_APPROVER_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER, PROJECT_ADMINISTRATOR, APPLICATION_ADMINISTRATOR), //
    APPLICATION_PARENT_VIEWER_GROUP(INSTITUTION_ADMINISTRATOR, INSTITUTION_ADMITTER, INSTITUTION_SPONSOR, PROGRAM_ADMINISTRATOR, PROGRAM_APPROVER,
            PROGRAM_VIEWER, PROGRAM_SPONSOR, PROJECT_ADMINISTRATOR, PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR, PROJECT_SPONSOR), //
    APPLICATION_CONFIRMED_SUPERVISOR_GROUP(PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR, APPLICATION_PRIMARY_SUPERVISOR,
            APPLICATION_SECONDARY_SUPERVISOR), //
    APPLICATION_POTENTIAL_SUPERVISOR_GROUP(PROGRAM_APPROVER, PROGRAM_VIEWER, PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR,
            APPLICATION_ADMINISTRATOR, APPLICATION_REVIEWER, APPLICATION_INTERVIEWER, APPLICATION_PRIMARY_SUPERVISOR, APPLICATION_SECONDARY_SUPERVISOR,
            APPLICATION_VIEWER_RECRUITER), //
    APPLICATION_POTENTIAL_INTERVIEW_GROUP(APPLICATION_POTENTIAL_INTERVIEWER, APPLICATION_POTENTIAL_INTERVIEWEE), //
    APPLICATION_CONFIRMED_INTERVIEW_GROUP(APPLICATION_INTERVIEWER, APPLICATION_INTERVIEWEE), //
    PROJECT_SUPERVISOR_GROUP(PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR), //
    PROJECT_PARENT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR), //
    PROJECT_ADMINISTRATOR_GROUP(INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR, PROJECT_ADMINISTRATOR), //
    PROGRAM_PARENT_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR), //
    PROGRAM_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR, PROGRAM_ADMINISTRATOR), //
    INSTITUTION_ADMINISTRATOR_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR), //
    INSTIUTTION_ADVERTISER_GROUP(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR, INSTITUTION_ADVERTISER);

    private PrismRole[] roles;

    private PrismRoleGroup(PrismRole... roles) {
        this.roles = roles;
    }

    public PrismRole[] getRoles() {
        return roles;
    }

}
