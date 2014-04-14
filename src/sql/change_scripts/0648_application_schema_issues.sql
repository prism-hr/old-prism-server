RENAME TABLE NOTIFICATION_TEMPLATE TO NOTIFICATION_TEMPLATE_VERSION
;

RENAME TABLE NOTIFICATION_TEMPLATE_TYPE TO NOTIFICATION_TEMPLATE
;

ALTER TABLE NOTIFICATION_TEMPLATE
	ADD COLUMN new_id VARCHAR(100),
	ADD COLUMN notification_template_version_id INT(10) UNSIGNED,
	ADD COLUMN reminder_notification_template_id VARCHAR(100),
	ADD COLUMN reminder_interval INT(10) UNSIGNED,
	ADD INDEX (notification_template_version_id),
	ADD INDEX (reminder_notification_template_id),
	ADD INDEX (reminder_interval),
	ADD FOREIGN KEY (notification_template_version_id) REFERENCES NOTIFICATION_TEMPLATE_VERSION (id)
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_COMPLETE_APPLICATION_NOTIFICATION"
WHERE id = "APPLICATION_SUBMIT_CONFIRMATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_TASK_REQUEST_REMINDER"
WHERE id = "DIGEST_TASK_REMINDER"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_TASK_REQUEST",
	reminder_notification_template_id = "APPLICATION_TASK_REQUEST_REMINDER",
	reminder_interval = 259200
WHERE id = "DIGEST_TASK_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_UPDATE_NOTIFICATION"
WHERE id = "DIGEST_UPDATE_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_EXPORT_ERROR_NOTIFICATION"
WHERE id = "EXPORT_ERROR"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "SYSTEM_IMPORT_ERROR_NOTIFICATION"
WHERE id = "IMPORT_ERROR"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION"
WHERE id = "INTERVIEW_VOTE_CONFIRMATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER"
WHERE id = "INTERVIEW_VOTE_REMINDER"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_NOTIFICATION",
	reminder_notification_template_id = "APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST_REMINDER",
	reminder_interval = 86400
WHERE id = "INTERVIEW_VOTE_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION"
WHERE id = "MOVED_TO_APPROVED_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_APPLICANT"
WHERE id = "MOVED_TO_INTERVIEW_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION_INTERVIEWER"
WHERE id = "INTERVIEWER_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "SYSTEM_PASSWORD_NOTIFICATION"
WHERE id = "NEW_PASSWORD_CONFIRMATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "SYSTEM_REGISTRATION_REQUEST"
WHERE id = "NEW_USER_SUGGESTION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "PROGRAM_TASK_REQUEST"
WHERE id = "OPPORTUNITY_REQUEST_NOTIFICATION"
;

INSERT INTO NOTIFICATION_TEMPLATE
	SELECT "PROGRAM_TASK_REQUEST_REMINDER", "PROGRAM_TASK_REQUEST_REMINDER", NULL, NULL, NULL
;

UPDATE NOTIFICATION_TEMPLATE
SET reminder_notification_template_id = "PROGRAM_TASK_REQUEST_REMINDER",
	reminder_interval = 259200
WHERE new_id = "PROGRAM_TASK_REQUEST"
;

ALTER TABLE NOTIFICATION_TEMPLATE_VERSION
	DROP FOREIGN KEY notification_template_version_ibfk_3,
	DROP COLUMN parent_notification_template_id,
	DROP FOREIGN KEY notification_template_version_ibfk_1,
	CHANGE COLUMN notification_template_type_id notification_template_id VARCHAR(100) NOT NULL,
	ADD FOREIGN KEY (notification_template_id) REFERENCES NOTIFICATION_TEMPLATE (id)
;

INSERT INTO NOTIFICATION_TEMPLATE_VERSION
	SELECT NULL, "PROGRAM_TASK_REQUEST_REMINDER", content, null, CONCAT("REMINDER: ", subject)
	FROM NOTIFICATION_TEMPLATE_VERSION
	WHERE notification_template_id = "OPPORTUNITY_REQUEST_NOTIFICATION"
	ORDER BY id DESC
	LIMIT 0, 1
;

