package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;


public enum PrismRole {

    APPLICATION_ADMINISTRATOR(PrismScope.APPLICATION, false), //
    APPLICATION_CREATOR(PrismScope.APPLICATION, true), //
    APPLICATION_INTERVIEWEE(PrismScope.APPLICATION, false), //
    APPLICATION_INTERVIEWER(PrismScope.APPLICATION, false), //
    APPLICATION_POTENTIAL_INTERVIEWEE(PrismScope.APPLICATION, false), //
    APPLICATION_POTENTIAL_INTERVIEWER(PrismScope.APPLICATION, false), //
    APPLICATION_PRIMARY_SUPERVISOR(PrismScope.APPLICATION, false), //
    APPLICATION_REFEREE(PrismScope.APPLICATION, false), //
    APPLICATION_REVIEWER(PrismScope.APPLICATION, false), //
    APPLICATION_SECONDARY_SUPERVISOR(PrismScope.APPLICATION, false), //
    APPLICATION_SUGGESTED_SUPERVISOR(PrismScope.APPLICATION, false), //
    APPLICATION_VIEWER_RECRUITER(PrismScope.APPLICATION, false), //
    APPLICATION_VIEWER_REFEREE(PrismScope.APPLICATION, false), //
    INSTITUTION_ADMINISTRATOR(PrismScope.INSTITUTION, true), //
    INSTITUTION_ADMITTER(PrismScope.INSTITUTION, false), //
    PROGRAM_ADMINISTRATOR(PrismScope.PROGRAM, true), //
    PROGRAM_APPROVER(PrismScope.PROGRAM, false), //
    PROGRAM_VIEWER(PrismScope.PROGRAM, false), //
    PROJECT_ADMINISTRATOR(PrismScope.PROJECT, false), //
    PROJECT_PRIMARY_SUPERVISOR(PrismScope.PROJECT, true), //
    PROJECT_SECONDARY_SUPERVISOR(PrismScope.PROJECT, false), //
    SYSTEM_ADMINISTRATOR(PrismScope.SYSTEM, true);
    
    private Boolean scopeOwner;
    
    private PrismScope scope;
    
    private static final HashMultimap<PrismRole, PrismRole> excludedRoles = HashMultimap.create();

    static {
        excludedRoles.put(PrismRole.APPLICATION_ADMINISTRATOR, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_INTERVIEWER, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_PRIMARY_SUPERVISOR, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_REFEREE, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_REVIEWER, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_SECONDARY_SUPERVISOR, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR, PrismRole.APPLICATION_CREATOR);
        excludedRoles.put(PrismRole.PROJECT_PRIMARY_SUPERVISOR, PrismRole.PROJECT_SECONDARY_SUPERVISOR);
        excludedRoles.put(PrismRole.PROJECT_SECONDARY_SUPERVISOR, PrismRole.PROJECT_PRIMARY_SUPERVISOR);
    }
    
    private static HashMultimap<PrismScope, PrismRole> scopeOwners = HashMultimap.create();
    
    static {
        for (PrismRole role : PrismRole.values()) {
            if (role.isScopeOwner()) {
                scopeOwners.put(role.getScope(), role);
            }
        }
    }
    
    private PrismRole(PrismScope scope, boolean scopeOwner) {
        this.scope = scope;
        this.scopeOwner = scopeOwner;
    }

    public boolean isScopeOwner() {
        return scopeOwner;
    }

    public PrismScope getScope() {
        return scope;
    }
    
    public static Set<PrismRole> getExcludedRoles(PrismRole role) {
        return excludedRoles.get(role) == null ? new HashSet<PrismRole>() : excludedRoles.get(role);
    }
    
    public static Set<PrismRole> getScopeOwners(PrismScope scope) {
        return scopeOwners.get(scope);
    }

}
