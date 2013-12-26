INSERT INTO APPLICATION_ROLE (id, update_visibility, do_send_update_notification)
VALUES ("SAFETYNET", 0, 0)
;

ALTER TABLE APPLICATION_ROLE
	ADD COLUMN do_send_role_notification INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (do_send_role_notification)
;

UPDATE APPLICATION_ROLE
SET do_send_role_notification = 1
WHERE id IN ("SUPERADMINISTRATOR", "ADMITTER", "ADMINISTRATOR", "APPROVER", "VIEWER")
;

DELETE FROM USER_ROLE_LINK
;

INSERT INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	SELECT registered_user_id, application_role_id
	FROM APPLICATION_FORM_USER_ROLE
	GROUP BY registered_user_id, application_role_id
;

INSERT INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	SELECT registered_user_id, "SAFETYNET"
	FROM USER_ROLE_LINK
	GROUP BY registered_user_id
;

CREATE PROCEDURE DELETE_USER_FROM_PROJECT_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_project_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN
	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.project_id = in_project_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.project_id = in_project_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

END
;

CREATE PROCEDURE UPDATE_USER_PROJECT_ROLE (
	IN in_new_registered_user_id INT(10) UNSIGNED, 
	IN in_old_registered_user_id INT(10) UNSIGNED, 
	IN in_project_id INT(10) UNSIGNED, 
	IN application_role_id VARCHAR(50))
BEGIN
	
	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	SET APPLICATION_FORM_USER_ROLE.registered_user_id = in_new_registered_user_id,
		APPLICATION_FORM_USER_ROLE.update_timestamp = CURRENT_TIMESTAMP(),
		APPLICATION_FORM_USER_ROLE.raises_update_flag = 1
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_old_registered_user_id
		AND APPLICATION_FORM.project_id = in_project_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
	
	INSERT IGNORE INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	VALUES (NEW.registered_user_id, NEW.application_role_id);
	
	CALL DELETE_USER_FROM_PROJECT_ROLE(in_old_registered_user_id, in_project_id, in_application_role_id);
	
END
;