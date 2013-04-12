package com.zuehlke.pgadmissions.domain.enums;

public enum Authority {
    
    /* This is an administrator of a particular programme */
    ADMINISTRATOR,
    
    /* An applicant applying for a programme */
    APPLICANT, 
    
    /* A person which approves an application and decides whether the application will be APPROVED or REJECTED */
    APPROVER, 
    
    /* A person which interviews applicants */
    INTERVIEWER, 
    
    /* A person which knows an applicant and provides feedback about him to the UCL Staff */
    REFEREE, 
    
    /* A person which reviews an existing application before moving it to the APPROVAL stage */
    REVIEWER, 
    
    /* Prism Superuser which can execute every possible action within the system */
    SUPERADMINISTRATOR, 
    
    /* A person which makes a commitment towards the applicant to supervise him after his application has been APPROVED */
    SUPERVISOR,
    
    /* Enables the holder to view all applications within the programme. */
    VIEWER
}
