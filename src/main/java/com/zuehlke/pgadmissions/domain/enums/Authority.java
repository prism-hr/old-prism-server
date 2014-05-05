package com.zuehlke.pgadmissions.domain.enums;

public enum Authority {

    /* This is an administrator of a particular programme */
    PROGRAM_ADMINISTRATOR,

    /* This is a person belonging to the admissions team of UCL */
    INSTITUTION_ADMITTER,

    /* An applicant applying for a programme */
    APPLICATION_CREATOR,

    /* A person which approves an application and decides whether the application will be APPROVED or REJECTED */
    PROGRAM_APPROVER,

    /* A person which interviews applicants */
    APPLICATION_INTERVIEWER,

    /* A person which knows an applicant and provides feedback about him to the UCL Staff */
    APPLICATION_REFEREE,

    /* A person which reviews an existing application before moving it to the APPROVAL stage */
    APPLICATION_REVIEWER,

    /* Prism Superuser which can execute every possible action within the system */
    SYSTEM_ADMINISTRATOR,

    /* A person which makes a commitment towards the applicant to supervise him after his application has been APPROVED */
    APPLICATION_PRIMARY_SUPERVISOR,

    APPLICATION_SECONDARY_SUPERVISOR,

    /* Enables the holder to view all applications within the programme */
    PROGRAM_VIEWER,

    /* A user that has delegated administrative privileges for one application */
    PROJECTADMINISTRATOR, 
    
    /* A user that has delegated administrative privileges for a given stage */
    APPLICATION_ADMINISTRATOR,
    
    /* A user that an applicant suggests as their supervisor */
    SUGGESTEDSUPERVISOR, PROJECT_PRIMARY_SUPERVISOR, PROJECT_SECONDARY_SUPERVISOR,
    
    PROGRAM_APPLICATION_CREATOR,
    PROJECT_APPLICATION_CREATOR
    
}