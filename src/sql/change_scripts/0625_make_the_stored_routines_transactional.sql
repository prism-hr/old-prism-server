DROP PROCEDURE SP_DELETE_APPLICATION_ACTIONS
;

CREATE PROCEDURE SP_DELETE_APPLICATION_ACTIONS (
	IN in_application_form_id INT(10) UNSIGNED)
BEGIN
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;

		UPDATE APPLICATION_FORM_USER_ROLE
		SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
	 	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id;
	
		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id;
	
	COMMIT;
	
END
;

DROP PROCEDURE SP_DELETE_APPLICATION_ROLE
;

CREATE PROCEDURE SP_DELETE_APPLICATION_ROLE (
	IN in_application_form_id INT(10) UNSIGNED,
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;
	
		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
	
		DELETE
		FROM APPLICATION_FORM_USER_ROLE
		WHERE application_form_id = in_application_form_id
			AND registered_user_id = in_registered_user_id
			AND application_role_id = in_application_role_id;
			
	COMMIT;
	
END
;

DROP PROCEDURE SP_DELETE_EXPIRED_CLOSING_DATES;

CREATE PROCEDURE SP_DELETE_EXPIRED_CLOSING_DATES ()
BEGIN

	DECLARE baseline_date DATE;

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;

		SET baseline_date = (SELECT CURRENT_DATE());
		
		DELETE FROM PROGRAM_CLOSING_DATES
		WHERE closing_date < baseline_date;
	
		UPDATE PROJECT
		SET closing_date = NULL
		WHERE closing_date < baseline_date;
		
	COMMIT;
	
END
;

DROP PROCEDURE SP_DELETE_INACTIVE_ADVERTS
;

ALTER TABLE REJECT_REASON
	ADD COLUMN is_system_rejection INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (is_system_rejection)
;

UPDATE REJECT_REASON
SET is_system_rejection = 1
WHERE id = 7
;

ALTER TABLE REGISTERED_USER
	ADD COLUMN is_system_user INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (is_system_user)
;

UPDATE REGISTERED_USER
SET is_system_user = 1
WHERE id = 15
;

CREATE PROCEDURE SP_DELETE_INACTIVE_ADVERTS ()
BEGIN
	
	DECLARE baseline DATE;
	DECLARE contingency INT(10) UNSIGNED;
	DECLARE system_rejection_id INT(10) UNSIGNED;
	DECLARE system_user_id INT(10) UNSIGNED;
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;
	
		SET baseline = CURRENT_DATE();
	
		SET contingency = (
			SELECT SUM(duration)
			FROM STATE
			WHERE duration IS NOT NULL);

		UPDATE PROGRAM LEFT JOIN (
			SELECT PROGRAM.id
			FROM PROGRAM INNER JOIN PROGRAM_INSTANCE
				ON PROGRAM.id = PROGRAM_INSTANCE.program_id
			WHERE PROGRAM_INSTANCE.disabled_date IS NULL
				OR PROGRAM_INSTANCE.disabled_date > UNIX_TIMESTAMP(baseline) + contingency
			GROUP BY PROGRAM.id) AS ACTIVE_PROGRAM
			ON PROGRAM.id = ACTIVE_PROGRAM.id
		LEFT JOIN ADVERT AS PROGRAM_ADVERT
			ON PROGRAM.id = PROGRAM_ADVERT.id
		LEFT JOIN PROGRAM_INSTANCE
			ON PROGRAM.id = PROGRAM_INSTANCE.program_id
		LEFT JOIN PROJECT
			ON PROGRAM.id = PROJECT.program_id
		LEFT JOIN ADVERT AS PROJECT_ADVERT
			ON PROJECT.id = PROJECT_ADVERT.id
		SET PROGRAM_ADVERT.active = 0,
			PROGRAM_ADVERT.enabled = 0,
			PROGRAM_INSTANCE.enabled = 0,
			PROJECT_ADVERT.active = 0,
			PROJECT_ADVERT.enabled = 0
		WHERE ACTIVE_PROGRAM.id IS NULL;
		
		UPDATE ADVERT
		SET active = 0
		WHERE advert_type = "PROJECT" 
			AND closing_date < baseline;
		
		UPDATE APPLICATION_FORM INNER JOIN ADVERT
			ON APPLICATION_FORM.project_id = ADVERT.id
		SET APPLICATION_FORM.project_id = NULL
		WHERE ADVERT.enabled = 0;
		
		/* SET system_rejection_id = (
			SELECT id
			FROM REJECT_REASON
			WHERE is_system_rejection = 1);
			
		SET system_user_id = (
			SELECT id
			FROM REGISTERED_USER_ID
			WHERE is_system_user = 1); */
		
		/* Queries to:
		 * 1) Automatically reject applications for disabled programs
		 * 2) Remove workflow actions for automatically rejected applications 
		 * Go here! */
		
	COMMIT;
	
END
;

DROP PROCEDURE SP_DELETE_PROGRAM_ROLE
;

CREATE PROCEDURE SP_DELETE_PROGRAM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_program_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;

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
			
	COMMIT;
	
END
;

DROP PROCEDURE SP_DELETE_ROLE_ACTION
;

CREATE PROCEDURE SP_DELETE_ROLE_ACTION (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50), 
	IN in_action_id VARCHAR(50))
