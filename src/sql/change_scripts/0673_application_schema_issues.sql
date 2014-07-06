ALTER TABLE ACTION
	DROP FOREIGN KEY action_ibfk_2,
	CHANGE COLUMN action_type_id action_type VARCHAR(50) NOT NULL AFTER id
;

DROP TABLE ACTION_TYPE
;

ALTER TABLE STATE_ACTION_ENHANCEMENT
	DROP FOREIGN KEY state_action_enhancement_ibfk_2,
	CHANGE COLUMN action_enhancement_type_id action_enhancement_type VARCHAR(50) NOT NULL
;

DROP TABLE ACTION_ENHANCEMENT_TYPE
;

ALTER TABLE ACTION_REDACTION
	DROP FOREIGN KEY action_redaction_ibfk_3,
	CHANGE COLUMN action_redaction_type_id action_redaction_type VARCHAR(50) NOT NULL
;

DROP TABLE ACTION_REDACTION_TYPE
;

ALTER TABLE CONFIGURATION
	DROP FOREIGN KEY configuration_ibfk_4,
	CHANGE COLUMN configuration_parameter_id configuration_parameter VARCHAR(50) NOT NULL
;

DROP TABLE CONFIGURATION_PARAMETER
;

ALTER TABLE IMPORTED_ENTITY
	DROP FOREIGN KEY imported_entity_ibfk_2,
	CHANGE COLUMN imported_entity_type_id imported_entity_type VARCHAR(50) NOT NULL
;

ALTER TABLE IMPORTED_ENTITY_FEED
	DROP FOREIGN KEY imported_entity_feed_ibfk_1,
	CHANGE COLUMN imported_entity_type_id imported_entity_type VARCHAR(50) NOT NULL
;

DROP TABLE IMPORTED_ENTITY_TYPE
;

ALTER TABLE NOTIFICATION_TEMPLATE
	DROP FOREIGN KEY notification_template_ibfk_2,
	CHANGE COLUMN notification_type_id notification_type VARCHAR(50) NOT NULL
;

DROP TABLE NOTIFICATION_TYPE
;

ALTER TABLE STATE_ACTION_ENHANCEMENT
	MODIFY COLUMN id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT FIRST
;

INSERT INTO STATE_ACTION_ENHANCEMENT (state_action_assignment_id, action_enhancement_type)
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_EDIT_EXPORT_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION_ENHANCEMENT
		ON STATE_ACTION_ASSIGNMENT.id = STATE_ACTION_ENHANCEMENT.state_action_assignment_id
	WHERE STATE_ACTION_ENHANCEMENT.action_enhancement_type = "APPLICATION_EDIT_REFERENCE_DATA"
;

INSERT INTO STATE_ACTION_ENHANCEMENT (state_action_assignment_id, action_enhancement_type)
	SELECT STATE_ACTION_ASSIGNMENT.id, "APPLICATION_VIEW_EXPORT_DATA"
	FROM STATE_ACTION_ASSIGNMENT INNER JOIN STATE_ACTION_ENHANCEMENT
		ON STATE_ACTION_ASSIGNMENT.id = STATE_ACTION_ENHANCEMENT.state_action_assignment_id
	WHERE STATE_ACTION_ENHANCEMENT.action_enhancement_type = "APPLICATION_VIEW_REFERENCE_DATA"
;

DELETE 
FROM NOTIFICATION_CONFIGURATION
WHERE notification_template_id = "APPLICATION_CORRECT_REQUEST"
;

DELETE 
FROM NOTIFICATION_TEMPLATE_VERSION
WHERE notification_template_id = "APPLICATION_CORRECT_REQUEST"
;

DELETE
FROM NOTIFICATION_TEMPLATE
WHERE id = "APPLICATION_CORRECT_REQUEST"
;

INSERT INTO ACTION (id, action_type, scope_id)
VALUES ("APPLICATION_CORRECT", "USER_INVOCATION", "APPLICATION")
;

