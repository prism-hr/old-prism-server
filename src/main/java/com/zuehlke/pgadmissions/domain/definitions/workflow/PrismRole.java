package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Set;

import com.google.common.collect.HashMultimap;


public enum PrismRole {

    APPLICATION_ADMINISTRATOR(PrismScope.APPLICATION), //
    APPLICATION_CREATOR(PrismScope.APPLICATION), //
    APPLICATION_INTERVIEWEE(PrismScope.APPLICATION), //
    APPLICATION_INTERVIEWER(PrismScope.APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(PrismScope.APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(PrismScope.APPLICATION), //
    APPLICATION_PRIMARY_SUPERVISOR(PrismScope.APPLICATION), //
    APPLICATION_REFEREE(PrismScope.APPLICATION), //
    APPLICATION_REVIEWER(PrismScope.APPLICATION), //
    APPLICATION_SECONDARY_SUPERVISOR(PrismScope.APPLICATION), //
    APPLICATION_SUGGESTED_SUPERVISOR(PrismScope.APPLICATION), //
    APPLICATION_VIEWER_RECRUITER(PrismScope.APPLICATION), //
    APPLICATION_VIEWER_REFEREE(PrismScope.APPLICATION), //
    INSTITUTION_ADMINISTRATOR(PrismScope.INSTITUTION), //
    INSTITUTION_ADMITTER(PrismScope.INSTITUTION), //
    PROGRAM_ADMINISTRATOR(PrismScope.PROGRAM), //
    PROGRAM_APPROVER(PrismScope.PROGRAM), //
    PROGRAM_VIEWER(PrismScope.PROGRAM), //
    PROJECT_ADMINISTRATOR(PrismScope.PROJECT), //
    PROJECT_PRIMARY_SUPERVISOR(PrismScope.PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(PrismScope.PROJECT), //
    SYSTEM_ADMINISTRATOR(PrismScope.SYSTEM);
    
    private PrismScope scope;
    
    private static final HashMultimap<PrismRole, PrismRole> exclusions = HashMultimap.create();

    static {
        exclusions.put(PrismRole.APPLICATION_ADMINISTRATOR, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.INSTITUTION_ADMINISTRATOR);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.INSTITUTION_ADMITTER);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.PROGRAM_ADMINISTRATOR);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.PROGRAM_APPROVER);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.PROGRAM_VIEWER);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.PROJECT_ADMINISTRATOR);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.PROJECT_PRIMARY_SUPERVISOR);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR);
        exclusions.put(PrismRole.APPLICATION_CREATOR, PrismRole.SYSTEM_ADMINISTRATOR);
        exclusions.put(PrismRole.APPLICATION_INTERVIEWER, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_PRIMARY_SUPERVISOR, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_REFEREE, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_REVIEWER, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_SECONDARY_SUPERVISOR, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR, PrismRole.APPLICATION_CREATOR);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.INSTITUTION_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.INSTITUTION_ADMITTER);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROGRAM_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROGRAM_APPROVER);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROGRAM_VIEWER);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_PRIMARY_SUPERVISOR);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR);
        exclusions.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.SYSTEM_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.INSTITUTION_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.INSTITUTION_ADMITTER);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROGRAM_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROGRAM_APPROVER);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROGRAM_VIEWER);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROJECT_ADMINISTRATOR);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROJECT_PRIMARY_SUPERVISOR);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR);
        exclusions.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.SYSTEM_ADMINISTRATOR);
    }
    
    private PrismRole(PrismScope scope) {
        this.scope = scope;
    }

    public PrismScope getScope() {
        return scope;
    }
    
    public Set<PrismRole> getExclusions(PrismRole role) {
        return exclusions.get(role);
    }

}
