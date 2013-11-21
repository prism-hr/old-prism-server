ALTER TABLE APPLICATION_FORM_USER_ROLE
	ADD COLUMN assigned_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ADD INDEX (assigned_timestamp)
;

ALTER TABLE APPLICATION_FORM_ACTION_REQUIRED
	ADD COLUMN assigned_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ADD INDEX (assigned_timestamp)
;