package com.zuehlke.pgadmissions.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AuthorityGroup {

    INTERNAL_SYSTEM_AUTHORITIES(Arrays.asList(Authority.SUPERADMINISTRATOR, Authority.ADMITTER)), 
    EXTERNAL_SYSTEM_AUTHORITIES(new ArrayList<Authority>(0)),

    INTERNAL_PROGRAM_AUTHORITIES(Arrays.asList(Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER)),
    EXTERNAL_PROGRAM_AUTHORITIES(new ArrayList<Authority>(0)),

    INTERNAL_PROJECT_AUTHORITIES(Arrays.asList(Authority.PROJECTADMINISTRATOR)),
    EXTERNAL_PROJECT_AUTHORITIES(new ArrayList<Authority>(0)),

    INTERNAL_APPLICATION_AUTHORITIES(Arrays.asList(Authority.INTERVIEWER, Authority.REVIEWER, Authority.SUPERVISOR)),
    EXTERNAL_APPLICATION_AUTHORITIES(new ArrayList<Authority>(0)),

    INTERNAL_STATE_AUTHORITIES(Arrays.asList(Authority.STATEADMINISTRATOR)), 
    EXTERNAL_STATE_AUTHORITIES(new ArrayList<Authority>(0));

    private List<Authority> authorities;

    private AuthorityGroup(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public static List<Authority> getAuthoritiesForAuthorityGroups(AuthorityGroup... authorityGroups) {
        List<Authority> authorities = new ArrayList<Authority>();

        for (AuthorityGroup authorityGroup : authorityGroups) {
            authorities.addAll(authorityGroup.getAuthorities());
        }

        return authorities;
    }

    public static List<Authority> getAllSystemAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_SYSTEM_AUTHORITIES, EXTERNAL_SYSTEM_AUTHORITIES);
    }

    public static List<Authority> getAllProgramAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_PROGRAM_AUTHORITIES, EXTERNAL_PROGRAM_AUTHORITIES);
    }

    public static List<Authority> getAllProjectAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_PROJECT_AUTHORITIES, EXTERNAL_PROJECT_AUTHORITIES);
    }

    public static List<Authority> getAllApplicationAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_APPLICATION_AUTHORITIES, EXTERNAL_APPLICATION_AUTHORITIES);
    }

    public static List<Authority> getAllStateAuthorities() {
        return getAuthoritiesForAuthorityGroups(INTERNAL_STATE_AUTHORITIES, EXTERNAL_STATE_AUTHORITIES);
    }

    public static List<Authority> getAllInternalRecruiterAuthorities() {
        List<Authority> inclusions = getAllProgramAuthorities();
        List<Authority> exclusions = Arrays.asList(Authority.ADMINISTRATOR, Authority.APPLICANT, Authority.REFEREE);
        inclusions.addAll(getAllProjectAuthorities());
        inclusions.addAll(getAllApplicationAuthorities());
        inclusions.addAll(getAllStateAuthorities());
        for (Authority exclusion : exclusions) {
            inclusions.remove(exclusion);
        } 
        return inclusions;
    }

}
