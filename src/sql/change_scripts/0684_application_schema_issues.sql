
/* Some more information about state to make the workflow verifiable */

ALTER TABLE STATE
	ADD COLUMN is_initial_state INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER parent_state_id,
	ADD COLUMN is_final_state INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER is_initial_state,
	ADD INDEX (is_initial_state),
	ADD INDEX (is_final_state)
;

ALTER TABLE STATE
	MODIFY COLUMN is_initial_state INT(1) UNSIGNED NOT NULL,
	MODIFY COLUMN is_final_state INT(1) UNSIGNED NOT NULL
;

UPDATE STATE
SET is_initial_state = 1
WHERE id IN ("APPLICATION_UNSUBMITTED", "PROJECT_APPROVED", "PROGRAM_APPROVAL", 
	"PROGRAM_APPROVED", "INSTITUTION_APPROVED", "SYSTEM_APPROVED")
;

UPDATE STATE
SET is_final_state = 1
WHERE id IN ("APPLICATION_APPROVED_COMPLETED", "APPLICATION_REJECTED_COMPLETED", 
	"APPLICATION_WITHDRAWN_COMPLETED", "PROJECT_DISABLED_COMPLETED",
	"PROGRAM_DISABLED_COMPLETED", "INSTITUTION_APPROVED",
	"SYSTEM_APPROVED")
;

ALTER TABLE ROLE
	ADD COLUMN is_scope_owner INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER id
;

ALTER TABLE ROLE
	MODIFY COLUMN is_scope_owner INT(1) UNSIGNED NOT NULL
;

UPDATE ROLE
SET is_scope_owner = 1
WHERE id IN ("APPLICATION_CREATOR", "PROJECT_ADMINISTRATOR", "PROJECT_PRIMARY_SUPERVISOR", 
	"PROGRAM_ADMINISTRATOR", "INSTITUTION_ADMINISTRATOR", "SYSTEM_ADMINISTRATOR")
;

UPDATE ROLE_TRANSITION
SET role_transition_type = "REMOVE"
WHERE role_transition_type = "REJOIN"
;

INSERT INTO STATE(id, is_final_state, is_initial_state, parent_state_id, scope_id)
VALUES("APPLICATION_WITHDRAWN_PENDING_EXPORT", 0, 0, "APPLICATION_WITHDRAWN", "APPLICATION")
;

UPDATE APPLICATION
SET state_id = "APPLICATION_WITHDRAWN_PENDING_EXPORT"
WHERE state_id = "APPLICATION_WITHDRAWN"
;

ALTER TABLE STATE
	DROP INDEX scope_id,
	ADD UNIQUE INDEX (scope_id, sequence_order)
;