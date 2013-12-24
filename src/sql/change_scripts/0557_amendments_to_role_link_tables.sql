DROP PROCEDURE DELETE_USER_FROM_ROLE
;

CREATE PROCEDURE DELETE_USER_FROM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
	   AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM_USER_ROLE
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		
	DELETE 
	FROM USER_ROLE_LINK
	WHERE registered_user_id = in_registered_user_id
		AND application_role_id = in_application_role_id;
		
END
;

DROP PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE
;

CREATE PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_program_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DECLARE retain_system_role INT(1) UNSIGNED DEFAULT 1;
	
	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.program_id = in_program_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE APPLICATION_FORM_USER_ROLE.*
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM.program_id = in_program_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;

	DELETE
	FROM USER_PROGRAM_ROLE_LINK
	WHERE registered_user_id = in_registered_user_id
		AND program_id = in_program_id
		AND application_role_id = in_application_role_id;
		
	SET retain_system_role = (
		SELECT IF(COUNT(id) > 0, 1, 0)
		FROM APPLICATION_FORM_USER_ROLE
		WHERE registered_user_id = in_registered_user_id
			AND application_role_id = in_application_role_id);
			
	IF retain_system_role = 0 THEN
		
		DELETE 
		FROM USER_ROLE_LINK
		WHERE registered_user_id = in_registered_user_id
			AND application_role_id = in_application_role_id;
		
	END IF;

END
;

CREATE TABLE USER_PROGRAM_ROLE_LINK (
	registered_user_id INT(10) UNSIGNED NOT NULL,
	program_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (registered_user_id, program_id, application_role_id),
	INDEX (program_id),
	INDEX (application_role_id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id))
ENGINE = INNODB
;

INSERT INTO USER_PROGRAM_ROLE_LINK (registered_user_id, program_id, application_role_id)
	SELECT APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM.program_id, APPLICATION_FORM_USER_ROLE.application_role_id
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE APPLICATION_FORM_USER_ROLE.application_role_id IN ("ADMINISTRATOR", "APPROVER", "VIEWER")
	GROUP BY APPLICATION_FORM_USER_ROLE.registered_user_id, APPLICATION_FORM.program_id, APPLICATION_FORM_USER_ROLE.application_role_id
;

DELETE FROM USER_ROLE_LINK
;

INSERT INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	SELECT registered_user_id, application_role_id
	FROM APPLICATION_FORM_USER_ROLE
	GROUP BY registered_user_id, application_role_id
;

ALTER TABLE application_role
	ADD COLUMN scope VARCHAR(50) NOT NULL DEFAULT "APPLICATION",
	ADD INDEX (scope)
;

UPDATE APPLICATION_ROLE
SET scope = "SYSTEM"
WHERE id IN ("SUPERADMINISTRATOR", "ADMITTER")
;

UPDATE APPLICATION_ROLE
SET scope = "PROGRAM"
WHERE id IN ("ADMINISTRATOR", "APPROVER", "VIEWER")
;

UPDATE APPLICATION_ROLE
SET scope = "PROJECT"
WHERE id = "PROJECTADMINISTRATOR"
;

UPDATE APPLICATION_ROLE
SET scope = "STATEADMINISTRATOR"
WHERE id = "STATE"
;

CREATE TRIGGER APPLICATION_FORM_USER_ROLE_INSERT 
	AFTER INSERT ON application_form_user_role 
	FOR EACH ROW 
BEGIN
	
	DECLARE do_insert_program_role INT(1) UNSIGNED DEFAULT 0;
	DECLARE application_form_program_id INT(10) UNSIGNED;

	INSERT IGNORE INTO USER_ROLE_LINK (registered_user_id, application_role_id)
	VALUES (NEW.registered_user_id, NEW.application_role_id);
	
	SET do_insert_program_role = (
		SELECT COUNT(id)
		FROM APPLICATION_ROLE
		WHERE id = NEW.application_role_id
		AND scope = "PROGRAM");
		
	IF do_insert_program_role = 1 THEN
	
		SET application_form_program_id = (
			SELECT program_id
			FROM APPLICATION_FORM
			WHERE id = NEW.application_form_id);

		INSERT IGNORE INTO USER_PROGRAM_ROLE_LINK (registered_user_id, program_id, application_role_id)
		VALUES (NEW.registered_user_id, application_form_program_id, NEW.application_role_id);
	
	END IF;
	
END
;

CREATE PROCEDURE DELETE_USER_FROM_PROJECT_ROLE (
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_project_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50))
BEGIN

	DECLARE retain_system_role INT(1) UNSIGNED DEFAULT 1;
	
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

	SET retain_system_role = (
		SELECT IF(COUNT(id) > 0, 1, 0)
		FROM APPLICATION_FORM_USER_ROLE
		WHERE registered_user_id = in_registered_user_id
			AND application_role_id = in_application_role_id);
			
	IF retain_system_role = 0 THEN
		
		DELETE 
		FROM USER_ROLE_LINK
		WHERE registered_user_id = in_registered_user_id
			AND application_role_id = in_application_role_id;
		
	END IF;

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