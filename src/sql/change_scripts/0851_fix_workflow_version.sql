ALTER TABLE INSTITUTION
	CHANGE COLUMN is_ucl_institution ucl_institution INT(1) UNSIGNED NOT NULL
;

UPDATE APPLICATION
SET workflow_property_configuration_version = (
	SELECT version
	FROM WORKFLOW_PROPERTY_CONFIGURATION
	WHERE active = 1
	AND system_id IS NOT NULL
	GROUP BY version)
WHERE workflow_property_configuration_version IS NULL 
;

DELETE FROM RESOURCE_STATE
WHERE state_id = "APPLICATION_REFERENCE"
AND application_id IN (
	SELECT APPLICATION.id
	FROM APPLICATION LEFT JOIN USER_ROLE
		ON APPLICATION.id = USER_ROLE.application_id
		AND USER_ROLE.role_id = "APPLICATION_REFEREE"
	WHERE USER_ROLE.role_id IS NULL
	GROUP BY APPLICATION.id)
;
