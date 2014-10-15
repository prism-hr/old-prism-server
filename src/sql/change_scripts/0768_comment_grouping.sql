ALTER TABLE ACTION
	ADD COLUMN emphasized_action INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER declinable_action
;

ALTER TABLE ACTION
	MODIFY COLUMN emphasized_action INT(1) UNSIGNED NOT NULL
;

UPDATE COMMENT
SET transition_state_id = state_id
WHERE transition_state_id IS NULL
;
