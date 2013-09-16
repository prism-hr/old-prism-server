ALTER TABLE pending_role_notification DROP FOREIGN KEY role_pending_not_fk
;

ALTER TABLE user_role_link DROP FOREIGN KEY user_role_role_fk
;

ALTER TABLE pending_role_notification MODIFY role_id VARCHAR(50) NOT NULL
;

ALTER TABLE user_role_link MODIFY application_role_id VARCHAR(50) NOT NULL
;

UPDATE pending_role_notification INNER JOIN application_role
	ON pending_role_notification.role_id = application_role.id
	SET pending_role_notification.role_id = application_role.authority
;

UPDATE user_role_link INNER JOIN application_role
	ON user_role_link.application_role_id = application_role.id
SET user_role_link.application_role_id = application_role.authority
;

ALTER TABLE application_role MODIFY id VARCHAR(50) NOT NULL
;

UPDATE application_role
SET application_role.id = application_role.authority
;

ALTER TABLE application_role DROP COLUMN authority
;

ALTER TABLE pending_role_notification ADD FOREIGN KEY (role_id) REFERENCES application_role (id)
;

ALTER TABLE user_role_link ADD FOREIGN KEY (application_role_id) REFERENCES application_role (id)
;

