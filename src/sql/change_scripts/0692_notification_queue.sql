DELETE 
FROM USER_NOTIFICATION_SYNDICATED
WHERE notification_template_id LIKE "%_UPDATE_%"
;

RENAME TABLE USER_NOTIFICATION_INDIVIDUAL TO USER_NOTIFICATION
;

ALTER TABLE USER_NOTIFICATION
	CHANGE COLUMN last_notification_timestamp created_timestamp DATETIME NOT NULL
;

INSERT INTO USER_NOTIFICATION (user_role_id, notification_template_id, created_timestamp)
	SELECT USER_ROLE.id, STATE_ACTION.notification_template_id, USER_NOTIFICATION_SYNDICATED.last_notification_timestamp
	FROM USER_NOTIFICATION_SYNDICATED INNER JOIN STATE_ACTION
		ON USER_NOTIFICATION_SYNDICATED.notification_template_id = STATE_ACTION.notification_template_id
	INNER JOIN APPLICATION
		ON STATE_ACTION.state_id = APPLICATION.state_id
	INNER JOIN STATE_ACTION_ASSIGNMENT
		ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
	INNER JOIN USER_ROLE
		ON (USER_ROLE.application_id = APPLICATION.id
			OR USER_ROLE.project_id = APPLICATION.project_id
			OR USER_ROLE.program_id = APPLICATION.program_id
			OR USER_ROLE.institution_id = APPLICATION.institution_id
			OR USER_ROLE.system_id = APPLICATION.system_id)
		AND USER_ROLE.user_id = USER_NOTIFICATION_SYNDICATED.user_id
		AND USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
	GROUP BY USER_ROLE.id, STATE_ACTION.notification_template_id
;

INSERT INTO USER_NOTIFICATION (user_role_id, notification_template_id, created_timestamp)
	SELECT USER_ROLE.id, STATE_ACTION.notification_template_id, USER_NOTIFICATION_SYNDICATED.last_notification_timestamp
	FROM USER_NOTIFICATION_SYNDICATED INNER JOIN STATE_ACTION
		ON USER_NOTIFICATION_SYNDICATED.notification_template_id = STATE_ACTION.notification_template_id
	INNER JOIN PROJECT
		ON STATE_ACTION.state_id = PROJECT.state_id
	INNER JOIN STATE_ACTION_ASSIGNMENT
		ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
	INNER JOIN USER_ROLE
		ON (USER_ROLE.project_id = PROJECT.id
			OR USER_ROLE.program_id = PROJECT.program_id
			OR USER_ROLE.institution_id = PROJECT.institution_id
			OR USER_ROLE.system_id = PROJECT.system_id)
		AND USER_ROLE.user_id = USER_NOTIFICATION_SYNDICATED.user_id
		AND USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
	GROUP BY USER_ROLE.id, STATE_ACTION.notification_template_id
;

INSERT INTO USER_NOTIFICATION (user_role_id, notification_template_id, created_timestamp)
	SELECT USER_ROLE.id, STATE_ACTION.notification_template_id, USER_NOTIFICATION_SYNDICATED.last_notification_timestamp
	FROM USER_NOTIFICATION_SYNDICATED INNER JOIN STATE_ACTION
		ON USER_NOTIFICATION_SYNDICATED.notification_template_id = STATE_ACTION.notification_template_id
	INNER JOIN PROGRAM
		ON STATE_ACTION.state_id = PROGRAM.state_id
	INNER JOIN STATE_ACTION_ASSIGNMENT
		ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
	INNER JOIN USER_ROLE
		ON (USER_ROLE.program_id = PROGRAM.id
			OR USER_ROLE.institution_id = PROGRAM.institution_id
			OR USER_ROLE.system_id = PROGRAM.system_id)
		AND USER_ROLE.user_id = USER_NOTIFICATION_SYNDICATED.user_id
		AND USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
	GROUP BY USER_ROLE.id, STATE_ACTION.notification_template_id
;

INSERT INTO USER_NOTIFICATION (user_role_id, notification_template_id, created_timestamp)
	SELECT USER_ROLE.id, STATE_ACTION.notification_template_id, USER_NOTIFICATION_SYNDICATED.last_notification_timestamp
	FROM USER_NOTIFICATION_SYNDICATED INNER JOIN STATE_ACTION
		ON USER_NOTIFICATION_SYNDICATED.notification_template_id = STATE_ACTION.notification_template_id
	INNER JOIN INSTITUTION
		ON STATE_ACTION.state_id = INSTITUTION.state_id
	INNER JOIN STATE_ACTION_ASSIGNMENT
		ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
	INNER JOIN USER_ROLE
		ON (USER_ROLE.institution_id = INSTITUTION.id
			OR USER_ROLE.system_id = INSTITUTION.system_id)
		AND USER_ROLE.user_id = USER_NOTIFICATION_SYNDICATED.user_id
		AND USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
	GROUP BY USER_ROLE.id, STATE_ACTION.notification_template_id
;

INSERT INTO USER_NOTIFICATION (user_role_id, notification_template_id, created_timestamp)
	SELECT USER_ROLE.id, STATE_ACTION.notification_template_id, USER_NOTIFICATION_SYNDICATED.last_notification_timestamp
	FROM USER_NOTIFICATION_SYNDICATED INNER JOIN STATE_ACTION
		ON USER_NOTIFICATION_SYNDICATED.notification_template_id = STATE_ACTION.notification_template_id
	INNER JOIN SYSTEM
		ON STATE_ACTION.state_id = SYSTEM.state_id
	INNER JOIN STATE_ACTION_ASSIGNMENT
		ON STATE_ACTION.id = STATE_ACTION_ASSIGNMENT.state_action_id
	INNER JOIN USER_ROLE
		ON USER_ROLE.system_id = SYSTEM.id
		AND USER_ROLE.user_id = USER_NOTIFICATION_SYNDICATED.user_id
		AND USER_ROLE.role_id = STATE_ACTION_ASSIGNMENT.role_id
	GROUP BY USER_ROLE.id, STATE_ACTION.notification_template_id
;

DROP TABLE USER_NOTIFICATION_SYNDICATED
;
