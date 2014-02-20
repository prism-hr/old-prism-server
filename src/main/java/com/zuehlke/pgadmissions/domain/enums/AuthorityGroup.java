package com.zuehlke.pgadmissions.domain.enums;

import org.apache.commons.lang.ArrayUtils;

public enum AuthorityGroup {

    INTERNAL_SYSTEM_AUTHORITIES(new Authority[] { Authority.SUPERADMINISTRATOR, Authority.ADMITTER }), 
    EXTERNAL_SYSTEM_AUTHORITIES(new Authority[] {}),

    INTERNAL_PROGRAM_AUTHORITIES(new Authority[] { Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER }), 
    EXTERNAL_PROGRAM_AUTHORITIES(new Authority[] {}),

    INTERNAL_PROJECT_AUTHORITIES(new Authority[] { Authority.PROJECTADMINISTRATOR }), 
    EXTERNAL_PROJECT_AUTHORITIES(new Authority[] {}),

    INTERNAL_APPLICATION_AUTHORITIES(new Authority[] { Authority.INTERVIEWER, Authority.REVIEWER, Authority.SUPERVISOR }), 
    EXTERNAL_APPLICATION_AUTHORITIES(new Authority[] { Authority.APPLICANT, Authority.REFEREE, Authority.SUGGESTEDSUPERVISOR}),

    INTERNAL_STATE_AUTHORITIES(new Authority[] { Authority.STATEADMINISTRATOR }), 
    EXTERNAL_STATE_AUTHORITIES(new Authority[] {});

    private Authority[] authorities;

    private AuthorityGroup(Authority[] authorities) {
        this.authorities = authorities;
    }

    public Authority[] getAuthorities() {
        return authorities;
    }

    public static Authority[] getAuthoritiesForAuthorityGroups(AuthorityGroup... authorityGroups) {
        Authority[] authorities = new Authority[] {};

        for (AuthorityGroup authorityGroup : authorityGroups) {
            ArrayUtils.addAll(authorities, authorityGroup.getAuthorities());
        }

        return authorities;
    }

    public static Authority[] getAllSystemAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_SYSTEM_AUTHORITIES, EXTERNAL_SYSTEM_AUTHORITIES);
    }

    public static Authority[] getAllProgramAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_PROGRAM_AUTHORITIES, EXTERNAL_PROGRAM_AUTHORITIES);
    }

    public static Authority[] getAllProjectAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_PROJECT_AUTHORITIES, EXTERNAL_PROJECT_AUTHORITIES);
    }

    public static Authority[] getAllApplicationAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_APPLICATION_AUTHORITIES, EXTERNAL_APPLICATION_AUTHORITIES);
    }

    public static Authority[] getAllStateAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_STATE_AUTHORITIES, EXTERNAL_STATE_AUTHORITIES);
    }

    public static Authority[] getAllInternalRecruiterAuthorities() {
        Authority[] inclusions = getAllProgramAuthorities();
        Authority[] exclusions = new Authority[] { Authority.ADMINISTRATOR, Authority.APPLICANT, Authority.REFEREE };
        ArrayUtils.addAll(inclusions, getAllProjectAuthorities());
        ArrayUtils.addAll(inclusions, getAllApplicationAuthorities());
        ArrayUtils.addAll(inclusions, getAllStateAuthorities());
        for (Authority exclusion : exclusions) {
            ArrayUtils.removeElement(inclusions, exclusion);
        } 
        return inclusions;
    }

}
