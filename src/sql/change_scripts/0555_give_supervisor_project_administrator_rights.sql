INSERT INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, update_timestamp, raises_update_flag, 
	raises_urgent_flag, assigned_timestamp)
	SELECT APPLICATION_FORM_USER_ROLE.application_form_id, PROJECT.primary_supervisor_id, APPLICATION_FORM_USER_ROLE.application_role_id,
		APPLICATION_FORM_USER_ROLE.update_timestamp, APPLICATION_FORM_USER_ROLE.raises_update_flag, APPLICATION_FORM_USER_ROLE.raises_urgent_flag,
		APPLICATION_FORM_USER_ROLE.assigned_timestamp
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	INNER JOIN PROJECT
		ON APPLICATION_FORM.project_id = PROJECT.id
	WHERE application_role_id = "PROJECTADMINISTRATOR"
;

INSERT INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, action_id, deadline_timestamp, bind_deadline_to_due_date,
	raises_urgent_flag, assigned_timestamp)
	SELECT APPLICATION_FORM_USER_ROLE2.id, APPLICATION_FORM_ACTION_REQUIRED.action_id, APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp,
		APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date, APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag, 
		APPLICATION_FORM_ACTION_REQUIRED.assigned_timestamp
	FROM PROJECT INNER JOIN APPLICATION_FORM
		ON PROJECT.id = APPLICATION_FORM.project_id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		AND PROJECT.administrator_id = APPLICATION_FORM_USER_ROLE.registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTADMINISTRATOR"
	INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE2
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE2.application_form_id
		AND PROJECT.primary_supervisor_id = APPLICATION_FORM_USER_ROLE2.registered_user_id
		AND APPLICATION_FORM_USER_ROLE2.application_role_id = "PROJECTADMINISTRATOR"
;