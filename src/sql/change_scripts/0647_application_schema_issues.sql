DROP TABLE APPLICATION_USER_ROLE
;

ALTER TABLE STATE
	ADD COLUMN parent_state_id VARCHAR(50) AFTER id,
	ADD INDEX (parent_state_id),
	ADD FOREIGN KEY (parent_state_id) REFERENCES STATE (id)
;

UPDATE STATE
SET parent_state_id = id
;

ALTER TABLE STATE
	MODIFY COLUMN parent_state_id VARCHAR(50) NOT NULL
;

INSERT INTO STATE
	SELECT "APPLICATION_REVIEW_PENDING_FEEDBACK", "APPLICATION_REVIEW", 0
		UNION
	SELECT "APPLICATION_REVIEW_PENDING_COMPLETION", "APPLICATION_REVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_AVAILABILITY", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_SCHEDULING", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_INTERVIEW", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_FEEDBACK", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_COMPLETION", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_APPROVAL_PENDING_FEEDBACK", "APPLICATION_APPROVAL", 0
		UNION
	SELECT "APPLICATION_APPROVAL_PENDING_COMPLETION", "APPLICATION_APPROVAL", 0
		UNION
	SELECT "APPLICATION_APPROVED_COMPLETED", "APPLICATION_APPROVED", 0
		UNION
	SELECT "APPLICATION_REJECTED_COMPLETED", "APPLICATION_REJECTED", 0
		UNION
	SELECT "APPLICATION_UNSUBMITTED_PENDING_COMPLETION", "APPLICATION_UNSUBMITTED", 0
		UNION
	SELECT "APPLICATION_VALIDATION_PENDING_COMPLETION", "APPLICATION_VALIDATION", 0
		UNION
	SELECT "APPLICATION_APPROVED_PENDING_EXPORT", "APPLICATION_APPROVED", 0
		UNION
	SELECT "APPLICATION_APPROVED_PENDING_CORRECTION", "APPLICATION_APPROVED", 0
		UNION
	SELECT "APPLICATION_REJECTED_PENDING_EXPORT", "APPLICATION_REJECTED", 0
		UNION
	SELECT "APPLICATION_REJECTED_PENDING_CORRECTION", "APPLICATION_REJECTED", 0
		UNION
	SELECT "APPLICATION_WITHDRAWN_PENDING_EXPORT", "APPLICATION_WITHDRAWN", 0
		UNION
	SELECT "APPLICATION_WITHDRAWN_PENDING_CORRECTION", "APPLICATION_WITHDRAWN", 0
		UNION
	SELECT "APPLICATION_WITHDRAWN_COMPLETED", "APPLICATION_WITHDRAWN", 0
		UNION
	SELECT "INSTITUTION_DISABLED", "INSTITUTION_DISABLED", 0
		UNION
	SELECT "APPLICATION_VALIDATION_PENDING_FEEDBACK", "APPLICATION_VALIDATION", 0
		UNION
	SELECT "APPLICATION_REVIEW_PENDING_FEEDBACK", "APPLICATION_REVIEW", 0
		UNION
	SELECT "APPLICATION_REVIEW_PENDING_COMPLETION", "APPLICATION_REVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_AVAILABILITY", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_SCHEDULING", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_INTERVIEW", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_FEEDBACK", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_INTERVIEW_PENDING_COMPLETION", "APPLICATION_INTERVIEW", 0
		UNION
	SELECT "APPLICATION_APPROVAL_PENDING_FEEDBACK", "APPLICATION_APPROVAL", 0
		UNION
	SELECT "APPLICATION_APPROVAL_PENDING_COMPLETION", "APPLICATION_APPROVAL", 0
		UNION
	SELECT "APPLICATION_APPROVED_COMPLETED", "APPLICATION_APPROVED", 0
		UNION
	SELECT "APPLICATION_REJECTED_COMPLETED", "APPLICATION_REJECTED", 0
;

