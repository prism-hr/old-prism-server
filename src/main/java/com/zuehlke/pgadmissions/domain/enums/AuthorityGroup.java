package com.zuehlke.pgadmissions.domain.enums;

public enum AuthorityGroup {

	/* Roles with system wide authority */
	SYSTEM(new Authority[]{Authority.ADMITTER, Authority.SUPERADMINISTRATOR}),
	
	/* Roles with program wide authority */
	PROGRAM(new Authority[]{Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER}),
	
	/* Roles with project wide authority */
	PROJECT(new Authority[]{Authority.PROJECTADMINISTRATOR, Authority.PROJECTAUTHOR}),
	
	/* Roles with application wide authority */
	APPLICATION(new Authority[]{Authority.APPLICANT, Authority.REFEREE, Authority.SUGGESTEDSUPERVISOR,
			Authority.INTERVIEWER, Authority.REVIEWER, Authority.STATEADMINISTRATOR, Authority.SUPERVISOR}),
	
	/* Roles that can configure the system */
	SYSTEMADMIN(new Authority[]{Authority.SUPERADMINISTRATOR}),
	
	/* Roles that can configure and manage admitters */
	ADMITTERADMIN(new Authority[]{Authority.ADMITTER, Authority.SUPERADMINISTRATOR}),
	
	/* Roles that can configure and manage users for programs */
	PROGRAMADMIN(new Authority[]{Authority.ADMINISTRATOR, Authority.SUPERADMINISTRATOR}),
	
	/* Roles that can author and edit program adverts */
	PROGRAMAUTHOR(new Authority[]{Authority.ADMINISTRATOR, Authority.SUPERADMINISTRATOR}),
	
	/* Roles that author project adverts */
	PROJECTAUTHOR(new Authority[]{Authority.ADMINISTRATOR, Authority.APPROVER, Authority.INTERVIEWER, 
			Authority.PROJECTADMINISTRATOR, Authority.REVIEWER, Authority.STATEADMINISTRATOR, 
			Authority.SUPERVISOR, Authority.SUPERADMINISTRATOR}),
	
	/* Roles that can edit and delete project adverts */
	PROJECTEDITOR(new Authority[]{Authority.ADMINISTRATOR, Authority.PROJECTADMINISTRATOR, 
			Authority.PROJECTAUTHOR, Authority.SUPERADMINISTRATOR}),
	
	/* Roles that can view equal opportunities information */
	EQUALOPPSVIEWER(new Authority[]{Authority.APPLICANT}),
	
	/* Roles that can view equal opportunities information */
	CONVICTIONSVIEWER(new Authority[]{Authority.ADMINISTRATOR, Authority.ADMITTER, Authority.APPLICANT,
			Authority.APPROVER, Authority.PROJECTADMINISTRATOR, Authority.SUPERADMINISTRATOR}),
	
	/* Roles that can view user ratings of applications */
	COMMENTVIEWER(new Authority[]{Authority.ADMINISTRATOR, Authority.ADMITTER, Authority.APPROVER, 
			Authority.INTERVIEWER, Authority.PROJECTADMINISTRATOR, Authority.REVIEWER,
			Authority.STATEADMINISTRATOR,Authority.SUPERADMINISTRATOR, Authority.SUPERVISOR, Authority.VIEWER}),

	/* Roles that can be regarded as potential supervisors for applications */		
	POTENTIALSUPERVISOR(new Authority[]{Authority.APPROVER, Authority.INTERVIEWER, 
			Authority.PROJECTADMINISTRATOR, Authority.REVIEWER, Authority.STATEADMINISTRATOR, 
			Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR}),
	
	/* Roles that can commit application state changes */
	TRANSITIONCOMMITTER(new Authority[]{Authority.ADMINISTRATOR, Authority.APPROVER, 
			Authority.PROJECTADMINISTRATOR, Authority.SUPERADMINISTRATOR});
	
	private final Authority[] authorities;
	
	private AuthorityGroup(Authority... authorities) {
		this.authorities = authorities;
	}

	public Authority[] authorities() {
		return authorities;
	}
	
}