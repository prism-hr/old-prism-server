package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismActionEnhancement {

	APPLICATION_VIEW_EDIT_AS_CREATOR, //
	APPLICATION_VIEW_EDIT_AS_APPROVER, //
	APPLICATION_VIEW_AS_CREATOR, //
	APPLICATION_VIEW_AS_RECRUITER, //
	APPLICATION_VIEW_AS_APPROVER, //
	APPLICATION_VIEW_AS_REFEREE, //
	
	PROJECT_VIEW_EDIT_AS_USER, //
	PROJECT_VIEW_AS_USER, // ,
	
	PROGRAM_VIEW_EDIT_AS_USER, //
	PROGRAM_VIEW_AS_USER, //
	
	DEPARTMENT_VIEW_EDIT_AS_USER, //
	DEPARTMENT_VIEW_AS_USER, //
	
	INSTITUTION_VIEW_EDIT_AS_USER, //
	INSTITUTION_VIEW_AS_USER, //
	
	SYSTEM_VIEW_EDIT_AS_USER;

	public enum PrismActionEnhancementGroup {

		APPLICATION_EQUAL_OPPORTUNITIES_VIEWER(APPLICATION_VIEW_AS_CREATOR, APPLICATION_VIEW_AS_APPROVER,
				APPLICATION_VIEW_EDIT_AS_CREATOR, APPLICATION_VIEW_EDIT_AS_APPROVER), //

		RESOURCE_ADMINISTRATOR(PROJECT_VIEW_EDIT_AS_USER, PROGRAM_VIEW_EDIT_AS_USER, DEPARTMENT_VIEW_EDIT_AS_USER,
				INSTITUTION_VIEW_EDIT_AS_USER, SYSTEM_VIEW_EDIT_AS_USER);

		private PrismActionEnhancement[] actionEnhancements;

		private PrismActionEnhancementGroup(PrismActionEnhancement... actionEnhancements) {
			this.actionEnhancements = actionEnhancements;
		}

		public PrismActionEnhancement[] getActionEnhancements() {
			return actionEnhancements;
		}

	}

}