CREATE TABLE STATE_DURATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	state_id VARCHAR(50) NOT NULL,
	expiry_duration INT(10) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, state_id),
	UNIQUE INDEX (institution_id, state_id),
	UNIQUE INDEX (program_id, state_id),
	INDEX (state_id),
	FOREIGN KEY (system_id) REFERENCES system (id),
	FOREIGN KEY (institution_id) REFERENCES institution (id),
	FOREIGN KEY (program_id) REFERENCES program (id),
	CONSTRAINT FOREIGN KEY (state_id) REFERENCES STATE (id)
) ENGINE = INNODB
	SELECT NULL AS id, 1 AS system_id, NULL AS institution_id, 
		NULL AS program_id, "APPLICATION_UNSUBMITTED" AS state_id, 2419200 AS expiry_duration
		UNION
	SELECT NULL, 1, NULL, NULL, "APPLICATION_REVIEW_PENDING_FEEDBACK", 604800
		UNION
	SELECT NULL, 1, NULL, NULL, "APPLICATION_INTERVIEW_PENDING_AVAILABILITY", 259200
		UNION
	SELECT NULL, 1, NULL, NULL, "APPLICATION_INTERVIEW_PENDING_FEEDBACK", 604800
		UNION
	SELECT NULL, 1, NULL, NULL, "APPLICATION_APPROVAL_PENDING_FEEDBACK", 604800
;

INSERT INTO STATE_DURATION
	SELECT NULL AS id, NULL AS system_id, 5243 AS institution_id, 
		NULL AS program_id, state_id, expiry_duration
	FROM STATE_DURATION
	WHERE system_id = 1
;

INSERT INTO STATE_DURATION
	SELECT NULL AS id, NULL AS system_id, NULL AS institution_id, 
		PROGRAM.id, STATE_DURATION.state_id, STATE_DURATION.expiry_duration
	FROM PROGRAM INNER JOIN STATE_DURATION
	WHERE STATE_DURATION.system_id = 1
;

DROP TABLE PROGRAM_STATE_DURATION
;

ALTER TABLE STATE
	DROP COLUMN completion_duration
;

UPDATE APPLICATION LEFT JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_CONFIRM_REJECTION"
	GROUP BY application_id, action_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_REJECTED_COMPLETED"
WHERE APPLICATION.state_id = "APPLICATION_REJECTED" 
	AND FOUND_APPLICATION.application_id IS NULL
;

UPDATE APPLICATION LEFT JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION"
	GROUP BY application_id, action_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_APPROVED_COMPLETED"
WHERE APPLICATION.state_id = "APPLICATION_APPROVED" 
	AND FOUND_APPLICATION.application_id IS NULL
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_PROVIDE_REVIEW"
		AND deadline_timestamp >= CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_REVIEW_PENDING_FEEDBACK"
WHERE APPLICATION.state_id = "APPLICATION_REVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_PROVIDE_REVIEW"
		OR action_id = "APPLICATION_COMPLETE_REVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR"
		AND deadline_timestamp < CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_REVIEW_PENDING_COMPLETION"
WHERE APPLICATION.state_id = "APPLICATION_REVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
		AND deadline_timestamp >= CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_AVAILABILITY"
WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY"
		OR action_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS"
		AND deadline_timestamp < CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_SCHEDULING"
WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT ACTION_REQUIRED.application_id
	FROM ACTION_REQUIRED INNER JOIN APPLICATION
		ON ACTION_REQUIRED.application_id = APPLICATION.id
	INNER JOIN INTERVIEW
		ON APPLICATION.latest_interview_id = INTERVIEW.id
	WHERE ACTION_REQUIRED.application_id IS NOT NULL
		AND ACTION_REQUIRED.action_id = "APPLICATION_PROVIDE_INTERVIEW_FEEDBACK"
		AND ACTION_REQUIRED.deadline_timestamp >= CURRENT_DATE()
		AND INTERVIEW.due_date >= CURRENT_DATE()
	GROUP BY ACTION_REQUIRED.application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_INTERVIEW"
WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT ACTION_REQUIRED.application_id
	FROM ACTION_REQUIRED INNER JOIN APPLICATION
		ON ACTION_REQUIRED.application_id = APPLICATION.id
	INNER JOIN INTERVIEW
		ON APPLICATION.latest_interview_id = INTERVIEW.id
	WHERE ACTION_REQUIRED.application_id IS NOT NULL
		AND ACTION_REQUIRED.action_id = "APPLICATION_PROVIDE_INTERVIEW_FEEDBACK"
		AND ACTION_REQUIRED.deadline_timestamp >= CURRENT_DATE()
		AND INTERVIEW.due_date < CURRENT_DATE()
	GROUP BY ACTION_REQUIRED.application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_FEEDBACK"
WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_PROVIDE_INTERVIEW_FEEDBACK"
		OR action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR"
		AND deadline_timestamp < CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_INTERVIEW_PENDING_COMPLETION"
WHERE APPLICATION.state_id = "APPLICATION_INTERVIEW"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_CONFIRM_PRIMARY_SUPERVISION"
		AND deadline_timestamp >= CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_APPROVAL_PENDING_FEEDBACK"
WHERE APPLICATION.state_id = "APPLICATION_APPROVAL"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_CONFIRM_PRIMARY_SUPERVISION"
		OR action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_ADMINISTRATOR"
		OR action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_APPROVER"
		AND deadline_timestamp < CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_APPROVAL_PENDING_COMPLETION"
WHERE APPLICATION.state_id = "APPLICATION_APPROVAL"
;

UPDATE APPLICATION INNER JOIN (
	SELECT application_id
	FROM ACTION_REQUIRED
	WHERE application_id IS NOT NULL
		AND action_id = "APPLICATION_CONFIRM_PRIMARY_SUPERVISION"
		OR action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_ADMINISTRATOR"
		OR action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_APPROVER"
		AND deadline_timestamp < CURRENT_DATE()
	GROUP BY application_id) AS FOUND_APPLICATION
	ON APPLICATION.id = FOUND_APPLICATION.application_id
SET APPLICATION.state_id = "APPLICATION_APPROVAL_PENDING_COMPLETION"
WHERE APPLICATION.state_id = "APPLICATION_APPROVAL"
;

UPDATE APPLICATION
SET state_id = "APPLICATION_WITHDRAWN_COMPLETED"
WHERE state_id = "APPLICATION_WITHDRAWN"
AND is_exported = 1
;

UPDATE APPLICATION INNER JOIN APPLICATION_TRANSFER
	ON APPLICATION.application_transfer_id = APPLICATION_TRANSFER.id
	AND APPLICATION_TRANSFER.application_transfer_state_id = "QUEUED_FOR_WEBSERVICE_CALL"
SET APPLICATION.state_id = REPLACE(APPLICATION.state_id, "_COMPLETED", "_PENDING_EXPORT")
WHERE APPLICATION.state_id LIKE "%APPROVED%"
		OR APPLICATION.state_id LIKE "%REJECTED%"
		OR APPLICATION.state_id LIKE "%WITHDRAWN%"
;

UPDATE APPLICATION INNER JOIN APPLICATION_TRANSFER
	ON APPLICATION.application_transfer_id = APPLICATION_TRANSFER.id
	AND APPLICATION_TRANSFER.application_transfer_state_id 
		NOT IN ("QUEUED_FOR_WEBSERVICE_CALL", "COMPLETED")
SET APPLICATION.state_id = REPLACE(APPLICATION.state_id, "_COMPLETED", "_PENDING_CORRECTION")
WHERE APPLICATION.state_id LIKE "%APPROVED%"
		OR APPLICATION.state_id LIKE "%REJECTED%"
		OR APPLICATION.state_id LIKE "%WITHDRAWN%"
;

ALTER TABLE APPLICATION
	DROP COLUMN is_exported,
	DROP FOREIGN KEY application_form_status_when_withdrawn_fk_idx,
	DROP COLUMN last_state_id,
	DROP FOREIGN KEY APPLICATION_FORM_ibfk_2,
	DROP COLUMN next_state_id
;