UPDATE NOTIFICATION_TEMPLATE INNER JOIN (
	SELECT MAX(id) AS version_id,
		notification_template_id AS template_id
	FROM NOTIFICATION_TEMPLATE_VERSION
	GROUP BY notification_template_id) AS ACTIVE
	ON NOTIFICATION_TEMPLATE.id = ACTIVE.template_id
SET NOTIFICATION_TEMPLATE.notification_template_version_id = ACTIVE.version_id
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION"
WHERE id = "OPPORTUNITY_REQUEST_OUTCOME"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER"
WHERE id = "REFEREE_REMINDER"
;

DELETE
FROM SYSTEM_CONFIGURATION
WHERE id LIKE "%FREQUENCY%"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_PROVIDE_REFERENCE_REQUEST",
	reminder_notification_template_id = "APPLICATION_PROVIDE_REFERENCE_REQUEST_REMINDER",
	reminder_interval = 604800
WHERE id = "REFEREE_NOTIFICATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "SYSTEM_COMPLETE_REGISTRATION_REQUEST"
WHERE id = "REGISTRATION_CONFIRMATION"
;

UPDATE NOTIFICATION_TEMPLATE
SET new_id = "APPLICATION_CONFIRM_REJECTION_NOTIFICATION"
WHERE id = "REJECTED_NOTIFICATION"
;

SET FOREIGN_KEY_CHECKS = 0
;

ALTER TABLE NOTIFICATION_TEMPLATE
	
	MODIFY COLUMN id VARCHAR(100) NOT NULL
;

UPDATE NOTIFICATION_TEMPLATE_VERSION INNER JOIN NOTIFICATION_TEMPLATE
	ON NOTIFICATION_TEMPLATE_VERSION.notification_template_id = NOTIFICATION_TEMPLATE.id
SET NOTIFICATION_TEMPLATE_VERSION.notification_template_id = NOTIFICATION_TEMPLATE.new_id
;

UPDATE NOTIFICATION_TEMPLATE
SET id = new_id
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE ACTION
	DROP FOREIGN KEY action_ibfk_2,
	DROP COLUMN notification_method_id
;

DROP TABLE NOTIFICATION_METHOD
;

RENAME TABLE USER_BATCH_NOTIFICATION TO USER_NOTIFICATION
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE USER_NOTIFICATION
SET notification_purpose_id = "APPLICATION_TASK_REQUEST"
WHERE notification_purpose_id = "APPLICATION_TASK"
;

UPDATE USER_NOTIFICATION
SET notification_purpose_id = "APPLICATION_UPDATE_NOTIFICATION"
WHERE notification_purpose_id = "APPLICATION_UPDATE"
;

UPDATE USER_NOTIFICATION
SET notification_purpose_id = "PROGRAM_TASK_REQUEST"
WHERE notification_purpose_id = "PROGRAM_TASK"
;

ALTER TABLE USER_NOTIFICATION
	DROP FOREIGN KEY user_notification_ibfk_3,
	CHANGE COLUMN notification_purpose_id notification_template_id VARCHAR(100) NOT NULL,
	ADD FOREIGN KEY (notification_template_id) REFERENCES NOTIFICATION_TEMPLATE (id)
;

SET FOREIGN_KEY_CHECKS = 1
;

DROP TABLE NOTIFICATION_PURPOSE
;

ALTER TABLE NOTIFICATION_TEMPLATE
	DROP COLUMN new_id
;

CREATE TABLE NOTIFICATION_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "REQUEST" AS id
		UNION
	SELECT "NOTIFICATION"
;

CREATE TABLE ACTION_N0TIFICATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	action_permitted_id INT(10) UNSIGNED NOT NULL,
	notification_template_id VARCHAR(100) NOT NULL,
	notification_type_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (action_permitted_id, notification_template_id, notification_type_id),
	INDEX (notification_template_id),
	INDEX (notification_type_id),
	FOREIGN KEY (action_permitted_id) REFERENCES ACTION_OPTIONAL (id),
	FOREIGN KEY (notification_template_id) REFERENCES NOTIFICATION_TEMPLATE (id),
	FOREIGN KEY (notification_type_id) REFERENCES NOTIFICATION_TYPE (id)
) ENGINE = INNODB
;
