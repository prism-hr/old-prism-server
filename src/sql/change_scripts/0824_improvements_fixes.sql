ALTER TABLE STATE_GROUP
	DROP COLUMN parallelizable
;

ALTER TABLE STATE
	ADD COLUMN parallelizable INT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER state_duration_evaluation,
	ADD INDEX (parallelizable)
;

ALTER TABLE STATE
	MODIFY COLUMN parallelizable INT(1) UNSIGNED NOT NULL
;
