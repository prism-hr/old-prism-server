UPDATE ACTION
SET precedence = 2
WHERE id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_APPROVER"
;

UPDATE ACTION
SET precedence = 1
WHERE id = "APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_ADMINISTRATOR"
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE ACTION
SET id = CONCAT(id, "_AS_PROGRAM_ADMINISTRATOR"),
	precedence = 1
WHERE id IN ("APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE")
;

UPDATE APPLICATION_ACTION_OPTIONAL
SET action_id = CONCAT(action_id, "_AS_PROGRAM_ADMINISTRATOR")
WHERE action_id IN ("APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE")
;

UPDATE APPLICATION_ACTION_REQUIRED
SET action_id = CONCAT(action_id, "_AS_PROGRAM_ADMINISTRATOR")
WHERE action_id IN ("APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE")
;

UPDATE STATE_TRANSITION
SET action_id = CONCAT(action_id, "_AS_PROGRAM_ADMINISTRATOR")
WHERE action_id IN ("APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE")
;

UPDATE USER
SET action_id = CONCAT(action_id, "_AS_PROGRAM_ADMINISTRATOR")
WHERE action_id IN ("APPLICATION_COMPLETE_REVIEW_STAGE", "APPLICATION_COMPLETE_INTERVIEW_STAGE")
;

SET FOREIGN_KEY_CHECKS = 0
;

INSERT INTO ACTION (id, action_type_id, precedence, notification_method_id, update_scope_id, scope_id)
	SELECT REPLACE(id, "PROGRAM_", "APPLICATION_"), action_type_id, 0, notification_method_id, 
		update_scope_id, "APPLICATION"
	FROM ACTION
	WHERE id IN("APPLICATION_COMPLETE_REVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR", 
		"APPLICATION_COMPLETE_INTERVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR", 
		"APPLICATION_COMPLETE_APPROVAL_STAGE_AS_PROGRAM_ADMINISTRATOR")
;

INSERT INTO STATE_TRANSITION
	SELECT REPLACE(action_id, "_PROGRAM", "_APPLICATION"), state_id, display_order, "SUGGESTION"
	FROM STATE_TRANSITION
	WHERE action_id LIKE "%PROGRAM_ADMINISTRATOR"
;

ALTER TABLE ACTION_TYPE
	ADD COLUMN scope_id VARCHAR(50),
	ADD INDEX (scope_id),
	ADD FOREIGN KEY (scope_id) REFERENCES SCOPE (id)
;

UPDATE ACTION_TYPE
SET scope_id = "APPLICATION"
;

SET FOREIGN_KEY_CHECKS = 0
;
	
UPDATE ACTION_TYPE
SET id = CONCAT("APPLICATION_", id)
;

UPDATE ACTION
SET action_type_id = CONCAT("APPLICATION_", action_type_id)
;
	
SET FOREIGN_KEY_CHECKS = 1
;