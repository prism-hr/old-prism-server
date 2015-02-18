package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_SUGGESTED_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ROLE_VIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public enum PrismRole {

    APPLICATION_ADMINISTRATOR(false, APPLICATION, SYSTEM_ROLE_ADMINISTRATOR), //
    APPLICATION_CREATOR(true, APPLICATION, SYSTEM_ROLE_CREATOR), //
    APPLICATION_INTERVIEWEE(false, APPLICATION, SYSTEM_ROLE_INTERVIEWEE), //
    APPLICATION_INTERVIEWER(false, APPLICATION, SYSTEM_ROLE_INTERVIEWER), //
    APPLICATION_POTENTIAL_INTERVIEWEE(false, APPLICATION, SYSTEM_ROLE_INTERVIEWEE), //
    APPLICATION_POTENTIAL_INTERVIEWER(false, APPLICATION, SYSTEM_ROLE_INTERVIEWER), //
    APPLICATION_PRIMARY_SUPERVISOR(false, APPLICATION, SYSTEM_ROLE_PRIMARY_SUPERVISOR), //
    APPLICATION_REFEREE(false, APPLICATION, SYSTEM_ROLE_REFEREE), //
    APPLICATION_REVIEWER(false, APPLICATION, SYSTEM_ROLE_REVIEWER), //
    APPLICATION_SECONDARY_SUPERVISOR(false, APPLICATION, SYSTEM_ROLE_SECONDARY_SUPERVISOR), //
    APPLICATION_SUGGESTED_SUPERVISOR(false, APPLICATION, SYSTEM_ROLE_SUGGESTED_SUPERVISOR), //
    APPLICATION_VIEWER_RECRUITER(false, APPLICATION, SYSTEM_ROLE_VIEWER), //
    APPLICATION_VIEWER_REFEREE(false, APPLICATION, SYSTEM_ROLE_REFEREE), //
    INSTITUTION_ADMINISTRATOR(true, INSTITUTION, SYSTEM_ROLE_ADMINISTRATOR), //
    INSTITUTION_ADMITTER(false, INSTITUTION, SYSTEM_ROLE_ADMITTER), //
    PROGRAM_ADMINISTRATOR(true, PROGRAM, SYSTEM_ROLE_ADMINISTRATOR), //
    PROGRAM_APPROVER(false, PROGRAM, SYSTEM_ROLE_APPROVER), //
    PROGRAM_VIEWER(false, PROGRAM, SYSTEM_ROLE_VIEWER), //
    PROJECT_ADMINISTRATOR(false, PROJECT, SYSTEM_ROLE_ADMINISTRATOR), //
    PROJECT_PRIMARY_SUPERVISOR(true, PROJECT, SYSTEM_ROLE_PRIMARY_SUPERVISOR), //
    PROJECT_SECONDARY_SUPERVISOR(false, PROJECT, SYSTEM_ROLE_SECONDARY_SUPERVISOR), //
    SYSTEM_ADMINISTRATOR(true, SYSTEM, SYSTEM_ROLE_ADMINISTRATOR);

    private boolean scopeOwner;

    private PrismScope scope;

    private PrismDisplayPropertyDefinition displayPropertyDefinition;

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

    private PrismRole(boolean scopeOwner, PrismScope scope, PrismDisplayPropertyDefinition displayPropertyDefinition) {
        this.scopeOwner = scopeOwner;
        this.scope = scope;
        this.displayPropertyDefinition = displayPropertyDefinition;
    }

    public boolean isScopeOwner() {
        return scopeOwner;
    }

    public PrismScope getScope() {
        return scope;
    }

    public final PrismDisplayPropertyDefinition getDisplayPropertyDefinition() {
        return displayPropertyDefinition;
    }

    public static Set<PrismRole> getExcludedRoles(PrismRole role) {
        return excludedRoles.get(role) == null ? new HashSet<PrismRole>() : excludedRoles.get(role);
    }

    public static Set<PrismRole> getScopeOwners(PrismScope scope) {
        return scopeOwners.get(scope);
    }

}