BEGIN
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;

		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
			AND APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id;
	
		UPDATE APPLICATION_FORM_USER_ROLE LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag,
				APPLICATION_FORM_USER_ROLE.id AS application_form_user_role_id
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
				ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
			GROUP BY APPLICATION_FORM_USER_ROLE.id) AS OTHER_ACTION
		ON APPLICATION_FORM_USER_ROLE.id = OTHER_ACTION.application_form_user_role_id
		SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag =
			IF(OTHER_ACTION.raises_urgent_flag IS NOT NULL,
				OTHER_ACTION.raises_urgent_flag,
				0)
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
			
	COMMIT;
	
END
;

DROP PROCEDURE 

CREATE PROCEDURE SP_DELETE_STATE_ACTIONS (
	IN in_application_form_id INT(10) UNSIGNED)
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;

		UPDATE APPLICATION_FORM_USER_ROLE
		SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id NOT IN ("ADMITTER", "REFEREE");
	
		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id NOT IN ("ADMITTER", "REFEREE");
			
	COMMIT;

END
;

DROP PROCEDURE SP_DELETE_USER_ACTION
;

CREATE PROCEDURE SP_DELETE_USER_ACTION (
	IN in_application_form_id INT(10) UNSIGNED,
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50), 
	IN in_action_id VARCHAR(50))
BEGIN

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;
	
		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
			AND APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id;
	
		UPDATE APPLICATION_FORM_USER_ROLE LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag,
				APPLICATION_FORM_USER_ROLE.id AS application_form_user_role_id
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
				ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
				AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
			GROUP BY APPLICATION_FORM_USER_ROLE.id) AS OTHER_ACTION
		ON APPLICATION_FORM_USER_ROLE.id = OTHER_ACTION.application_form_user_role_id
		SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag =
			IF(OTHER_ACTION.raises_urgent_flag IS NOT NULL,
				OTHER_ACTION.raises_urgent_flag,
				0)
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
			
	COMMIT;
	
END
;

DROP PROCEDURE SP_DELETE_USER_ROLE
;

CREATE PROCEDURE SP_DELETE_USER_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;

		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		   AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		   
		DELETE APPLICATION_FORM_USER_ROLE.*
		FROM APPLICATION_FORM_USER_ROLE
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
			
	COMMIT;

END
;

DROP PROCEDURE SP_INSERT_PROGRAM_ROLE
;

