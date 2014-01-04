DROP PROCEDURE DELETE_ACTIONS_FOR_STATE_BOUNDED_WORKERS
;

DROP PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE
;

DROP PROCEDURE DELETE_USER_FROM_ROLE
;

DROP PROCEDURE DELETE_APPLICATION_FORM_ACTIONS
;

DROP PROCEDURE INSERT_USER_IN_PROGRAM_ROLE
;

DROP PROCEDURE INSERT_USER_IN_ROLE
;

DROP PROCEDURE INSERT_APPLICATION_FORM_USER_ROLE_UPDATE
;

DROP PROCEDURE SELECT_USER_APPLICATION_FORM_ACTION_LIST
;

DROP PROCEDURE UPDATE_APPLICATION_FORM_ACTION_REQUIRED_DEADLINE
;

DROP PROCEDURE UPDATE_RAISES_URGENT_FLAG
;

ALTER TABLE REGISTERED_USER
	ADD COLUMN is_reference_user INT(1) UNSIGNED DEFAULT 0,
	ADD INDEX (is_reference_user)
;

UPDATE REGISTERED_USER
SET is_reference_user = 1
WHERE username = "prism@ucl.ac.uk"
;

CREATE PROCEDURE DELETE_ROLES (
	IN in_program_id INT(10) UNSIGNED,
	IN in_project_id INT(10) UNSIGNED,
	IN in_registered_user_id INT(10) UNSIGNED,
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE 
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	WHERE (in_program_id IS NULL
			OR APPLICATION_FORM.program_id = in_program_id)
		AND (in_project_id IS NULL
			OR APPLICATION_FORM.project_id = in_project_id)
		AND (in_registered_user_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id)
		AND (in_application_role_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id);

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE 
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE (in_program_id IS NULL
			OR APPLICATION_FORM.program_id = in_program_id)
		AND (in_project_id IS NULL
			OR APPLICATION_FORM.project_id = in_project_id)
		AND (in_registered_user_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id)
		AND (in_application_role_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id);
		
	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id AS registered_user_id, 
			APPLICATION_FORM_USER_ROLE.application_role_id AS application_role_id
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE 
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE (in_program_id IS NULL
				OR APPLICATION_FORM.program_id = in_program_id)
			AND (in_project_id IS NULL
				OR APPLICATION_FORM.project_id = in_project_id)
			AND (in_registered_user_id IS NULL
				OR APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id)
			AND (in_application_role_id IS NULL
				OR APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id)
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
			HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;

END
;

CREATE PROCEDURE DELETE_ACTIONS (
	IN in_application_form_id INT(10) UNSIGNED,
	IN in_scope_id VARCHAR(50))
BEGIN
	
	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN ACTION
		ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND ACTION.action_scope_id = in_scope_id;
	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN ACTION
		ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND ACTION.action_scope_id = in_scope_id;
		
	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id AS registered_user_id, 
			APPLICATION_FORM_USER_ROLE.application_role_id AS application_role_id
		FROM APPLICATION_FORM_USER_ROLE LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
			HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;
			
END
;

CREATE PROCEDURE UPDATE_ACTION_REQUIRED_DEADLINE (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_deadline_timestamp DATE)
BEGIN

	DECLARE in_raises_urgent_flag INT(1) UNSIGNED;

	SET in_raises_urgent_flag = (
		SELECT IF (in_deadline_timestamp < CURRENT_DATE(),
						1,
						0));
						
	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	SET APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp = in_deadline_timestamp,
		APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = in_raises_urgent_flag,
		APPLICATION_FORM_USER_ROLE.raises_urgent_flag = in_raises_urgent_flag
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date = 1;

END
;

CREATE PROCEDURE INSERT_APPLICATION_FORM_UPDATE (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_update_timestamp DATETIME, 
	IN in_update_visibility INT(1) UNSIGNED)
BEGIN

	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
		ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
	SET APPLICATION_FORM_USER_ROLE.update_timestamp = in_update_timestamp,
		APPLICATION_FORM_USER_ROLE.raises_update_flag =
			IF (APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id,
				0,
				1)
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_ROLE.update_visibility >= in_update_visibility;

END
;

CREATE PROCEDURE SELECT_APPLICATION_FORM_ACTIONS (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_state_id VARCHAR(50))
BEGIN
	
	(SELECT APPLICATION_FORM_ACTION_REQUIRED.action_id AS action_id,
		 MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag
	 FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		 ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	 WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		 AND APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
	 GROUP BY APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag DESC,
	 	 APPLICATION_FORM_ACTION_REQUIRED.action_id ASC)
		 UNION
	(SELECT APPLICATION_FORM_ACTION_OPTIONAL.action_id AS action_id,
		 APPLICATION_FORM_ACTION_OPTIONAL.raises_urgent_flag AS raises_urgent_flag
	 FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN AUTHORITY_GROUP
	 	ON APPLICATION_FORM_ACTION_OPTIONAL.authority_group_type_id = AUTHORITY_GROUP.authority_group_type_id
	 INNER JOIN APPLICATION_FORM_USER_ROLE
		 ON AUTHORITY_GROUP.application_role_id = APPLICATION_FORM_USER_ROLE.application_role_id
	 WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		 AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		 AND APPLICATION_FORM_ACTION_OPTIONAL.state_id = in_state_id
	 GROUP BY APPLICATION_FORM_ACTION_OPTIONAL.action_id ASC);

END
;

CREATE PROCEDURE UPDATE_RAISES_URGENT_FLAG ()
BEGIN
	
	UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
		APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
	WHERE APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp < CURRENT_DATE();
	
END
;

CREATE PROCEDURE INSERT_ROLES (
	IN in_program_id INT(10) UNSIGNED,
	IN in_project_id INT(10) UNSIGNED,
	IN in_registered_user_id INT(10) UNSIGNED,
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DECLARE in_reference_user_id INT(10) UNSIGNED;

	SET in_reference_user_id = (
		SELECT id FROM REGISTERED_USER
		WHERE is_reference_user_id = 1);

	INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
		application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag)
		SELECT APPLICATION_FORM.id, in_registered_user_id, in_application_role_id, 0, 
			APPLICATION_FORM_USER_ROLE.update_timestamp, 1
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		INNER JOIN APPLICATION_FORM_ACTION_OPTIONAL
			ON APPLICATION_FORM.status = APPLICATION_FORM_ACTION_OPTIONAL.state_id
		INNER JOIN AUTHORITY_GROUP
			ON APPLICATION_FORM_ACTION_OPTIONAL.authority_group_type_id = AUTHORITY_GROUP.authority_group_type_id
		WHERE (in_program_id IS NULL
				OR APPLICATION_FORM.program_id = in_program_id)
			AND (in_project_id IS NULL
				OR APPLICATION_FORM.project_id = in_project_id)
			AND AUTHORITY_GROUP.authority_id = in_authority_id
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_reference_user_id
		GROUP BY APPLICATION_FORM_USER_ROLE.id;
		
	INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id,
		action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
		SELECT APPLICATION_FORM_USER_ROLE.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
			APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
			APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		INNER JOIN AUTHORITY_GROUP_RECRUITMENT_ACTION
			ON APPLICATION_FORM_ACTION_REQUIRED.action_id = AUTHORITY_GROUP_RECRUITMENT_ACTION.action_id
		INNER JOIN AUTHORITY_GROUP
			ON AUTHORITY_GROUP_RECRUITMENT_ACTION.authority_group_type_id = AUTHORITY_GROUP.authority_group_type_id
		WHERE (in_program_id IS NULL
				OR APPLICATION_FORM.program_id = in_program_id)
			AND (in_project_id IS NULL
				OR APPLICATION_FORM.project_id = in_project_id)
			AND AUTHORITY_GROUP.authority_id = in_authority_id
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_reference_user_id
		GROUP BY APPLICATION_FORM_ACTION_REQUIRED.id;
		
	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN (
		SELECT MIN(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag, 
			APPLICATION_FORM.id AS application_form_id, 
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		WHERE (in_program_id IS NULL
				OR APPLICATION_FORM.program_id = in_program_id)
			AND (in_project_id IS NULL
				OR APPLICATION_FORM.project_id = in_project_id)
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		GROUP BY APPLICATION_FORM.id) AS APPLICATION_UPDATE
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_UPDATE.application_form_id
	SET APPLICATION_FORM_USER_ROLE.raises_update_flag = APPLICATION_UPDATE.raises_update_flag
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	UPDATE APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
		APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
	WHERE (in_program_id IS NULL
			OR APPLICATION_FORM.program_id = in_program_id)
		AND (in_project_id IS NULL
			OR APPLICATION_FORM.project_id = in_project_id)
		AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		AND APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1;

END
;
