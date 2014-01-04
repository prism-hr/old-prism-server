DROP PROCEDURE DELETE_ACTIONS_FOR_STATE_BOUNDED_WORKERS
;

DROP PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE
;

DROP PROCEDURE DELETE_USER_FROM_ROLE
;

DROP PROCEDURE DELETE_APPLICATION_FORM_ACTIONS
;

CREATE PROCEDURE DELETE_ROLES (
	IN in_program_id INT(10) UNSIGNED,
	IN in_project_id INT(10) UNSIGNED,
	IN in_application_form_id INT(10) UNSIGNED,
	IN in_registered_user_id INT(10) UNSIGNED,
	IN in_application_role_id VARCHAR(50),
	IN in_scope_id VARCHAR(50))
BEGIN

	UPDATE APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE 
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN ACTION
		ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
	WHERE (in_program_id IS NULL
			OR APPLICATION_FORM.program_id = in_program_id)
		AND (in_project_id IS NULL
			OR APPLICATION_FORM.project_id = in_project_id) 
		AND (in_application_form_id IS NULL
			OR APPLICATION_FORM.id = in_application_form_id)
		AND (in_registered_user_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id)
		AND (in_application_role_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id)
		AND ACTION.action_scope_id = in_scope_id;
		
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE 
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN ACTION
		ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
	WHERE (in_program_id IS NULL
			OR APPLICATION_FORM.program_id = in_program_id)
		AND (in_project_id IS NULL
			OR APPLICATION_FORM.project_id = in_project_id) 
		AND (in_application_form_id IS NULL
			OR APPLICATION_FORM.id = in_application_form_id)
		AND (in_registered_user_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id)
		AND (in_application_role_id IS NULL
			OR APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id)
		AND ACTION.action_scope_id = in_scope_id;
		
	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id AS registered_user_id, 
			APPLICATION_FORM_USER_ROLE.application_role_id AS application_role_id
		FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE 
			ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		LEFT JOIN ACTION
			ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
		WHERE (in_program_id IS NULL
				OR APPLICATION_FORM.program_id = in_program_id)
			AND (in_project_id IS NULL
				OR APPLICATION_FORM.project_id = in_project_id) 
			AND (in_application_form_id IS NULL
				OR APPLICATION_FORM.id = in_application_form_id)
			AND (in_registered_user_id IS NULL
				OR APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id)
			AND (in_application_role_id IS NULL
				OR APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id)
			AND ACTION.action_scope_id = in_scope_id
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
			HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;
			
END
;

DROP PROCEDURE UPDATE_APPLICATION_FORM_ACTION_REQUIRED_DEADLINE
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

DROP PROCEDURE INSERT_APPLICATION_FORM_USER_ROLE_UPDATE
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

DROP PROCEDURE SELECT_USER_APPLICATION_FORM_ACTION_LIST
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
	 FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_FORM_USER_ROLE
		 ON APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = APPLICATION_FORM_USER_ROLE.application_role_id
	 WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		 AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		 AND APPLICATION_FORM_ACTION_OPTIONAL.state_id = in_state_id
	 GROUP BY APPLICATION_FORM_ACTION_OPTIONAL.action_id ASC);

END
;

DROP PROCEDURE UPDATE_RAISES_URGENT_FLAG

CREATE PROCEDURE UPDATE_RAISES_URGENT_FLAG ()
BEGIN
	
	UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
		APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
	WHERE APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp < CURRENT_DATE();
	
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

DROP PROCEDURE INSERT_USER_IN_PROGRAM_ROLE
;

DROP PROCEDURE INSERT_USER_IN_ROLE
;

ALTER TABLE REGISTERED_USER
	ADD COLUMN can_be_deactivated INT(1) UNSIGNED DEFAULT 1,
	ADD INDEX (can_be_deactivated)
;

UPDATE REGISTERED_USER
SET can_be_deactivated = 0
WHERE username = "prism@ucl.ac.uk"
;

CREATE PROCEDURE INSERT_USER_IN_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED,
	IN in_program_id INT(10) UNSIGNED,
	IN in_project_id INT(10) UNSIGNED,
	IN in_application_role_id VARCHAR(50))
BEGIN
	DECLARE in_base_role_to_copy INT(10) UNSIGNED;

	SET in_base_role_to_copy = (
		SELECT id FROM REGISTERED_USER
		WHERE can_be_deactivated = 0);

	INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
		application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
		SELECT application_form_id, in_registered_user_id, in_application_role_id, 0,
			CURRENT_TIMESTAMP(), 1, 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
			AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
			AND APPLICATION_FORM.status != "VALIDATION"
			AND APPLICATION_FORM.status_when_withdrawn != "VALIDATION";

	INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id,
		action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
		SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
			APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
			APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
			AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
			AND APPLICATION_FORM_ACTION_REQUIRED.action_id = "CONFIRM_ELIGIBILITY";

	IF in_application_role_id = "SUPERADMINISTRATOR" THEN

		INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
			application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
			SELECT application_form_id, in_registered_user_id, in_application_role_id, 0,
				update_timestamp, raises_update_flag, raises_urgent_flag
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
				AND APPLICATION_FORM.status = "VALIDATION"
				OR APPLICATION_FORM.status_when_withdrawn = "VALIDATION";

		INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id,
			action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
			SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
				APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
				APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
				ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
				AND APPLICATION_FORM_ACTION_REQUIRED.action_id != "CONFIRM_ELIGIBILITY";

	END IF;
	UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
		APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		AND APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1;

END
;