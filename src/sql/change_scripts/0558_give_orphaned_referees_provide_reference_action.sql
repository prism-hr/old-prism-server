INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
	SELECT APPLICATION_FORM.id, APPLICATION_FORM_REFEREE.registered_user_id, "REFEREE", 0, APPLICATION_FORM.last_updated,
		1, 1 
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_REFEREE
		ON APPLICATION_FORM.id = APPLICATION_FORM_REFEREE.application_form_id
	LEFT JOIN REFERENCE_COMMENT
		ON APPLICATION_FORM_REFEREE.id = REFERENCE_COMMENT.referee_id
	LEFT JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		AND APPLICATION_FORM_REFEREE.registered_user_id = APPLICATION_FORM_USER_ROLE.registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = "REFEREE"
	WHERE REFERENCE_COMMENT.id IS NULL
		AND APPLICATION_FORM_REFEREE.declined = 0
		AND APPLICATION_FORM_REFEREE.registered_user_id IS NOT NULL
		AND APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
		AND APPLICATION_FORM_USER_ROLE.id IS NULL
;

INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, action_id, deadline_timestamp,
	bind_deadline_to_due_date, raises_urgent_flag)
	SELECT APPLICATION_FORM_USER_ROLE.id, "PROVIDE_REFERENCE", CURRENT_DATE(), 0, 1
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_REFEREE
			ON APPLICATION_FORM.id = APPLICATION_FORM_REFEREE.application_form_id
			AND APPLICATION_FORM_REFEREE.declined = 0
		LEFT JOIN REFERENCE_COMMENT
			ON APPLICATION_FORM_REFEREE.id = REFERENCE_COMMENT.referee_id
		INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE.application_form_id
			AND APPLICATION_FORM_REFEREE.registered_user_id = APPLICATION_FORM_USER_ROLE.registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = "REFEREE"
		LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			AND APPLICATION_FORM_ACTION_REQUIRED.action_id = "PROVIDE_REFERENCE"
		WHERE REFERENCE_COMMENT.id IS NULL
			AND APPLICATION_FORM_REFEREE.declined = 0
			AND APPLICATION_FORM_REFEREE.registered_user_id IS NOT NULL
			AND APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM_ACTION_REQUIRED.id IS NULL
;

CALL UPDATE_RAISES_URGENT_FLAG()
;