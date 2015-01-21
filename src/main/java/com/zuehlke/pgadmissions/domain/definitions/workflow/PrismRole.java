package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;

public enum PrismRole {

    APPLICATION_ADMINISTRATOR(false, true, APPLICATION), //
    APPLICATION_CREATOR(true, false, APPLICATION), //
    APPLICATION_INTERVIEWEE(false, false, APPLICATION), //
    APPLICATION_INTERVIEWER(false, true, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWEE(false, false, APPLICATION), //
    APPLICATION_POTENTIAL_INTERVIEWER(false, true, APPLICATION), //
    APPLICATION_PRIMARY_SUPERVISOR(false, true, APPLICATION), //
    APPLICATION_REFEREE(false, false, APPLICATION), //
    APPLICATION_REVIEWER(false, true, APPLICATION), //
    APPLICATION_SECONDARY_SUPERVISOR(false, true, APPLICATION), //
    APPLICATION_SUGGESTED_SUPERVISOR(false, false, APPLICATION), //
    APPLICATION_VIEWER_RECRUITER(false, true, APPLICATION), //
    APPLICATION_VIEWER_REFEREE(false, false, APPLICATION), //
    INSTITUTION_ADMINISTRATOR(true, true, INSTITUTION), //
    INSTITUTION_ADMITTER(false, false, INSTITUTION), //
    PROGRAM_ADMINISTRATOR(true, true, PROGRAM), //
    PROGRAM_APPROVER(false, true, PROGRAM), //
    PROGRAM_VIEWER(false, false, PROGRAM), //
    PROJECT_ADMINISTRATOR(false, true, PROJECT), //
    PROJECT_PRIMARY_SUPERVISOR(true, true, PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(false, true, PROJECT), //
    SYSTEM_ADMINISTRATOR(true, true, SYSTEM);

    private boolean scopeOwner;
    
    private boolean overrideRedaction;
    
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

    private PrismRole(boolean scopeOwner, boolean overrideRedaction, PrismScope scope) {
        this.scopeOwner = scopeOwner;
        this.overrideRedaction = overrideRedaction;
        this.scope = scope;
    }

    public boolean isScopeOwner() {
        return scopeOwner;
    }

    public final boolean isOverrideRedaction() {
        return overrideRedaction;
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
