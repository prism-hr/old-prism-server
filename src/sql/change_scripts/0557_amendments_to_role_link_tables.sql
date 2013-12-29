INSERT INTO APPLICATION_ROLE (id, update_visibility, do_send_update_notification)
VALUES ("SAFETYNET", 0, 0)
;

ALTER TABLE APPLICATION_ROLE
	ADD COLUMN do_send_role_notification INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (do_send_role_notification)
;

UPDATE APPLICATION_ROLE
SET do_send_role_notification = 1
WHERE id IN ("SUPERADMINISTRATOR", "ADMITTER", "ADMINISTRATOR", "APPROVER", "VIEWER")
;

DELETE FROM USER_ROLE_LINK
;

INSERT INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	SELECT registered_user_id, application_role_id
	FROM APPLICATION_FORM_USER_ROLE
	GROUP BY registered_user_id, application_role_id
;

INSERT INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	SELECT registered_user_id, "SAFETYNET"
	FROM USER_ROLE_LINK
	GROUP BY registered_user_id
;

ALTER TABLE APPLICATION_ROLE
	ADD COLUMN scope VARCHAR(50) NOT NULL DEFAULT "STATE",
	ADD INDEX (scope)
;

UPDATE APPLICATION_ROLE
SET scope = "SYSTEM"
WHERE id IN ("SUPERADMINISTRATOR", "ADMITTER")
;

UPDATE APPLICATION_ROLE
SET scope = "PROGRAM"
WHERE id IN ("ADMINISTRATOR", "APPROVER", "VIEWER")
; 

UPDATE APPLICATION_ROLE
SET scope = "PROJECT"
WHERE id IN ("PROJECTADMINISTRATOR")
; 

UPDATE APPLICATION_ROLE
SET scope = "APPLICATION"
WHERE id IN ("APPLICANT", "REFEREE", "SUGGESTEDSUPERVISOR")
;

DROP PROCEDURE DELETE_ACTIONS_FOR_STATE_BOUNDED_WORKERS
;

CREATE PROCEDURE DELETE_STATE_ROLES (
	IN in_application_form_id INT(10) UNSIGNED)
BEGIN

	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
		ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_ROLE.scope = "STATE";
		
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
		ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
	INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_ROLE.scope = "STATE";
		
	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		INNER JOIN (
			SELECT APPLICATION_FORM_USER_ROLE.registered_user_id
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
				AND APPLICATION_ROLE.scope = "STATE"
			GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id) AS AFFECTED_USER
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = AFFECTED_USER.registered_user_id
		LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_ROLE.scope = "STATE"
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;
			
END
;

DROP PROCEDURE DELETE_APPLICATION_FORM_ACTIONS
;

CREATE PROCEDURE DELETE_APPLICATION_ROLES (
	IN in_application_form_id INT(10) UNSIGNED)
BEGIN

	UPDATE APPLICATION_FORM_USER_ROLE
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
 	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id;
 	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id;
	
	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		INNER JOIN (
			SELECT APPLICATION_FORM_USER_ROLE.registered_user_id
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
				AND APPLICATION_ROLE.scope IN ("APPLICATION", "STATE")
			GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id) AS AFFECTED_USER
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = AFFECTED_USER.registered_user_id
		LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_ROLE.scope IN ("APPLICATION", "STATE")
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;

END
;

CREATE PROCEDURE DELETE_PROJECT_ROLES (
	IN in_project_id INT(10) UNSIGNED)
BEGIN

	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		INNER JOIN (
			SELECT APPLICATION_FORM_USER_ROLE.registered_user_id
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			WHERE APPLICATION_FORM_USER_ROLE.project_id = in_project_id
				AND APPLICATION_ROLE.scope IN ("PROJECT", "APPLICATION", "STATE")
			GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id) AS AFFECTED_USER
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = AFFECTED_USER.registered_user_id
		LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_ROLE.scope IN ("PROJECT", "APPLICATION", "STATE")
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;