CREATE PROCEDURE SP_INSERT_PROGRAM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_program_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DECLARE in_base_role_to_copy INT(10) UNSIGNED;
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
		
	START TRANSACTION;
	
		SET in_base_role_to_copy = (
			SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
			FROM APPLICATION_FORM_USER_ROLE
			WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR");
			
		INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
			application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
			SELECT application_form_id, in_registered_user_id, in_application_role_id, 0,
				CURRENT_TIMESTAMP(), 1, 0
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
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
				INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
				WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
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
					INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
						ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
					INNER JOIN APPLICATION_FORM
						ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
					WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
						AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
						AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
						AND APPLICATION_FORM.status = "APPROVAL"
						AND APPLICATION_FORM_ACTION_REQUIRED.action_id != "CONFIRM_ELIGIBILITY";
						
		END IF;
		
			UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
				ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
			INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
				APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
				AND APPLICATION_FORM.program_id = in_program_id
				AND APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1;
				
	COMMIT;
	
END
;

DROP PROCEDURE SP_INSERT_USER_ROLE
;

CREATE PROCEDURE SP_INSERT_USER_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DECLARE in_base_role_to_copy INT(10) UNSIGNED;
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
		
	START TRANSACTION;
	
		SET in_base_role_to_copy = (
			SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
			FROM APPLICATION_FORM_USER_ROLE
			WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR");
			
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
			
	COMMIT;
		
END
;

DROP PROCEDURE SP_SELECT_RECOMMENDED_ADVERTS
;

CREATE PROCEDURE SP_SELECT_RECOMMENDED_ADVERTS (
	IN in_registered_user_id INT, 
	IN in_ranking_threshold DECIMAL(3,2))
BEGIN
	DECLARE ranking_threshold INT(10) UNSIGNED;
	DECLARE baseline_date DATE;
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		DROP TABLE RECOMMENDED_ADVERT;
   		ROLLBACK;
	END;
	
	START TRANSACTION;
	
		SET baseline_date = CURRENT_DATE();
		
		CREATE TEMPORARY TABLE RECOMMENDED_ADVERT (
			id INT(10) UNSIGNED NOT NULL,
			title VARCHAR(255) NOT NULL,
			description VARCHAR(3000) NOT NULL,
			study_duration INT(4) NOT NULL,
			funding VARCHAR(2000),
			program_code VARCHAR(50) NOT NULL,
			closing_date DATE,
			primary_supervisor_first_name VARCHAR(30) NOT NULL,
			primary_supervisor_last_name VARCHAR(40) NOT NULL,
			primary_supervisor_email VARCHAR(255) NOT NULL,
			advert_type VARCHAR(10) NOT NULL,
			secondary_supervisor_first_name VARCHAR(30),
			secondary_supervisor_last_name VARCHAR(40),
			ranking DECIMAL(8,2) NOT NULL DEFAULT 0.00,
			last_edited_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
			PRIMARY KEY (id),
			INDEX (last_edited_timestamp),
			INDEX (ranking)) ENGINE = MEMORY
			SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
				ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
				REGISTERED_USER2.firstname AS primary_supervisor_first_name, REGISTERED_USER2.lastname AS primary_supervisor_last_name,
				REGISTERED_USER2.email AS primary_supervisor_email, "PROGRAM" AS advert_type, NULL AS secondary_supervisor_first_name,
				NULL AS secondary_supervisor_last_name, ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM2.id))), 2) AS ranking,
				ADVERT.last_edited_timestamp AS last_edited_timestamp
			FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
				ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
			INNER JOIN REGISTERED_USER
				ON APPLICATION_FORM_USER_ROLE.registered_user_id = REGISTERED_USER.id
			INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE2
				ON REGISTERED_USER.id = APPLICATION_FORM_USER_ROLE2.registered_user_id
			INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
				ON APPLICATION_FORM_USER_ROLE2.application_form_id = APPLICATION_FORM2.id
			INNER JOIN PROGRAM
				ON APPLICATION_FORM2.program_id = PROGRAM.id
			INNER JOIN ADVERT
				ON PROGRAM.id = ADVERT.id
			INNER JOIN REGISTERED_USER AS REGISTERED_USER2
				ON ADVERT.registered_user_id = REGISTERED_USER2.id
			LEFT JOIN PROGRAM_CLOSING_DATES
				ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
			WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id IN ("APPROVER", "INTERVIEWER",
					"PROJECTADMINISTRATOR", "REVIEWER", "SUGGESTEDSUPERVISOR", "STATEADMINISTRATOR", "SUPERVISOR")
				AND REGISTERED_USER.enabled = 1
				AND APPLICATION_FORM.program_id != PROGRAM.id
				AND ADVERT.enabled = 1
				AND ADVERT.active = 1
				AND (PROGRAM_CLOSING_DATES.id IS NULL
					OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
				AND APPLICATION_FORM2.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
			GROUP BY ADVERT.id;
	
		INSERT INTO RECOMMENDED_ADVERT
			SELECT *
			FROM (
				SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
					ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
					REGISTERED_USER.firstname AS primary_supervisor_first_name, REGISTERED_USER.lastname AS primary_supervisor_last_name,
					REGISTERED_USER.email AS primary_supervisor_email, "PROGRAM" AS advert_type, NULL AS secondary_supervisor_first_name,
					NULL AS secondary_supervisor_last_name, ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM3.id))), 2) AS ranking,
					ADVERT.last_edited_timestamp AS last_edited_timestamp
				FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
					ON APPLICATION_FORM.program_id = APPLICATION_FORM2.program_id
				INNER JOIN APPLICATION_FORM AS APPLICATION_FORM3
					ON APPLICATION_FORM2.applicant_id = APPLICATION_FORM3.applicant_id
				INNER JOIN PROGRAM
					ON APPLICATION_FORM3.program_id = PROGRAM.id
				INNER JOIN ADVERT
					ON PROGRAM.id = ADVERT.id
				INNER JOIN REGISTERED_USER
					ON ADVERT.registered_user_id = REGISTERED_USER.id
				LEFT JOIN PROGRAM_CLOSING_DATES
					ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
				WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
					AND APPLICATION_FORM.program_id != PROGRAM.id
					AND ADVERT.enabled = 1
					AND ADVERT.active = 1
					AND (PROGRAM_CLOSING_DATES.id IS NULL
						OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
					AND APPLICATION_FORM3.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
				GROUP BY ADVERT.id) AS RECOMMENDED_ADVERT_SECONDARY
			ON DUPLICATE KEY UPDATE RECOMMENDED_ADVERT.ranking = RECOMMENDED_ADVERT.ranking + RECOMMENDED_ADVERT_SECONDARY.ranking;
		
		INSERT INTO RECOMMENDED_ADVERT
			SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
				ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
				REGISTERED_USER2.firstname AS primary_supervisor_first_name, REGISTERED_USER2.lastname AS primary_supervisor_last_name,
				REGISTERED_USER2.email AS primary_supervisor_email, "PROJECT" AS advert_type,
				REGISTERED_USER3.firstname AS secondary_supervisor_first_name,
				REGISTERED_USER3.lastname AS secondary_supervisor_last_name,
				ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM2.id))), 2) AS ranking, ADVERT.last_edited_timestamp AS last_edited_timestamp
			FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_USER_ROLE
				ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
			INNER JOIN REGISTERED_USER
				ON APPLICATION_FORM_USER_ROLE.registered_user_id = REGISTERED_USER.id
			INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE2
				ON APPLICATION_FORM_USER_ROLE.registered_user_id = APPLICATION_FORM_USER_ROLE2.registered_user_id
			INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
				ON APPLICATION_FORM_USER_ROLE2.application_form_id = APPLICATION_FORM2.id
			INNER JOIN PROGRAM
				ON APPLICATION_FORM2.program_id = PROGRAM.id
			INNER JOIN PROJECT
				ON APPLICATION_FORM2.project_id = PROJECT.id
			INNER JOIN ADVERT
				ON PROJECT.id = ADVERT.id
			INNER JOIN REGISTERED_USER AS REGISTERED_USER2
				ON PROJECT.primary_supervisor_id = REGISTERED_USER2.id
			LEFT JOIN REGISTERED_USER AS REGISTERED_USER3
				ON PROJECT.secondary_supervisor_id = REGISTERED_USER3.id
			LEFT JOIN PROGRAM_CLOSING_DATES
				ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
			WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id IN ("APPROVER", "INTERVIEWER",
					"PROJECTADMINISTRATOR", "REVIEWER", "SUGGESTEDSUPERVISOR", "STATEADMINISTRATOR", "SUPERVISOR")
				AND REGISTERED_USER.enabled = 1
				AND (APPLICATION_FORM.project_id is NULL
					OR APPLICATION_FORM.project_id != PROJECT.id)
				AND ADVERT.enabled = 1
				AND ADVERT.active = 1
				AND (PROGRAM_CLOSING_DATES.id IS NULL
					OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
				AND APPLICATION_FORM2.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
			GROUP BY ADVERT.id;
			
		INSERT INTO RECOMMENDED_ADVERT
			SELECT *
			FROM (
				SELECT ADVERT.id AS id, ADVERT.title AS title, ADVERT.description AS description, ADVERT.study_duration AS study_duration,
					ADVERT.funding AS funding, PROGRAM.code AS program_code, PROGRAM_CLOSING_DATES.closing_date AS closing_date,
					REGISTERED_USER.firstname AS primary_supervisor_first_name, REGISTERED_USER.lastname AS primary_supervisor_last_name,
					REGISTERED_USER.email AS primary_supervisor_email, "PROJECT" AS advert_type,
					REGISTERED_USER2.firstname AS secondary_supervisor_first_name,
					REGISTERED_USER2.lastname AS secondary_supervisor_last_name,
					ROUND(SQRT(COUNT(DISTINCT(APPLICATION_FORM3.id))), 2) AS ranking, ADVERT.last_edited_timestamp AS last_edited_timestamp
				FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM AS APPLICATION_FORM2
					ON APPLICATION_FORM.program_id = APPLICATION_FORM2.program_id
				INNER JOIN APPLICATION_FORM AS APPLICATION_FORM3
					ON APPLICATION_FORM2.applicant_id = APPLICATION_FORM3.applicant_id
				INNER JOIN PROGRAM
					ON APPLICATION_FORM3.program_id = PROGRAM.id
				INNER JOIN PROJECT
					ON APPLICATION_FORM3.project_id = PROJECT.id
				INNER JOIN ADVERT
					ON PROJECT.id = ADVERT.id
				INNER JOIN REGISTERED_USER
					ON PROJECT.primary_supervisor_id = REGISTERED_USER.id
				LEFT JOIN REGISTERED_USER AS REGISTERED_USER2
					ON PROJECT.secondary_supervisor_id = REGISTERED_USER2.id
				LEFT JOIN PROGRAM_CLOSING_DATES
					ON PROGRAM.id = PROGRAM_CLOSING_DATES.program_id
				WHERE APPLICATION_FORM.applicant_id = in_registered_user_id
					AND (APPLICATION_FORM.project_id is NULL
						OR APPLICATION_FORM.project_id != PROJECT.id)
					AND ADVERT.enabled = 1
					AND ADVERT.active = 1
					AND (PROGRAM_CLOSING_DATES.id IS NULL
						OR PROGRAM_CLOSING_DATES.closing_date >= baseline_date)
					AND APPLICATION_FORM3.status NOT IN ("UNSUBMITTED", "WITHDRAWN")
				GROUP BY ADVERT.id) AS RECOMMENDED_ADVERT_SECONDARY
			ON DUPLICATE KEY UPDATE RECOMMENDED_ADVERT.ranking = RECOMMENDED_ADVERT.ranking + RECOMMENDED_ADVERT_SECONDARY.ranking;
			
		SET ranking_threshold = (
				SELECT ROUND(MAX(RECOMMENDED_ADVERT.ranking) * in_ranking_threshold)
				FROM RECOMMENDED_ADVERT);
				
		SELECT RECOMMENDED_ADVERT.id AS id, RECOMMENDED_ADVERT.title AS title, RECOMMENDED_ADVERT.description AS description,
			RECOMMENDED_ADVERT.study_duration AS studyDuration, RECOMMENDED_ADVERT.funding AS funding,
			RECOMMENDED_ADVERT.program_code AS programCode, RECOMMENDED_ADVERT.closing_date AS closingDate,
			RECOMMENDED_ADVERT.primary_supervisor_first_name AS primarySupervisorFirstName,
			RECOMMENDED_ADVERT.primary_supervisor_last_name AS primarySupervisorLastName,
			RECOMMENDED_ADVERT.primary_supervisor_email AS primarySupervisorEmail, RECOMMENDED_ADVERT.advert_type AS advertType,
			RECOMMENDED_ADVERT.secondary_supervisor_first_name AS secondarySupervisorFirstName,
			RECOMMENDED_ADVERT.secondary_supervisor_last_name AS secondarySupervisorLastName,
			RECOMMENDED_ADVERT.ranking
		FROM RECOMMENDED_ADVERT
		WHERE RECOMMENDED_ADVERT.ranking >= ranking_threshold
		ORDER BY RECOMMENDED_ADVERT.ranking DESC,
			RECOMMENDED_ADVERT.last_edited_timestamp;
			
		DROP TABLE RECOMMENDED_ADVERT;
		
		COMMIT;
	
END
;

DROP PROCEDURE SP_SELECT_USER_ACTIONS
;

CREATE PROCEDURE SP_SELECT_USER_ACTIONS (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_action_id VARCHAR(50), 
	IN in_action_type_id VARCHAR(50))
BEGIN
	
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		DROP TABLE USER_ACTION_LIST;
   		ROLLBACK;
	END;
	
	START TRANSACTION;
	
		CREATE TEMPORARY TABLE USER_ACTION_LIST (
			action_id VARCHAR(50) NOT NULL,
			action_type_id VARCHAR(50) NOT NULL,
			precedence INT(1) UNSIGNED NOT NULL,
			raises_urgent_flag INT(1) UNSIGNED NOT NULL DEFAULT 0,
			return_group INT(1) UNSIGNED NOT NULL DEFAULT 0,
			PRIMARY KEY (action_id),
			UNIQUE INDEX (action_type_id),
			INDEX (return_group, action_id)
		) ENGINE = MEMORY;

		INSERT INTO USER_ACTION_LIST (action_id, action_type_id, precedence, raises_urgent_flag, return_group)
			SELECT *
			FROM (
				SELECT APPLICATION_FORM_ACTION_REQUIRED.action_id AS action_id, ACTION.action_type_id AS action_type_id,
					ACTION.precedence AS precedence,
					MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag,
					MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) + 1 AS return_group
			 	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
				 	ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
				INNER JOIN ACTION
					ON APPLICATION_FORM_ACTION_REQUIRED.action_id = ACTION.id
			 	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
				 	AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
				 	AND (in_action_id IS NULL
				 		OR APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id)
				 	AND (in_action_type_id IS NULL
				 		OR ACTION.action_type_id = in_action_type_id)
			 	GROUP BY APPLICATION_FORM_ACTION_REQUIRED.action_id) AS ACTION_REQUIRED
		ON DUPLICATE KEY UPDATE
			USER_ACTION_LIST.action_id = IF (
				USER_ACTION_LIST.precedence < ACTION_REQUIRED.precedence,
				ACTION_REQUIRED.action_id,
				USER_ACTION_LIST.action_id),
			USER_ACTION_LIST.precedence = IF (
				USER_ACTION_LIST.precedence < ACTION_REQUIRED.precedence,
				ACTION_REQUIRED.precedence,
				USER_ACTION_LIST.precedence);

		INSERT INTO USER_ACTION_LIST (action_id, action_type_id, precedence)
			SELECT *
			FROM (
				SELECT APPLICATION_FORM_ACTION_OPTIONAL.action_id, ACTION.action_type_id, ACTION.precedence
			 	FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_FORM_USER_ROLE
					ON APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = APPLICATION_FORM_USER_ROLE.application_role_id
				INNER JOIN APPLICATION_FORM
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
				INNER JOIN ACTION
					ON APPLICATION_FORM_ACTION_OPTIONAL.action_id = ACTION.id
				WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
					AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
				 	AND APPLICATION_FORM_ACTION_OPTIONAL.state_id = APPLICATION_FORM.status
				AND (in_action_id IS NULL
				 		OR APPLICATION_FORM_ACTION_OPTIONAL.action_id = in_action_id)
				 	AND (in_action_type_id IS NULL
				 		OR ACTION.action_type_id = in_action_type_id)
			 	GROUP BY APPLICATION_FORM_ACTION_OPTIONAL.action_id) AS ACTION_OPTIONAL
		ON DUPLICATE KEY UPDATE
			USER_ACTION_LIST.action_id = IF (
				USER_ACTION_LIST.precedence < ACTION_OPTIONAL.precedence,
				ACTION_OPTIONAL.action_id,
				USER_ACTION_LIST.action_id),
			USER_ACTION_LIST.precedence = IF (
				USER_ACTION_LIST.precedence < ACTION_OPTIONAL.precedence,
				ACTION_OPTIONAL.precedence,
				USER_ACTION_LIST.precedence);

		SELECT action_id, raises_urgent_flag
		FROM USER_ACTION_LIST
		ORDER BY return_group DESC, action_id;

		DROP TABLE USER_ACTION_LIST;
		
		COMMIT;
		
END
;

DROP PROCEDURE SP_UPDATE_APPLICATION_FORM_DUE_DATE
;

CREATE PROCEDURE SP_UPDATE_APPLICATION_FORM_DUE_DATE (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_deadline_timestamp DATE)
BEGIN

	DECLARE in_raises_urgent_flag INT(1) UNSIGNED;

	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
   		ROLLBACK;
	END;
	
	START TRANSACTION;
	
		SET in_raises_urgent_flag = (
			SELECT IF (in_deadline_timestamp <= CURRENT_DATE(), 1, 0));

		UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		SET APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp = in_deadline_timestamp,
			APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = in_raises_urgent_flag,
			APPLICATION_FORM_USER_ROLE.raises_urgent_flag = in_raises_urgent_flag
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date = 1;
			
	COMMIT;
	
END
;
