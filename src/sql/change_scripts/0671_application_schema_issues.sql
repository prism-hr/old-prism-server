/* Action visibility exclusion precedence not needed
 * Just implement all exclusions user has
 */

ALTER TABLE ACTION_VISIBILITY_EXCLUSION
	DROP COLUMN precedence
;

/* Missing configuration parameters at program level */

INSERT INTO CONFIGURATION (program_id, configuration_parameter_id, parameter_value)
	SELECT PROGRAM.id, CONFIGURATION.configuration_parameter_id, CONFIGURATION.parameter_value
	FROM PROGRAM INNER JOIN CONFIGURATION
	WHERE CONFIGURATION.system_id IS NOT NULL
;

/* Customisable email templates */

RENAME TABLE NOTIFICATION_REMINDER_INTERVAL TO NOTIFICATION_CONFIGURATION
;

ALTER TABLE NOTIFICATION_TEMPLATE_VERSION
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD COLUMN program_id INT(10) UNSIGNED AFTER institution_id,
	ADD INDEX (system_id),
	ADD INDEX (institution_id),
	ADD INDEX (program_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	ADD FOREIGN KEY (program_id) REFERENCES PROGRAM (id)
;

UPDATE NOTIFICATION_TEMPLATE_VERSION
SET system_id = 1
;

INSERT INTO NOTIFICATION_TEMPLATE_VERSION (institution_id, notification_template_id, subject, content, created_timestamp)
 	SELECT 5243, NOTIFICATION_TEMPLATE_VERSION.notification_template_id, NOTIFICATION_TEMPLATE_VERSION.subject,
 		NOTIFICATION_TEMPLATE_VERSION.content, NOTIFICATION_TEMPLATE_VERSION.created_timestamp
 	FROM NOTIFICATION_TEMPLATE_VERSION INNER JOIN (
		SELECT MAX(id) AS version_id
		FROM NOTIFICATION_TEMPLATE_VERSION
		GROUP BY notification_template_id) AS CURRENT_NOTIFICATION_TEMPLATE
	ON NOTIFICATION_TEMPLATE_VERSION.id = CURRENT_NOTIFICATION_TEMPLATE.version_id
;

INSERT INTO NOTIFICATION_TEMPLATE_VERSION (program_id, notification_template_id, subject, content, created_timestamp)
 	SELECT PROGRAM.id, NOTIFICATION_TEMPLATE_VERSION.notification_template_id, NOTIFICATION_TEMPLATE_VERSION.subject,
 		NOTIFICATION_TEMPLATE_VERSION.content, NOTIFICATION_TEMPLATE_VERSION.created_timestamp
 	FROM NOTIFICATION_TEMPLATE_VERSION INNER JOIN (
		SELECT MAX(id) AS version_id
		FROM NOTIFICATION_TEMPLATE_VERSION
		GROUP BY notification_template_id) AS CURRENT_NOTIFICATION_TEMPLATE
	ON NOTIFICATION_TEMPLATE_VERSION.id = CURRENT_NOTIFICATION_TEMPLATE.version_id
	INNER JOIN PROGRAM
;

DELETE 
FROM NOTIFICATION_TEMPLATE_VERSION
WHERE (notification_template_id LIKE "SYSTEM%"
	AND system_id IS NULL)
	OR (notification_template_id LIKE "INSTITUTION%"
		AND program_id IS NOT NULL)
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	ADD COLUMN notification_template_version_id INT(10) UNSIGNED AFTER notification_template_id,
	ADD INDEX (notification_template_version_id),
	ADD FOREIGN KEY (notification_template_version_id) REFERENCES NOTIFICATION_TEMPLATE_VERSION (id)
;

INSERT INTO NOTIFICATION_CONFIGURATION (system_id, institution_id, program_id, notification_template_id)
	SELECT 1, NULL, NULL, id
	FROM NOTIFICATION_TEMPLATE
	WHERE reminder_notification_template_id IS NULL
		UNION
	SELECT NULL, 5243, NULL, id
	FROM NOTIFICATION_TEMPLATE
	WHERE reminder_notification_template_id IS NULL
		UNION
	SELECT NULL, NULL, PROGRAM.id, NOTIFICATION_TEMPLATE.id
	FROM NOTIFICATION_TEMPLATE INNER JOIN PROGRAM
	WHERE NOTIFICATION_TEMPLATE.reminder_notification_template_id IS NULL
;

ALTER TABLE NOTIFICATION_TEMPLATE
	DROP FOREIGN KEY notification_template_ibfk_1,
	DROP COLUMN notification_template_version_id
;

UPDATE NOTIFICATION_CONFIGURATION INNER JOIN NOTIFICATION_TEMPLATE_VERSION
	ON NOTIFICATION_CONFIGURATION.system_id = NOTIFICATION_TEMPLATE_VERSION.system_id
	AND NOTIFICATION_CONFIGURATION.notification_template_id = NOTIFICATION_TEMPLATE_VERSION.notification_template_id
INNER JOIN (
	SELECT MAX(id) AS version_id
	FROM NOTIFICATION_TEMPLATE_VERSION
	WHERE system_id IS NOT NULL
	GROUP BY notification_template_id) AS CURRENT_NOTIFICATION_TEMPLATE
	ON NOTIFICATION_TEMPLATE_VERSION.id = CURRENT_NOTIFICATION_TEMPLATE.version_id
SET NOTIFICATION_CONFIGURATION.notification_template_version_id = CURRENT_NOTIFICATION_TEMPLATE.version_id
;

UPDATE NOTIFICATION_CONFIGURATION INNER JOIN NOTIFICATION_TEMPLATE_VERSION
	ON NOTIFICATION_CONFIGURATION.institution_id = NOTIFICATION_TEMPLATE_VERSION.institution_id
	AND NOTIFICATION_CONFIGURATION.notification_template_id = NOTIFICATION_TEMPLATE_VERSION.notification_template_id
INNER JOIN (
	SELECT MAX(id) AS version_id
	FROM NOTIFICATION_TEMPLATE_VERSION
	WHERE institution_id IS NOT NULL
	GROUP BY notification_template_id) AS CURRENT_NOTIFICATION_TEMPLATE
	ON NOTIFICATION_TEMPLATE_VERSION.id = CURRENT_NOTIFICATION_TEMPLATE.version_id
SET NOTIFICATION_CONFIGURATION.notification_template_version_id = CURRENT_NOTIFICATION_TEMPLATE.version_id
;

UPDATE NOTIFICATION_CONFIGURATION INNER JOIN NOTIFICATION_TEMPLATE_VERSION
	ON NOTIFICATION_CONFIGURATION.program_id = NOTIFICATION_TEMPLATE_VERSION.program_id
	AND NOTIFICATION_CONFIGURATION.notification_template_id = NOTIFICATION_TEMPLATE_VERSION.notification_template_id
INNER JOIN (
	SELECT program_id AS program_id,
		MAX(id) AS version_id
	FROM NOTIFICATION_TEMPLATE_VERSION
	WHERE program_id IS NOT NULL
	GROUP BY program_id, notification_template_id) AS CURRENT_NOTIFICATION_TEMPLATE
	ON NOTIFICATION_TEMPLATE_VERSION.program_id = CURRENT_NOTIFICATION_TEMPLATE.program_id
	AND NOTIFICATION_TEMPLATE_VERSION.id = CURRENT_NOTIFICATION_TEMPLATE.version_id
SET NOTIFICATION_CONFIGURATION.notification_template_version_id = CURRENT_NOTIFICATION_TEMPLATE.version_id
;

DELETE
FROM NOTIFICATION_CONFIGURATION
WHERE notification_template_version_id IS NULL
;

ALTER TABLE NOTIFICATION_CONFIGURATION
	CHANGE COLUMN day_interval day_reminder_interval INT(3) UNSIGNED
;

/* Updateable workflow configuration */