INSERT INTO STATE_ACTION(state_id, action_id, raises_urgent_flag, is_default_action, notification_template_id)
	SELECT id, "APPLICATION_CORRECT", 1, 0, "APPLICATION_TASK_REQUEST"
	FROM STATE
	WHERE id LIKE "APPLICATION%"
		AND id LIKE "%PENDING_CORRECTION"
;

INSERT INTO STATE_ACTION_ASSIGNMENT
	SELECT NULL, STATE_ACTION.id, ROLE.id
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_CORRECT"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "INSTITUTION_ADMITTER")
;

INSERT INTO STATE_ACTION_NOTIFICATION
	SELECT NULL, STATE_ACTION.id, ROLE.id, "APPLICATION_UPDATE_NOTIFICATION"
	FROM STATE_ACTION INNER JOIN ROLE
	WHERE STATE_ACTION.action_id = "APPLICATION_CORRECT"
		AND ROLE.id IN ("INSTITUTION_ADMINISTRATOR", "INSTITUTION_ADMITTER")
;

INSERT INTO STATE_TRANSITION (state_action_id, transition_state_id, transition_action_id, do_post_comment)
	SELECT id, "APPLICATION_APPROVED_PENDING_EXPORT", "SYSTEM_VIEW_APPLICATION_LIST", 1
	FROM STATE_ACTION
	WHERE state_id = "APPLICATION_APPROVED_PENDING_CORRECTION"
		AND action_id = "APPLICATION_CORRECT"
;

INSERT INTO STATE_TRANSITION (state_action_id, transition_state_id, transition_action_id, do_post_comment)
	SELECT id, "APPLICATION_REJECTED_PENDING_EXPORT", "SYSTEM_VIEW_APPLICATION_LIST", 1
	FROM STATE_ACTION
	WHERE state_id = "APPLICATION_REJECTED_PENDING_CORRECTION"
		AND action_id = "APPLICATION_CORRECT"
;

INSERT INTO STATE_TRANSITION (state_action_id, transition_state_id, transition_action_id, do_post_comment)
	SELECT id, "APPLICATION_WITHDRAWN", "SYSTEM_VIEW_APPLICATION_LIST", 1
	FROM STATE_ACTION
	WHERE state_id = "APPLICATION_WITHDRAWN_PENDING_CORRECTION"
		AND action_id = "APPLICATION_CORRECT"
;

DELETE STATE_ACTION_ENHANCEMENT.*
FROM STATE_ACTION_ENHANCEMENT INNER JOIN STATE_ACTION_ASSIGNMENT
	ON STATE_ACTION_ENHANCEMENT.state_action_assignment_id = STATE_ACTION_ASSIGNMENT.id
WHERE STATE_ACTION_ENHANCEMENT.action_enhancement_type = "APPLICATION_EDIT_ALL_DATA"
	AND STATE_ACTION_ASSIGNMENT.role_id != "APPLICATION_CREATOR"
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE NOTIFICATION_CONFIGURATION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER"
;

DELETE 
FROM NOTIFICATION_CONFIGURATION
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE"
;

UPDATE NOTIFICATION_TEMPLATE_VERSION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER"
;

DELETE 
FROM NOTIFICATION_TEMPLATE_VERSION
WHERE notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE"
;

UPDATE STATE_ACTION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE notification_template_id IN ("APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER",
	"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER")
;

UPDATE STATE_ACTION_NOTIFICATION
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE notification_template_id IN ("APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER",
	"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER")
;

UPDATE USER_NOTIFICATION_INDIVIDUAL
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE notification_template_id IN ("APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER",
	"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER")
;

UPDATE USER_NOTIFICATION_SYNDICATED
SET notification_template_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE notification_template_id IN ("APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER",
	"APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER")
;

UPDATE NOTIFICATION_TEMPLATE
SET id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGMENTS_NOTIFICATION"
WHERE id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER"
;

DELETE 
FROM NOTIFICATION_TEMPLATE
WHERE id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWEE"
;

SET FOREIGN_KEY_CHECKS = 1
;