ALTER TABLE USER_ACCOUNT
	CHANGE COLUMN temporary_password_expiry_datetime temporary_password_expiry_timestamp DATETIME
;

ALTER TABLE APPLICATION
	ADD INDEX (user_id, sequence_identifier)
;

ALTER TABLE 

