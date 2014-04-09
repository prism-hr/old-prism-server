ALTER TABLE STATE
	ADD COLUMN duration INT(10) UNSIGNED,
	ADD COLUMN can_be_assigned_to INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (can_be_assigned_to),
	ADD COLUMN can_be_assigned_from INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (can_be_assigned_from),
	ADD COLUMN is_submitted INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (is_submitted),
	ADD COLUMN is_modifiable INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (is_modifiable),
	ADD COLUMN is_completed INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (is_completed)
;

UPDATE STATE
SET duration = 60 * 60 * 24 * 7 * 4,
	can_be_assigned_to = 0,
	can_be_assigned_from = 0,
	is_modifiable = 1
WHERE id = "UNSUBMITTED"
;

UPDATE STATE
SET duration = 60 * 60 * 24 * 3,
	can_be_assigned_to = 0,
	can_be_assigned_from = 1,
	is_submitted = 1,
	is_modifiable = 1
WHERE id = "VALIDATION"
;

UPDATE STATE
SET duration = 60 * 60 * 24 * 7,
	can_be_assigned_to = 1,
	can_be_assigned_from = 1,
	is_submitted = 1,
	is_modifiable = 1
WHERE id = "REVIEW"
;

UPDATE STATE
SET duration = 60 * 60 * 24 * 7 * 2,
	can_be_assigned_to = 1,
	can_be_assigned_from = 1,
	is_submitted = 1,
	is_modifiable = 1
WHERE id = "INTERVIEW"
;

UPDATE STATE
SET duration = 60 * 60 * 24 * 7,
	can_be_assigned_to = 1,
	can_be_assigned_from = 1,
	is_submitted = 1
WHERE id = "APPROVAL"
;

UPDATE STATE
SET can_be_assigned_to = 1,
	can_be_assigned_from = 1,
	is_submitted = 1,
	is_completed = 1
WHERE id = "APPROVED"
;

UPDATE STATE
SET can_be_assigned_to = 1,
	can_be_assigned_from = 1,
	is_submitted = 1,
	is_completed = 1
WHERE id = "REJECTED"
;

UPDATE STATE
SET is_submitted = 1,
	is_completed = 1
WHERE id = "WITHDRAWN"
;
	
DROP TABLE STAGE_DURATION
;

ALTER TABLE STATE
	MODIFY can_be_assigned_to INT(1) UNSIGNED NOT NULL,
	MODIFY can_be_assigned_from INT(1) UNSIGNED NOT NULL,
	MODIFY is_submitted INT(1) UNSIGNED NOT NULL,
	MODIFY is_modifiable INT(1) UNSIGNED NOT NULL,
	MODIFY is_completed INT(1) UNSIGNED NOT NULL
;

ALTER TABLE STATE
	ADD COLUMN is_under_consideration INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (is_under_consideration)
;

UPDATE STATE
SET is_under_consideration = 1
WHERE id IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
;

ALTER TABLE STATE
	MODIFY is_under_consideration INT(1) UNSIGNED NOT NULL
;