END
;

CREATE PROCEDURE DELETE_PROGRAM_ROLES (
	IN in_program_id INT(10) UNSIGNED)
BEGIN

	DELETE USER_ROLE_LINK.*
	FROM USER_ROLE_LINK INNER JOIN (
		SELECT APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		INNER JOIN (
			SELECT APPLICATION_FORM_USER_ROLE.registered_user_id
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			WHERE APPLICATION_FORM_USER_ROLE.program_id = in_program_id
				AND APPLICATION_ROLE.scope IN ("PROGRAM", "PROJECT", "APPLICATION", "STATE")
			GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id) AS AFFECTED_USER
			ON APPLICATION_FORM_USER_ROLE.registered_user_id = AFFECTED_USER.registered_user_id
		LEFT JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_ROLE.scope IN ("PROGRAM", "PROJECT", "APPLICATION", "STATE")
		GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM_USER_ROLE.application_role_id
		HAVING COUNT(APPLICATION_FORM_ACTION_REQUIRED.id) = 0) AS ORPHANED_USER_ROLE
		ON USER_ROLE_LINK.registered_user_id = ORPHANED_USER_ROLE.registered_user_id
			AND USER_ROLE_LINK.application_role_id = ORPHANED_USER_ROLE.application_role_id;

END
;

DROP PROCEDURE UPDATE_APPLICATION_FORM_ACTION_REQUIRED_DEADLINE
;