INSERT INTO ROLE
	SELECT "APPLICATION_VIEWER_REFEREE", 0, NULL
		UNION
	SELECT "APPLICATION_VIEWER_RECRUITER", 0, NULL
		UNION
	SELECT "APPLICATION_INTERVIEWEE", 0, "APPLICATION_APPLICANT"
		UNION
	SELECT "PROGRAM_PROJECT_CREATOR", 0, NULL
;

INSERT INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT APPLICATION.id, APPLICATION.user_id, "APPLICATION_INTERVIEWEE", ADVERT.user_id, INTERVIEW.created_date
	FROM APPLICATION INNER JOIN ADVERT
		ON APPLICATION.program_id = ADVERT.id
	INNER JOIN INTERVIEW
		ON APPLICATION.latest_interview_id = INTERVIEW.id
	INNER JOIN INTERVIEW_PARTICIPANT
		ON INTERVIEW.id = INTERVIEW_PARTICIPANT.interview_id
		AND APPLICATION.user_id = INTERVIEW_PARTICIPANT.user_id
	WHERE APPLICATION.state_id IN ("APPLICATION_INTERVIEW_PENDING_AVAILABILITY",
		"APPLICATION_INTERVIEW_PENDING_SCHEDULING")
		AND INTERVIEW_PARTICIPANT.responded = 0
		AND APPLICATION.project_id IS NULL
;

INSERT INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT APPLICATION.id, APPLICATION.user_id, "APPLICATION_INTERVIEWEE", ADVERT.user_id, INTERVIEW.created_date
	FROM APPLICATION INNER JOIN ADVERT
		ON APPLICATION.project_id = ADVERT.id
	INNER JOIN INTERVIEW
		ON APPLICATION.latest_interview_id = INTERVIEW.id
	INNER JOIN INTERVIEW_PARTICIPANT
		ON INTERVIEW.id = INTERVIEW_PARTICIPANT.interview_id
		AND APPLICATION.user_id = INTERVIEW_PARTICIPANT.user_id
	WHERE APPLICATION.state_id IN ("APPLICATION_INTERVIEW_PENDING_AVAILABILITY",
		"APPLICATION_INTERVIEW_PENDING_SCHEDULING")
		AND INTERVIEW_PARTICIPANT.responded = 0
;

DROP TABLE ACTION_REQUIRED
;

UPDATE USER_ROLE
SET role_id = "APPLICATION_VIEWER_REFEREE"
WHERE role_id = "APPLICATION_REFEREE_PREVIOUS"
;

UPDATE IGNORE USER_ROLE
SET role_id = "APPLICATION_VIEWER_RECRUITER"
WHERE role_id LIKE "%PREVIOUS"
;

UPDATE ACTION_OPTIONAL
SET role_id = "APPLICATION_VIEWER_REFEREE"
WHERE role_id = "APPLICATION_REFEREE_PREVIOUS"
;

UPDATE IGNORE ACTION_OPTIONAL
SET role_id = "APPLICATION_VIEWER_RECRUITER"
WHERE role_id LIKE "%PREVIOUS"
;

DELETE
FROM USER_ROLE
WHERE role_id LIKE "%PREVIOUS"
;

DELETE
FROM ACTION_OPTIONAL
WHERE role_id LIKE "%PREVIOUS"
;

UPDATE ROLE
SET role_on_expiry_id = "PROGRAM_PROJECT_CREATOR"
WHERE role_on_expiry_id = "PROGRAM_PRACTITIONER"
;

UPDATE ACTION_OPTIONAL
SET role_id = "PROGRAM_PROJECT_CREATOR"
WHERE role_id = "PROGRAM_PRACTITIONER"
;

UPDATE USER_ROLE
SET role_id = "PROGRAM_PROJECT_CREATOR"
WHERE role_id = "PROGRAM_PRACTITIONER"
;

UPDATE ROLE
SET role_on_expiry_id = "APPLICATION_APPLICANT"
WHERE id = "APPLICATION_INTERVIEWEE"
;

UPDATE ROLE
SET role_on_expiry_id = "APPLICATION_VIEWER_REFEREE"
WHERE id = "APPLICATION_REFEREE"
;

