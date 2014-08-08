ALTER TABLE COMMENT
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD INDEX (system_id),
	ADD INDEX (institution_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id)
;

INSERT INTO COMMENT (institution_id, user_id, role_id, action_id, declined_response, content, transition_state_id, created_timestamp)
VALUES (5243, 1024, "SYSTEM_ADMINISTRATOR", "SYSTEM_CREATE_INSTITUTION", 0, "New institution created", "INSTITUTION_APPROVED", "2012-10-01 00:09:00")
;
