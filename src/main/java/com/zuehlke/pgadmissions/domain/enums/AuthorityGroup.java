package com.zuehlke.pgadmissions.domain.enums;

public enum AuthorityGroup {

	/* Users with system wide authority */
	SYSTEM(new Authority[]{Authority.ADMITTER, Authority.SUPERADMINISTRATOR}),
	
	/* Users with program wide authority */
	PROGRAM(new Authority[]{Authority.ADMINISTRATOR, Authority.APPROVER, Authority.VIEWER}),
	
	/* Users with project wide authority */
	PROJECT(new Authority[]{Authority.PROJECTADMINISTRATOR, Authority.PROJECTAUTHOR}),
	
	/* Users with application wide authority */
	APPLICATION(new Authority[]{Authority.APPLICANT, Authority.REFEREE, Authority.SUGGESTEDSUPERVISOR}),
	
	/* Users with application state wide authority */
	STATE(new Authority[]{Authority.INTERVIEWER, Authority.REVIEWER, Authority.STATEADMINISTRATOR, 
			Authority.SUPERVISOR}),
	
	/* Users with no authority (other than over their own user account) */
	USER(new Authority[]{Authority.SAFETYNET}),
	
	/* Users that can configure the system */
	SYSTEMADMIN(new Authority[]{Authority.SUPERADMINISTRATOR}),
	
	/* Users that can configure and manage admitters */
	ADMITTERADMIN(new Authority[]{Authority.ADMITTER, Authority.SUPERADMINISTRATOR}),
	
	/* Users that can configure and manage users for programs */
	PROGRAMADMIN(new Authority[]{Authority.ADMINISTRATOR, Authority.SUPERADMINISTRATOR}),
	
	/* Users that can author and edit program adverts */
	PROGRAMAUTHOR(new Authority[]{Authority.ADMINISTRATOR, Authority.SUPERADMINISTRATOR}),
	
	/* Users that author project adverts */
	PROJECTAUTHOR(new Authority[]{Authority.ADMINISTRATOR, Authority.APPROVER, Authority.INTERVIEWER, 
			Authority.PROJECTADMINISTRATOR, Authority.REVIEWER, Authority.STATEADMINISTRATOR, 
			Authority.SUPERVISOR, Authority.SUPERADMINISTRATOR}),
	
	/* Users that can edit and delete project adverts */
	PROJECTEDITOR(new Authority[]{Authority.ADMINISTRATOR, Authority.PROJECTADMINISTRATOR, 
			Authority.PROJECTAUTHOR, Authority.SUPERADMINISTRATOR}),
	
	/* Users that can view equal opportunities information */
	EQUALOPPSVIEWER(new Authority[]{Authority.APPLICANT}),
	
	/* Users that can view equal opportunities information */
	CONVICTIONSVIEWER(new Authority[]{Authority.ADMINISTRATOR, Authority.ADMITTER, Authority.APPLICANT,
			Authority.APPROVER, Authority.PROJECTADMINISTRATOR, Authority.SUPERADMINISTRATOR}),
			
	/* Users that can view reference documents */
	REFERENCESVIEWER(new Authority[]{Authority.ADMINISTRATOR, Authority.ADMITTER, Authority.APPLICANT,
			Authority.APPROVER, Authority.INTERVIEWER, Authority.PROJECTADMINISTRATOR, Authority.REVIEWER,
			Authority.STATEADMINISTRATOR,Authority.SUPERADMINISTRATOR, Authority.SUPERVISOR, Authority.VIEWER}),
	
	/* Users that can view user ratings of applications */
	RATINGSVIEWER(new Authority[]{Authority.ADMINISTRATOR, Authority.ADMITTER, Authority.APPLICANT,
			Authority.APPROVER, Authority.INTERVIEWER, Authority.PROJECTADMINISTRATOR, Authority.REVIEWER,
			Authority.STATEADMINISTRATOR,Authority.SUPERADMINISTRATOR, Authority.SUPERVISOR, Authority.VIEWER}),

	/* Users that can be regarded as potential supervisors for applications */		
	POTENTIALSUPERVISOR(new Authority[]{Authority.APPROVER, Authority.INTERVIEWER, 
			Authority.PROJECTADMINISTRATOR, Authority.REVIEWER, Authority.STATEADMINISTRATOR, 
			Authority.SUGGESTEDSUPERVISOR, Authority.SUPERVISOR});
	
	private final Authority[] authorities;
	
	private AuthorityGroup(Authority... authorities) {
		this.authorities = authorities;
	}

	public Authority[] authorities() {
		return authorities;
	}
	
}