UPDATE ROLE
SET role_on_expiry_id = "APPLICATION_VIEWER_RECRUITER"
WHERE role_on_expiry_id LIKE "%PREVIOUS"
;

DELETE
FROM ROLE
WHERE id LIKE "%PREVIOUS"
;

INSERT INTO ROLE
	SELECT "INSTITUTION_PROGRAM_CREATOR", 0, NULL
		UNION
	SELECT "PROGRAM_CREATOR", 1, NULL
;

INSERT IGNORE INTO USER_ROLE (institution_id, user_id, role_id, requesting_user_id)
	SELECT 5243, user_id, "INSTITUTION_PROGRAM_CREATOR", 1024
	FROM USER_ROLE
	WHERE role_id IN ("PROGRAM_ADMINISTRATOR", "PROGRAM_APPROVER")
;

CREATE TABLE ROLE_INHERITANCE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	inherited_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, inherited_role_id),
	INDEX (inherited_role_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (inherited_role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
	SELECT NULL AS id, id AS role_id, "PROGRAM_PROJECT_CREATOR" AS inherited_role_id
	FROM ROLE
	WHERE id IN ("APPLICATION_ADMINISTRATOR", "APPLICATION_INTERVIEWER",
		"APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_SECONDARY_SUPERVISOR",
		"APPLICATION_REVIEWER", "PROJECT_ADMINISTRATOR", 
		"PROJECT_PRIMARY_SUPERVISOR", "PROJECT_SECONDARY_SUPERVISOR")
;


INSERT INTO ROLE_INHERITANCE (role_id, inherited_role_id)
VALUES ("PROGRAM_ADMINISTRATOR", "INSTITUTION_PROGRAM_CREATOR"),
	("PROGRAM_APPROVER", "INSTITUTION_PROGRAM_CREATOR")
;

CREATE TABLE STATE_TRANSITION_PROPAGATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	state_id VARCHAR(50) NOT NULL,
	propagated_state_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (state_id, propagated_state_id),
	INDEX (propagated_state_id),
	FOREIGN KEY (state_id) REFERENCES STATE (id),
	FOREIGN KEY (propagated_state_id) REFERENCES STATE (id)
) ENGINE = INNODB
	SELECT NULL AS id, "INSTITUTION_DISABLED" AS state_id, "PROGRAM_DISABLED" AS propagated_state_id
		UNION
	SELECT NULL, "INSTITUTION_DISABLED", "PROJECT_DISABLED"
		UNION
	SELECT NULL, "INSTITUTION_DISABLED", "APPLICATION_REJECTED_PENDING_EXPORT"
		UNION
	SELECT NULL, "PROGRAM_DISABLED", "PROJECT_DISABLED"
		UNION
	SELECT NULL, "PROGRAM_DISABLED", "APPLICATION_REJECTED_PENDING_EXPORT"
		UNION
	SELECT NULL, "PROJECT_DISABLED", "APPLICATION_REJECTED_PENDING_EXPORT"
;

DROP TABLE PROGRAM_STATE_TRANSITION
;

DROP TABLE STATE_TRANSITION
;

DROP TABLE STATE_TRANSITION_TYPE
;

CREATE TABLE STATE_TRANSITION_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "ALL_COMPLETED" AS id
		UNION
	SELECT "ONE_COMPLETED"
		UNION
	SELECT "DUE_DATE_EXPIRY"
;

CREATE TABLE STATE_TRANSITION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	action_id VARCHAR(100) NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	state_transition_type_id VARCHAR(50) NOT NULL,
	display_order INT(1) UNSIGNED NULL DEFAULT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (action_id, state_id, state_transition_type_id),
	INDEX (state_id),
	INDEX (state_transition_type_id),
	INDEX (display_order),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id),
	FOREIGN KEY (state_transition_type_id) REFERENCES STATE_TRANSITION_TYPE (id)
) ENGINE = INNODB
;