CREATE PROCEDURE UPDATE_APPLICATION_FORM_DUE_DATE (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_deadline_timestamp DATE)
BEGIN

	DECLARE in_raises_urgent_flag INT(1) UNSIGNED;

	SET in_raises_urgent_flag = (
		SELECT IF (in_deadline_timestamp <= CURRENT_DATE(),
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

CREATE PROCEDURE SELECT_APPLICATION_FORM_ACTION (
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

DROP PROCEDURE INSERT_USER_IN_ROLE
;

CREATE PROCEDURE INSERT_USER_IN_SYSTEM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DECLARE in_base_role_to_copy INT(10) UNSIGNED;

	SET in_base_role_to_copy = (
		SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
		FROM APPLICATION_FORM_USER_ROLE
		WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR");

	INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
		application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
		SELECT application_form_id, in_registered_user_id, in_application_role_id, 0,
			IF (LATEST_UPDATE.update_timestamp IS NOT NULL,
				LATEST_UPDATE.update_timestamp,
				CURRENT_TIMESTAMP()),
			IF (LATEST_UPDATE.update_visibility IS NOT NULL,
				LATEST_UPDATE.update_visibility,
				0), 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
				MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
				APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			GROUP BY APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility) AS LATEST_UPDATE
			ON APPLICATION_FORM_USER_ROLE.application_form_id = LATEST_UPDATE.application_form_id
				AND APPLICATION_ROLE.update_visibility = LATEST_UPDATE.update_visibility
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
				IF (LATEST_UPDATE.update_timestamp IS NOT NULL,
					LATEST_UPDATE.update_timestamp,
					CURRENT_TIMESTAMP()),
				IF (LATEST_UPDATE.update_visibility IS NOT NULL,
					LATEST_UPDATE.update_visibility,
					0), 0
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			LEFT JOIN (
				SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
					MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
					APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility
				FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
					ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
				WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
				GROUP BY APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility) AS LATEST_UPDATE
				ON APPLICATION_FORM_USER_ROLE.application_form_id = LATEST_UPDATE.application_form_id
					AND APPLICATION_ROLE.update_visibility = LATEST_UPDATE.update_visibility
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
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		AND APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1;

END
;

DROP PROCEDURE DELETE_USER_FROM_ROLE
;

CREATE PROCEDURE DELETE_USER_FROM_SYSTEM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
	   AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM_USER_ROLE
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		
END
;

DROP PROCEDURE INSERT_USER_IN_PROGRAM_ROLE
;

CREATE PROCEDURE INSERT_USER_IN_PROGRAM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_program_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DECLARE in_base_role_to_copy INT(10) UNSIGNED;

	SET in_base_role_to_copy = (
		SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
		FROM APPLICATION_FORM_USER_ROLE
		WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR");

	INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
		application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
		SELECT application_form_id, in_registered_user_id, in_application_role_id, 0,
			IF (LATEST_UPDATE.update_timestamp IS NOT NULL,
				LATEST_UPDATE.update_timestamp,
				CURRENT_TIMESTAMP()),
			IF (LATEST_UPDATE.update_visibility IS NOT NULL,
				LATEST_UPDATE.update_visibility,
				0), 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
				MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
				APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			GROUP BY APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility) AS LATEST_UPDATE
			ON APPLICATION_FORM_USER_ROLE.application_form_id = LATEST_UPDATE.application_form_id
				AND APPLICATION_ROLE.update_visibility = LATEST_UPDATE.update_visibility
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
			AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
			AND APPLICATION_FORM.program_id = in_program_id;

	IF in_application_role_id = "ADMINISTRATOR" THEN

		INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id,
			action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
			SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
				APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
				APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
				ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
			WHERE APPLICATION_FORM.program_id = in_program_id
				AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
				AND APPLICATION_FORM_ACTION_REQUIRED.action_id NOT IN ("CONFIRM_ELIGIBILITY", "CONFIRM_OFFER_RECOMMENDATION");

	ELSEIF in_application_role_id = "APPROVER" THEN

			INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id,
				action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
				SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
					APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
					APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag
				FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
					ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
				INNER JOIN APPLICATION_FORM
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
				INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
				WHERE APPLICATION_FORM.program_id = in_program_id
					AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
					AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
					AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
					AND APPLICATION_FORM.status = "APPROVAL"
					AND APPLICATION_FORM_ACTION_REQUIRED.action_id != "CONFIRM_ELIGIBILITY";

	END IF;

		UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
		INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
			AND APPLICATION_FORM.program_id = in_program_id
			AND APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1;

END
;

DROP PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE
;

CREATE PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_program_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.program_id = in_program_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.program_id = in_program_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

END
;

CREATE PROCEDURE INSERT_USER_IN_PROJECT_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_project_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DECLARE in_base_role_to_copy INT(10) UNSIGNED;

	SET in_base_role_to_copy = (
		SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTADMINISTRATOR"
			AND APPLICATION_FORM.project_id = in_project_id);

	INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
		application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
		SELECT application_form_id, in_registered_user_id, in_application_role_id, 0,
			IF (LATEST_UPDATE.update_timestamp IS NOT NULL,
				LATEST_UPDATE.update_timestamp,
				CURRENT_TIMESTAMP()),
			IF (LATEST_UPDATE.update_visibility IS NOT NULL,
				LATEST_UPDATE.update_visibility,
				0), 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
				MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
				APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			GROUP BY APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_ROLE.update_visibility) AS LATEST_UPDATE
			ON APPLICATION_FORM_USER_ROLE.application_form_id = LATEST_UPDATE.application_form_id
				AND APPLICATION_ROLE.update_visibility = LATEST_UPDATE.update_visibility
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
			AND APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTADMINISTRATOR"
			AND APPLICATION_FORM.project_id = in_project_id;
			
		INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id,
			action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
			SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
				APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
				APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
				ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
			WHERE APPLICATION_FORM.project_id = in_project_id
				AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTADMINISTRATOR";

		UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
		INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
			AND APPLICATION_FORM.project_id = in_project_id
			AND APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1;

END
;

CREATE PROCEDURE DELETE_USER_FROM_PROJECT_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_project_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.project_id = in_project_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.project_id = in_project_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

END
;

INSERT INTO APPLICATION_ROLE (update_visibility, do_send_update_notification, do_send_role_notification, scope)
	VALUES(1, 0, 0, "PROJECT")
;
