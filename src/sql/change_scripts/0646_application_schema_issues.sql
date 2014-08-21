ALTER TABLE APPLICATION
	ADD COLUMN new_submitted_ip_address VARCHAR(32)
;

UPDATE APPLICATION
SET new_submitted_ip_address = INET_NTOA(CONV(HEX(submitted_ip_address),16,10))
;

ALTER TABLE APPLICATION
	DROP COLUMN submitted_ip_address,
	CHANGE COLUMN new_submitted_ip_address submitted_ip_address VARCHAR(32)
;

ALTER TABLE ROLE
	DROP FOREIGN KEY role_ibfk_3,
	DROP COLUMN update_scope_id
;

ALTER TABLE ACTION
	DROP FOREIGN KEY action_ibfk_3,
	DROP COLUMN update_scope_id
;

DROP TABLE APPLICATION_UPDATE_VIEW
;

DROP TABLE APPLICATION_UPDATE
;

DROP TABLE UPDATE_SCOPE
;

ALTER TABLE APPLICATION
	DROP COLUMN update_timestamp,
	DROP COLUMN display_range_timestamp
;

UPDATE USER_BATCH_NOTIFICATION
SET last_notification_timestamp = DATE(last_notification_timestamp)
;

ALTER TABLE USER_BATCH_NOTIFICATION
	MODIFY COLUMN last_notification_timestamp DATE NOT NULL
;