INSERT INTO ACTION
	SELECT "APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_REVIEW_STAGE", 10, "SYNDICATED"
		UNION
	SELECT "APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE", 10, "SYNDICATED"
		UNION
	SELECT "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR", "APPLICATION_COMPLETE_APPROVAL_STAGE", 10, "SYNDICATED"
		UNION
	SELECT "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER", "APPLICATION_COMPLETE_APPROVAL_STAGE", 11, "SYNDICATED"
;

DELETE 
FROM ACTION
WHERE id LIKE "%_PROGRAM_ADMINISTRATOR"
	OR id LIKE "%_PROGRAM_APPROVER"
	OR id LIKE "%_APPLICATION_ADMINISTRATOR"
;

UPDATE ACTION
SET precedence = precedence - 10
WHERE precedence >= 10
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE ACTION
SET id = "APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR"
WHERE id = "APPLICATION_MOVE_TO_DIFFERENT_STAGE"
;

UPDATE ACTION_OPTIONAL
SET action_id = "APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR"
WHERE action_id = "APPLICATION_MOVE_TO_DIFFERENT_STAGE"
;

SET FOREIGN_KEY_CHECKS = 1
;

UPDATE ACTION
SET notification_method_id = "SYNDICATED"
WHERE id IN ("PROGRAM_COMPLETE_APPROVAL_STAGE",
	"APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR")
;

INSERT INTO ACTION_TYPE (id)
	SELECT "APPLICATION_ASSESS_ELIGIBILITY"
		UNION
	SELECT "APPLICATION_EXPORT"
;

