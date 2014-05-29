CREATE PROCEDURE DELETE_ROLE_ACTION (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50), 
	IN in_action_id VARCHAR(50))
BEGIN

DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		AND APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id;
	
	UPDATE APPLICATION_FORM_USER_ROLE LEFT JOIN (
		SELECT MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag,
			APPLICATION_FORM_USER_ROLE.id AS application_form_user_role_id
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		GROUP BY APPLICATION_FORM_USER_ROLE.id) AS OTHER_ACTION
	ON APPLICATION_FORM_USER_ROLE.id = OTHER_ACTION.application_form_user_role_id
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 		
		IF(OTHER_ACTION.raises_urgent_flag IS NOT NULL,
			OTHER_ACTION.raises_urgent_flag,
			0)
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		
END
;

CREATE PROCEDURE DELETE_USER_ACTION (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_application_role_id VARCHAR(50), 
	IN in_action_id VARCHAR(50))
BEGIN

	DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		AND APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id;
			DELETE APPLICATION_FORM_ACTION_REQUIRED.*
	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		AND APPLICATION_FORM_ACTION_REQUIRED.action_id = in_action_id;
		
	UPDATE APPLICATION_FORM_USER_ROLE LEFT JOIN (
		SELECT MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raises_urgent_flag,
			APPLICATION_FORM_USER_ROLE.id AS application_form_user_role_id
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
			AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id
		GROUP BY APPLICATION_FORM_USER_ROLE.id) AS OTHER_ACTION
	ON APPLICATION_FORM_USER_ROLE.id = OTHER_ACTION.application_form_user_role_id
	SET APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 		
		IF(OTHER_ACTION.raises_urgent_flag IS NOT NULL,
			OTHER_ACTION.raises_urgent_flag,
			0)
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		
END
;
