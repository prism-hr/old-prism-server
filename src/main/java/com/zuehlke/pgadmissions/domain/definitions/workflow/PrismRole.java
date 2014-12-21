package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.collect.HashMultimap;

import java.util.HashSet;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismRole {

    APPLICATION_ADMINISTRATOR(false, APPLICATION), //
    APPLICATION_CREATOR(true, APPLICATION), //
    APPLICATION_INTERVIEWEE(false, APPLICATION), //
    APPLICATION_INTERVIEWER(false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(false, APPLICATION), //
    APPLICATION_PRIMARY_SUPERVISOR(false, APPLICATION), //
    APPLICATION_REFEREE(false, APPLICATION), //
    APPLICATION_REVIEWER(false, APPLICATION), //
    APPLICATION_SECONDARY_SUPERVISOR(false, APPLICATION), //
    APPLICATION_SUGGESTED_SUPERVISOR(false, APPLICATION), //
    APPLICATION_VIEWER_RECRUITER(false, APPLICATION), //
    APPLICATION_VIEWER_REFEREE(false, APPLICATION), //
    INSTITUTION_ADMINISTRATOR(true, INSTITUTION), //
    INSTITUTION_ADMITTER(false, INSTITUTION), //
    PROGRAM_ADMINISTRATOR(true, PROGRAM), //
    PROGRAM_APPROVER(false, PROGRAM), //
    PROGRAM_VIEWER(false, PROGRAM), //
    PROJECT_ADMINISTRATOR(false, PROJECT), //
    PROJECT_PRIMARY_SUPERVISOR(true, PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(false, PROJECT), //
    SYSTEM_ADMINISTRATOR(true, SYSTEM);

    private boolean scopeOwner;

    private PrismScope scope;

    private static final HashMultimap<PrismRole, PrismRole> excludedRoles = HashMultimap.create();

    private static HashMultimap<PrismScope, PrismRole> scopeOwners = HashMultimap.create();

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

        for (PrismRole role : PrismRole.values()) {
            if (role.isScopeOwner()) {
                scopeOwners.put(role.getScope(), role);
            }
        }
    }

    private PrismRole(boolean scopeOwner, PrismScope scope) {
        this.scopeOwner = scopeOwner;
        this.scope = scope;
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