INSERT INTO ACTION
	SELECT "APPLICATION_ASSESS_ELIGIBILITY", "APPLICATION_ASSESS_ELIGIBILITY", 0, "SYNDICATED"
		UNION
	SELECT "APPLICATION_EXPORT", "APPLICATION_EXPORT", 0, NULL
		UNION
	SELECT "APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_APPROVER", "APPLICATION_MOVE_TO_DIFFERENT_STAGE", 1, "SYNDICATED"
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_INTERVIEW_STAGE', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_INTERVIEW_STAGE', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_INTERVIEW_STAGE', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_INTERVIEW_STAGE', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_REVIEW_STAGE', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_REVIEW_STAGE', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_REVIEW_STAGE', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_REVIEW_STAGE', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_VALIDATION_STAGE', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_VALIDATION_STAGE', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_VALIDATION_STAGE', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_VALIDATION_STAGE', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 4)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER', 'APPLICATION_APPROVED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_ADMINISTRATOR', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_APPROVER', 'APPLICATION_APPROVAL', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_APPROVER', 'APPLICATION_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_APPROVER', 'APPLICATION_REJECTED', 'ONE_COMPLETED', 4)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_APPROVER', 'APPLICATION_REVIEW', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_MOVE_TO_DIFFERENT_STAGE_AS_APPROVER', 'APPLICATION_APPROVED', 'ONE_COMPLETED', 3)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPLICATION', 'APPLICATION_VALIDATION', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_COMPLETE_APPLICATION', 'APPLICATION_UNSUBMITTED_PENDING_COMPLETION', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSESS_ELIGIBILITY', 'APPLICATION_VALIDATION_PENDING_FEEDBACK', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSESS_ELIGIBILITY', 'APPLICATION_VALIDATION_PENDING_COMPLETION', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_ELIGIBILITY', 'APPLICATION_VALIDATION_PENDING_COMPLETION', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROGRAM_COMPLETE_APPROVAL_STAGE', 'PROGRAM_APPROVED', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROGRAM_COMPLETE_APPROVAL_STAGE', 'PROGRAM_REJECTED', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSIGN_REVIEWERS', 'APPLICATION_REVIEW_PENDING_FEEDBACK', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_PROVIDE_REVIEW', 'APPLICATION_REVIEW_PENDING_COMPLETION', 'ALL_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_PROVIDE_REVIEW', 'APPLICATION_REVIEW_PENDING_COMPLETION', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSIGN_INTERVIEWERS', 'APPLICATION_INTERVIEW_PENDING_SCHEDULING', 'ONE_COMPLETED', 2)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSIGN_INTERVIEWERS', 'APPLICATION_INTERVIEW_PENDING_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSIGN_INTERVIEWERS', 'APPLICATION_INTERVIEW_PENDING_FEEDBACK', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY', 'APPLICATION_INTERVIEW_PENDING_SCHEDULING', 'ALL_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY', 'APPLICATION_INTERVIEW_PENDING_SCHEDULING', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS', 'APPLICATION_INTERVIEW_PENDING_INTERVIEW', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS', 'APPLICATION_INTERVIEW_PENDING_FEEDBACK', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_PROVIDE_INTERVIEW_FEEDBACK', 'APPLICATION_INTERVIEW_PENDING_COMPLETION', 'ALL_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_PROVIDE_INTERVIEW_FEEDBACK', 'APPLICATION_INTERVIEW_PENDING_COMPLETION', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROJECT_CONFIGURE', 'PROJECT_DEACTIVATED', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROJECT_CONFIGURE', 'PROJECT_DISABLED', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROJECT_CONFIGURE', 'PROJECT_DISABLED', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROGRAM_CONFIGURE', 'PROGRAM_DEACTIVATED', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROGRAM_CONFIGURE', 'PROGRAM_DISABLED', 'ONE_COMPLETED', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'PROGRAM_CONFIGURE', 'PROGRAM_DISABLED', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_ASSIGN_SUPERVISORS', 'APPLICATION_APPROVAL_PENDING_FEEDBACK', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_PRIMARY_SUPERVISION', 'APPLICATION_APPROVAL_PENDING_COMPLETION', 'ALL_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_PRIMARY_SUPERVISION', 'APPLICATION_APPROVAL_PENDING_COMPLETION', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_OFFER_RECOMMENDATION', 'APPLICATION_APPROVED_PENDING_EXPORT', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CORRECT_APPLICATION', 'APPLICATION_APPROVED_PENDING_EXPORT', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CONFIRM_REJECTION', 'APPLICATION_REJECTED_PENDING_EXPORT', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CORRECT_APPLICATION', 'APPLICATION_REJECTED_PENDING_EXPORT', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_EXPORT', 'APPLICATION_APPROVED_PENDING_CORRECTION', 'DUE_DATE_EXPIRY', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_EXPORT', 'APPLICATION_APPROVED_COMPLETED', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_EXPORT', 'APPLICATION_REJECTED_PENDING_CORRECTION', 'DUE_DATE_EXPIRY', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_EXPORT', 'APPLICATION_REJECTED_COMPLETED', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_CORRECT_APPLICATION', 'APPLICATION_WITHDRAWN_PENDING_EXPORT', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_EXPORT', 'APPLICATION_WITHDRAWN_PENDING_CORRECTION', 'DUE_DATE_EXPIRY', 1)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_EXPORT', 'APPLICATION_WITHDRAWN_COMPLETED', 'DUE_DATE_EXPIRY', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_WITHDRAW', 'APPLICATION_WITHDRAWN_PENDING_EXPORT', 'ONE_COMPLETED', 0)
;

INSERT INTO state_transition (id, action_id, state_id, state_transition_type_id, display_order)
VALUES (NULL, 'APPLICATION_WITHDRAW', 'APPLICATION_WITHDRAWN_COMPLETED', 'ONE_COMPLETED', 1)
;

