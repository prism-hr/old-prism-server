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
			IF (LATEST_UPDATE.raises_update_flag IS NOT NULL,
				LATEST_UPDATE.raises_update_flag,
				1), 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
				MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
				APPLICATION_FORM_USER_ROLE.application_form_id AS application_form_id, 
				APPLICATION_ROLE.update_visibility AS update_visibility
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
				IF (LATEST_UPDATE.raises_update_flag IS NOT NULL,
					LATEST_UPDATE.raises_update_flag,
					1), 0
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			INNER JOIN APPLICATION_ROLE
				ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
			LEFT JOIN (
				SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
					MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
					APPLICATION_FORM_USER_ROLE.application_form_id AS application_form_id, 
					APPLICATION_ROLE.update_visibility AS update_visibility
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
			IF (LATEST_UPDATE.raises_update_flag IS NOT NULL,
				LATEST_UPDATE.raises_update_flag,
				1), 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
				MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
				APPLICATION_FORM_USER_ROLE.application_form_id AS application_form_id, 
				APPLICATION_ROLE.update_visibility AS update_visibility
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
			IF (LATEST_UPDATE.raises_update_flag IS NOT NULL,
				LATEST_UPDATE.raises_update_flag,
				1), 0
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		INNER JOIN APPLICATION_ROLE
			ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
		LEFT JOIN (
			SELECT MAX(APPLICATION_FORM_USER_ROLE.update_timestamp) AS update_timestamp,
				MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag) AS raises_update_flag,
				APPLICATION_FORM_USER_ROLE.application_form_id AS application_form_id, 
				APPLICATION_ROLE.update_visibility AS update_visibility
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

DROP TABLE NOTIFICATION_RECORD
;

ALTER TABLE INTERVIEWER
	DROP COLUMN admins_notified_on,
	DROP COLUMN requires_admin_notification,
	DROP COLUMN first_admin_notification,
	DROP COLUMN last_notified
;

ALTER TABLE INTERVIEW_COMMENT
	DROP COLUMN admins_notified,
	DROP COLUMN old_admins_notified
;

ALTER TABLE REVIEWER
	DROP COLUMN admins_notified_on,
	DROP COLUMN requires_admin_notification,
	DROP COLUMN last_notified
;

ALTER TABLE REVIEW_COMMENT
	DROP COLUMN admins_notified
;

ALTER TABLE SUPERVISOR
	DROP COLUMN last_notified
;

ALTER TABLE COMMENT
	CHANGE COMMENT content TEXT,
	ADD COLUMN declined INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER content,
	ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT "GENERIC" AFTER declined,
	ADD INDEX (declined),
	ADD INDEX (type)
;

UPDATE COMMENT INNER JOIN ADMITTER_COMMENT
	ON COMMENT.id = ADMITTER_COMMENT.id
SET COMMENT.type = ADMITTER_COMMENT.comment_type
;

ALTER TABLE ADMITTER_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN APPROVAL_COMMENT
	ON COMMENT.id = APPROVAL_COMMENT.id
SET COMMENT.type = APPROVAL_COMMENT.comment_type
;

ALTER TABLE APPROVAL_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN INTERVIEW_COMMENT
	ON COMMENT.id = INTERVIEW_COMMENT.id
SET COMMENT.type = INTERVIEW_COMMENT.comment_type,
	COMMENT.declined = INTERVIEW_COMMENT.decline
;

ALTER TABLE INTERVIEW_COMMENT
	DROP COLUMN comment_type,
	DROP COLUMN decline
;

UPDATE COMMENT INNER JOIN STATECHANGE_COMMENT
	ON COMMENT.id = STATECHANGE_COMMENT.id
SET COMMENT.type = SUBSTRING_INDEX(STATECHANGE_COMMENT.comment_type, "_", 1)
;

UPDATE COMMENT INNER JOIN INTERVIEW_SCHEDULE_COMMENT
	ON COMMENT.id = INTERVIEW_SCHEDULE_COMMENT.id
SET COMMENT.type = INTERVIEW_SCHEDULE_COMMENT.comment_type
;

ALTER TABLE INTERVIEW_SCHEDULE_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN INTERVIEW_VOTE_COMMENT
	ON COMMENT.id = INTERVIEW_VOTE_COMMENT.id
SET COMMENT.type = INTERVIEW_VOTE_COMMENT.comment_type
;

ALTER TABLE INTERVIEW_VOTE_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN OFFER_RECOMMENDED_COMMENT
	ON COMMENT.id = OFFER_RECOMMENDED_COMMENT.id
SET COMMENT.type = OFFER_RECOMMENDED_COMMENT.comment_type
;

ALTER TABLE OFFER_RECOMMENDED_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN REFERENCE_COMMENT
	ON COMMENT.id = REFERENCE_COMMENT.id
SET COMMENT.type = REFERENCE_COMMENT.comment_type
;

ALTER TABLE REFERENCE_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN REQUEST_RESTART_COMMENT
	ON COMMENT.id = REQUEST_RESTART_COMMENT.id
SET COMMENT.type = REQUEST_RESTART_COMMENT.comment_type
;

ALTER TABLE REQUEST_RESTART_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN REVIEW_COMMENT
	ON COMMENT.id = REVIEW_COMMENT.id
SET COMMENT.type = REVIEW_COMMENT.comment_type,
	COMMENT.declined = REVIEW_COMMENT.decline
;

ALTER TABLE REVIEW_COMMENT
	DROP COLUMN comment_type,
	DROP COLUMN decline
;

UPDATE COMMENT INNER JOIN STATE_CHANGE_SUGGESTION_COMMENT
	ON COMMENT.id = STATE_CHANGE_SUGGESTION_COMMENT.id
SET COMMENT.type = STATE_CHANGE_SUGGESTION_COMMENT.comment_type
;

ALTER TABLE STATE_CHANGE_SUGGESTION_COMMENT
	DROP COLUMN comment_type
;

UPDATE COMMENT INNER JOIN SUPERVISION_CONFIRMATION_COMMENT
	ON COMMENT.id = SUPERVISION_CONFIRMATION_COMMENT.id
SET COMMENT.type = SUPERVISION_CONFIRMATION_COMMENT.comment_type
;

ALTER TABLE SUPERVISION_CONFIRMATION_COMMENT
	DROP COLUMN comment_type
;

INSERT INTO COMMENT (application_form_id, content, declined, type, user_id, created_timestamp)
	SELECT APPLICATION_FORM_REFEREE.application_form_id, NULL, APPLICATION_FORM_REFEREE.declined, 
		"REFERENCE", APPLICATION_FORM_REFEREE.registered_user_id, EVENT.event_date
	FROM APPLICATION_FORM_REFEREE INNER JOIN REFERENCE_EVENT
		ON APPLICATION_FORM_REFEREE.id = REFERENCE_EVENT.referee_id
	INNER JOIN EVENT
		ON REFERENCE_EVENT.id = EVENT.id
	WHERE APPLICATION_FORM_REFEREE.declined = 1
	GROUP BY APPLICATION_FORM_REFEREE.id
;

ALTER TABLE APPLICATION_FORM_REFEREE
	DROP COLUMN declined
;

CREATE TABLE COMMENT_PROPERTY_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

INSERT INTO COMMENT_PROPERTY_TYPE (id)
VALUES ("ACADMEMICALLYQUALIFIED"),
	("LINGUISTICALLYQUALIFIED"),
	("FEESTATUS"),
	("PRIMARYSUPERVISOR"),
	("SECONDARYSUPERVISOR"),
	("PROJECTDESCRIPTIONAVAILABLE"),
	("PROJECTTITLE"),
	("PROJECTABSTRACT"),
	("RECOMMENDEDOFFERCONDITIONSAVAILABLE"),
	("RECOMMENDEDSTARTDATE"),
	("RECOMMENDEDOFFERCONDITIONS"),
	("WILLINGTOINTERVIEW"),
	("WILLINGTOSUPERVISE"),
	("SUITABLEFORINSTITUTION"),
	("SUITABLEFORPROGRAMME"),
	("APPLICANTRATING"),
	("INTERVIEWEEINSTRUCTIONS"),
	("INTERVIEWERINSTRUCTIONS"),
	("INTERVIEWLOCATION"),
	("PROPOSEDINTERVIEWDATETIME"),
	("CONFIRMEDINTERVIEWDATETIME"),
	("PROPOSEDINTERVIEWDATETIMEAVAILABILITY"),
	("REASONFORREJECTION"),
	("PROPOSEDNEXTSTATUS"),
	("CONFIRMEDNEXTSTATUS"),
	("DELEGATEADMINISTRATOR"),
	("SECONDSINREVIEWSTATE"),
	("SECONDSININTERVIEWSTATE"),
	("SECONDSINAPPROVALSTATE")
;

CREATE TABLE COMMENT_PROPERTY_VARCHAR (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_property_type_id VARCHAR(50) NOT NULL,
	content VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_property_type_id),
	INDEX (value_varchar),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_property_type_id) REFERENCES COMMENT_PROPERTY_TYPE (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_PROPERTY_INTEGER (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_property_type_id VARCHAR(50) NOT NULL,
	content INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_property_type_id),
	INDEX (content),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_property_type_id) REFERENCES COMMENT_PROPERTY_TYPE (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_PROPERTY_BOOLEAN (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_property_type_id VARCHAR(50) NOT NULL,
	content INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_property_type_id),
	INDEX (content),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_property_type_id) REFERENCES COMMENT_PROPERTY_TYPE (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_PROPERTY_DECIMAL (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_property_type_id VARCHAR(50) NOT NULL,
	content DECIMAL (3,2) NOT NULL,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_property_type_id),
	INDEX (content),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_property_type_id) REFERENCES COMMENT_PROPERTY_TYPE (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_PROPERTY_DATETIME (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_property_type_id VARCHAR(50) NOT NULL,
	content DATETIME NOT NULL,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_property_type_id),
	INDEX (content),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_property_type_id) REFERENCES COMMENT_PROPERTY_TYPE (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_PROPERTY_TEXT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	comment_property_type_id VARCHAR(50) NOT NULL,
	content TEXT NOT NULL,
	PRIMARY KEY (id),
	INDEX (comment_id),
	INDEX (comment_property_type_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (comment_property_type_id) REFERENCES COMMENT_PROPERTY_TYPE (id)
) ENGINE = INNODB
;