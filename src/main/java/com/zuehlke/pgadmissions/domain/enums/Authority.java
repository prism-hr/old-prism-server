package com.zuehlke.pgadmissions.domain.enums;

public enum Authority {

    /* This is an administrator of a particular programme */
    ADMINISTRATOR,

    /* This is a person belonging to the admissions team of UCL */
    ADMITTER,

    /* An applicant applying for a programme */
    APPLICANT,

    /* A person which approves an application */
    APPROVER,

    /* A person which interviews applicants */
    INTERVIEWER,

    /* A person which provides a reference for an applicant */
    REFEREE,

    /* A person which reviews an existing application */
    REVIEWER,

    /* Superuser which can execute every possible action within the system */
    SUPERADMINISTRATOR,

    /* A person which considers providing supervision to an applicant */
    SUPERVISOR,

    /* Enables the holder to view all applications within the programme */
    VIEWER,

    /* A user that has delegated administrative privileges for a given project */
    PROJECTADMINISTRATOR,
    
    /* A user that has delegated administrative privileges for a given stage */
    STATEADMINISTRATOR,
    
    /* A user that an applicant suggests as their supervisor */
    SUGGESTEDSUPERVISOR,
    
    /* Allows a user with no active role to continue to log in */
    SAFETYNET
}