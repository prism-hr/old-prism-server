ALTER TABLE APPLICATION_FORM
	ADD COLUMN status_when_withdrawn VARCHAR (50) AFTER next_status,
	ADD INDEX (status_when_withdrawn),
	ADD CONSTRAINT application_form_status_when_withdrawn_fk_idx FOREIGN KEY (status_when_withdrawn) REFERENCES STATE (ID)
;

CREATE TABLE LAST_STATE_BEFORE_WITHDRAWN (
	application_form_id INT(10) UNSIGNED NOT NULL,
	last_state_id VARCHAR(50) NOT NULL,
	timestamp_of_entry_to_state DATETIME NOT NULL,
	PRIMARY KEY (application_form_id),
	INDEX (timestamp_of_entry_to_state))
ENGINE = INNODB
;

INSERT INTO LAST_STATE_BEFORE_WITHDRAWN
	SELECT id, "UNSUBMITTED", app_date_time
	FROM APPLICATION_FORM
	WHERE withdrawn_before_submit = 1
;

INSERT INTO LAST_STATE_BEFORE_WITHDRAWN
	SELECT id, "VALIDATION", 
		IF(submitted_on_timestamp IS NOT NULL,
			submitted_on_timestamp,
			app_date_time)
	FROM APPLICATION_FORM
	WHERE status = "WITHDRAWN" 
		AND withdrawn_before_submit = 0
		AND latest_review_round_id IS NULL
		AND latest_interview_id IS NULL
		AND latest_approval_round_id IS NULL
;

INSERT INTO LAST_STATE_BEFORE_WITHDRAWN
	SELECT APPLICATION_FORM.id, "REVIEW", 
		REVIEW_ROUND.created_date
	FROM APPLICATION_FORM INNER JOIN REVIEW_ROUND
		ON APPLICATION_FORM.latest_review_round_id = REVIEW_ROUND.id
	WHERE status = "WITHDRAWN" 
;

INSERT INTO LAST_STATE_BEFORE_WITHDRAWN
	SELECT APPLICATION_FORM.id, "INTERVIEW", INTERVIEW.created_date
	FROM APPLICATION_FORM INNER JOIN INTERVIEW
		ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
	LEFT JOIN LAST_STATE_BEFORE_WITHDRAWN
		ON APPLICATION_FORM.id = LAST_STATE_BEFORE_WITHDRAWN.application_form_id
	WHERE status = "WITHDRAWN"
	AND LAST_STATE_BEFORE_WITHDRAWN.application_form_id IS NULL
;

UPDATE APPLICATION_FORM INNER JOIN INTERVIEW
	ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
INNER JOIN LAST_STATE_BEFORE_WITHDRAWN
	ON APPLICATION_FORM.id = LAST_STATE_BEFORE_WITHDRAWN.application_form_id
SET LAST_STATE_BEFORE_WITHDRAWN.last_state_id = "INTERVIEW",
	LAST_STATE_BEFORE_WITHDRAWN.timestamp_of_entry_to_state = INTERVIEW.created_date
WHERE INTERVIEW.created_date > LAST_STATE_BEFORE_WITHDRAWN.timestamp_of_entry_to_state
;

INSERT INTO LAST_STATE_BEFORE_WITHDRAWN
	SELECT APPLICATION_FORM.id, "APPROVAL", APPROVAL_ROUND.created_date
	FROM APPLICATION_FORM INNER JOIN APPROVAL_ROUND
		ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
	LEFT JOIN LAST_STATE_BEFORE_WITHDRAWN
		ON APPLICATION_FORM.id = LAST_STATE_BEFORE_WITHDRAWN.application_form_id
	WHERE status = "WITHDRAWN"
	AND LAST_STATE_BEFORE_WITHDRAWN.application_form_id IS NULL
;

UPDATE APPLICATION_FORM INNER JOIN APPROVAL_ROUND
	ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
INNER JOIN LAST_STATE_BEFORE_WITHDRAWN
	ON APPLICATION_FORM.id = LAST_STATE_BEFORE_WITHDRAWN.application_form_id
SET LAST_STATE_BEFORE_WITHDRAWN.last_state_id = "APPROVAL",
	LAST_STATE_BEFORE_WITHDRAWN.timestamp_of_entry_to_state = APPROVAL_ROUND.created_date
WHERE APPROVAL_ROUND.created_date > LAST_STATE_BEFORE_WITHDRAWN.timestamp_of_entry_to_state
;

UPDATE APPLICATION_FORM INNER JOIN LAST_STATE_BEFORE_WITHDRAWN
	ON APPLICATION_FORM.id = LAST_STATE_BEFORE_WITHDRAWN.application_form_id
SET APPLICATION_FORM.status_when_withdrawn = LAST_STATE_BEFORE_WITHDRAWN.last_state_id
;

DROP TABLE LAST_STATE_BEFORE_WITHDRAWN
;

ALTER TABLE APPLICATION_FORM
	DROP COLUMN withdrawn_before_submit
;

CREATE PROCEDURE INSERT_USER_IN_ROLE (
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