CREATE TABLE ROLE_TRANSITION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	transition_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, action_id, transition_role_id),
	INDEX (action_id),
	INDEX (transition_role_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (transition_role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

INSERT INTO ROLE_TRANSITION (role_id, action_id, transition_role_id)
	SELECT "APPLICATION_ADMINISTRATOR", id, "APPLICATION_VIEWER_RECRUITER"
	FROM ACTION
	WHERE id LIKE "%COMPLETE%"
		AND id NOT IN ("APPLICATION_COMPLETE_APPLICATION",
			"APPLICATION_COMPLETE_VALIDATION_STAGE",
			"PROGRAM_COMPLETE_APPROVAL_STAGE")
;

INSERT INTO ROLE_TRANSITION (role_id, action_id, transition_role_id)
	SELECT "APPLICATION_REFEREE", id, "APPLICATION_VIEWER_REFEREE"
	FROM ACTION
	WHERE id IN ("APPLICATION_WITHDRAW", 
		"APPLICATION_CONFIRM_REJECTION",
		"APPLICATION_CONFIRM_OFFER_RECOMMENDATION")
;

INSERT INTO ROLE_TRANSITION (role_id, action_id, transition_role_id)
	SELECT "APPLICATION_REVIEWER", "APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_REVIEWER", "APPLICATION_WITHDRAW", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_INTERVIEWEE", "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS", "APPLICATION_APPLICANT"
		UNION
	SELECT "APPLICATION_INTERVIEWEE", "APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_APPLICANT"
		UNION
	SELECT "APPLICATION_INTERVIEWEE", "APPLICATION_WITHDRAW", "APPLICATION_APPLICANT"
		UNION
	SELECT "APPLICATION_INTERVIEWER", "APPLICATION_COMPLETE_INTERVIEW_STAGE", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_INTERVIEWER", "APPLICATION_WITHDRAW", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_WITHDRAW", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_SECONDARY_SUPERVISOR", "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_APPROVER", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_SECONDARY_SUPERVISOR", "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_ADMINISTRATOR", "APPLICATION_VIEWER_RECRUITER"
		UNION
	SELECT "APPLICATION_SECONDARY_SUPERVISOR", "APPLICATION_WITHDRAW", "APPLICATION_VIEWER_RECRUITER"
;

CREATE TABLE ROLE_TRANSITION_LINKED (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	state_transition_id INT(10) UNSIGNED NOT NULL,
	role_id VARCHAR(50) NOT NULL,
	transition_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (state_transition_id, role_id, transition_role_id),
	INDEX (role_id),
	INDEX (transition_role_id),
	FOREIGN KEY (state_transition_id) REFERENCES STATE_TRANSITION (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (transition_role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
	SELECT NULL AS id, STATE_TRANSITION.id AS state_transition_id, 
		ROLE_TRANSITION.role_id AS role_id,
		ROLE_TRANSITION.transition_role_id AS transition_role_id
	FROM STATE_TRANSITION INNER JOIN ROLE_TRANSITION
		ON STATE_TRANSITION.action_id = ROLE_TRANSITION.action_id
;

DROP TABLE ROLE_TRANSITION
;

RENAME TABLE ROLE_TRANSITION_LINKED TO ROLE_TRANSITION
;

INSERT INTO ROLE_TRANSITION
	SELECT NULL, id, "PROGRAM_CREATOR", "PROGRAM_ADMINISTRATOR"
	FROM STATE_TRANSITION
	WHERE action_id = "PROGRAM_COMPLETE_APPROVAL_STAGE"
		AND state_id = "PROGRAM_APPROVED"
;

ALTER TABLE ROLE
	DROP FOREIGN KEY role_ibfk_2,
	DROP COLUMN role_on_expiry_id
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE ROLE
SET id = "APPLICATION_CREATOR"
WHERE id = "APPLICATION_APPLICANT"
;

UPDATE ROLE_TRANSITION
SET transition_role_id = "APPLICATION_CREATOR"
WHERE transition_role_id = "APPLICATION_APPLICANT"
;

UPDATE USER_ROLE
SET role_id = "APPLICATION_CREATOR"
WHERE role_id = "APPLICATION_APPLICANT"
;

SET FOREIGN_KEY_CHECKS = 1
;

DELETE ROLE_TRANSITION.* 
FROM ROLE_TRANSITION INNER JOIN STATE_TRANSITION
	ON ROLE_TRANSITION.state_transition_id = STATE_TRANSITION.id
WHERE STATE_TRANSITION.action_id = "APPLICATION_WITHDRAW"
	AND STATE_TRANSITION.state_id = "APPLICATION_WITHDRAWN_COMPLETED"
;
