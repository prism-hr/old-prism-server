ALTER TABLE STATE_ACTION
	ADD COLUMN action_enhancement VARCHAR(50) AFTER is_default_action,
	ADD INDEX (action_enhancement)
;

ALTER TABLE STATE_ACTION_ASSIGNMENT
	ADD COLUMN action_enhancement VARCHAR(50) AFTER role_id,
	ADD INDEX (action_enhancement)
;

ALTER TABLE STATE_ACTION_ASSIGNMENT
	ADD COLUMN delegated_action_id VARCHAR(100) AFTER action_enhancement,
	ADD INDEX (delegated_action_id),
	ADD FOREIGN KEY (delegated_action_id) REFERENCES ACTION (id)
;

DROP TABLE STATE_ACTION_ENHANCEMENT
;

DELETE 
FROM ROLE_TRANSITION
;

DELETE
FROM STATE_TRANSITION_PROPAGATION
;

DELETE
FROM STATE_TRANSITION
;

DELETE
FROM STATE_ACTION_ASSIGNMENT
;

DELETE
FROM STATE_ACTION_NOTIFICATION
;

DELETE
FROM STATE_ACTION
;

DELETE 
FROM ACTION WHERE id LIKE "%_LIST"
	AND id NOT LIKE "SYSTEM%"
;

UPDATE STATE
SET sequence_order = 2
WHERE id = "INSTITUTION_APPROVED"
;

INSERT INTO STATE (id, parent_state_id, is_initial_state, is_final_state, sequence_order, scope_id)
VALUES ("INSTITUTION_APPROVAL", "INSTITUTION_APPROVAL", 0, 1, 1, "INSTITUTION")
;

UPDATE COMMENT
SET transition_state_id = "INSTITUTION_APPROVAL"
WHERE transition_state_id = "INSTITUTION_APPROVED"
;

INSERT INTO COMMENT (institution_id, user_id, role_id, action_id, declined_response, content, transition_state_id, created_timestamp)
	SELECT institution_id, user_id, role_id, action_id, declined_response, content, transition_state_id, created_timestamp
	FROM COMMENT
	WHERE transition_state_id = "INSTITUTION_APPROVAL"
;

INSERT INTO ACTION (id, action_type, action_category, do_save_comment, scope_id)
VALUES("INSTITUTION_COMPLETE_APPROVAL_STAGE", "USER_INVOCATION", "PROCESS_RESOURCE", 1, "INSTITUTION")
;

UPDATE COMMENT
SET action_id = "INSTITUTION_COMPLETE_APPROVAL_STAGE",
	transition_state_id = "INSTITUTION_APPROVED",
	content = "Institution approved"
WHERE id = LAST_INSERT_ID()
;

DELETE 
FROM ACTION_REDACTION
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE COMMENT
SET action_id = REPLACE(action_id, "_CONFIGURE", "_VIEW_EDIT")
;

UPDATE ACTION
SET id = REPLACE(id, "_CONFIGURE", "_VIEW_EDIT")
;

SET FOREIGN_KEY_CHECKS = 1
;

DELETE
FROM ACTION
WHERE id IN ("INSTITUTION_VIEW", "PROGRAM_VIEW", "PROJECT_VIEW")
;

CREATE TABLE STATE_GROUP (
	id VARCHAR(50) NOT NULL,
	sequence_order INT(1) UNSIGNED,
	scope_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (scope_id, sequence_order),
	FOREIGN KEY(scope_id) REFERENCES SCOPE (id)
) ENGINE = INNODB
;

INSERT INTO STATE_GROUP
	SELECT id, sequence_order, scope_id
	FROM STATE
	WHERE id = parent_state_id
;

ALTER TABLE STATE
	DROP FOREIGN KEY state_ibfk_1,
	CHANGE COLUMN parent_state_id state_group_id VARCHAR(50) NOT NULL,
	ADD FOREIGN KEY (state_group_id) REFERENCES STATE_GROUP (id),
	DROP COLUMN is_initial_state,
	DROP COLUMN is_final_state,
	DROP COLUMN sequence_order
;

UPDATE COMMENT
SET transition_state_id = "APPLICATION_WITHDRAWN_PENDING_EXPORT"
WHERE transition_state_id = "APPLICATION_WITHDRAWN"
;

UPDATE COMMENT
SET transition_state_id = "PROJECT_DISABLED_COMPLETED"
WHERE transition_state_id = "PROJECT_DISABLED"
;

DELETE
FROM STATE_DURATION
WHERE state_id IN ("PROGRAM_DISABLED", "PROJECT_DISABLED")
;

DELETE FROM STATE
WHERE id IN ("APPLICATION_WITHDRAWN", "PROGRAM_DISABLED", "PROJECT_DISABLED")
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE COMMENT
SET transition_state_id = "SYSTEM_RUNNING"
WHERE transition_state_id = "SYSTEM_APPROVED"
;

UPDATE STATE_GROUP
SET id = "SYSTEM_RUNNING"
WHERE id = "SYSTEM_APPROVED"
;

UPDATE STATE
SET id = "SYSTEM_RUNNING",
	state_group_id = "SYSTEM_RUNNING"
WHERE id = "SYSTEM_APPROVED"
;

UPDATE SYSTEM
SET state_id = "SYSTEM_RUNNING"
;

SET FOREIGN_KEY_CHECKS = 1
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type, code, name, enabled)
VALUES(5243, "GENDER", "F", "Female", 1),
	(5243, "GENDER", "M", "Male", 1),
	(5243, "GENDER", "N", "Indeterminate Gender", 1)
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD COLUMN gender_id INT(10) UNSIGNED AFTER title_id,
	ADD INDEX (gender_id),
	ADD FOREIGN KEY (gender_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE APPLICATION_PERSONAL_DETAIL
SET gender_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type = "GENDER"
	AND code = "F")
WHERE gender = "FEMALE"
;

UPDATE APPLICATION_PERSONAL_DETAIL
SET gender_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type = "GENDER"
	AND code = "M")
WHERE gender = "MALE"
;

UPDATE APPLICATION_PERSONAL_DETAIL
SET gender_id = (
	SELECT id
	FROM IMPORTED_ENTITY
	WHERE imported_entity_type = "GENDER"
	AND code = "N")
WHERE gender = "INDETERMINATE_GENDER"
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	MODIFY COLUMN gender_id INT(10) UNSIGNED NOT NULL,
	DROP COLUMN gender
;
