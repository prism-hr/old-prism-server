ALTER TABLE ADVERT
	ADD COLUMN state_id VARCHAR(50),
	ADD INDEX (state_id),
	ADD FOREIGN KEY (state_id) REFERENCES STATE (id)
;

UPDATE ADVERT
SET state_id = CONCAT(advert_type, "_DISABLED")
WHERE enabled = 0
;

UPDATE ADVERT
SET state_id = CONCAT(advert_type, "_DEACTIVATED")
WHERE enabled = 1
	AND active = 0
;
	
UPDATE ADVERT
SET state_id = CONCAT(advert_type, "_APPROVED")
WHERE state_id IS NULL
;

ALTER TABLE ADVERT
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL,
	DROP COLUMN enabled,
	DROP COLUMN active
;

ALTER TABLE SYSTEM
	ADD COLUMN state_id VARCHAR(50),
	ADD INDEX (state_id),
	ADD FOREIGN KEY (state_id) REFERENCES STATE (id)
;

UPDATE SYSTEM
SET state_id = "SYSTEM_APPROVED"
;

ALTER TABLE SYSTEM
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL
;

ALTER TABLE INSTITUTION
	ADD COLUMN state_id VARCHAR(50),
	ADD INDEX (state_id),
	ADD FOREIGN KEY (state_id) REFERENCES STATE (id)
;

UPDATE INSTITUTION
SET state_id = IF(enabled = 1, "INSTITUTION_APPROVED", "INSTITUTION_DISABLED")
;

ALTER TABLE INSTITUTION
	MODIFY COLUMN state_id VARCHAR(50) NOT NULL,
	DROP COLUMN enabled
;

INSERT INTO ROLE 
	SELECT "PROGRAM_PRACTITIONER", 0, "INTERNAL", NULL
;

INSERT INTO ACTION_OPTIONAL (role_id, state_id, action_id)
	SELECT "SYSTEM_ADMINISTRATOR", "SYSTEM_APPROVED", "SYSTEM_CONFIGURE"
		UNION
	SELECT "SYSTEM_ADMINISTRATOR", "INSTITUTION_APPROVED", "INSTITUTION_CONFIGURE"
		UNION
	SELECT "SYSTEM_ADMINISTRATOR", "PROGRAM_APPROVED", "PROGRAM_CONFIGURE"	
		UNION
	SELECT "SYSTEM_ADMINISTRATOR", "PROGRAM_DEACTIVATED", "PROGRAM_CONFIGURE"
		UNION	
	SELECT "SYSTEM_ADMINISTRATOR", "PROGRAM_APPROVED", "PROGRAM_CREATE_PROJECT"	
		UNION
	SELECT "SYSTEM_ADMINISTRATOR", "PROGRAM_DEACTIVATED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "SYSTEM_ADMINISTRATOR", "PROJECT_APPROVED", "PROJECT_CONFIGURE"
		UNION
	SELECT "SYSTEM_ADMINISTRATOR", "PROJECT_DEACTIVATED", "PROJECT_CONFIGURE"
		UNION
	SELECT "INSTITUTION_ADMITTER", "INSTITUTION_APPROVED", "INSTITUTION_CONFIGURE"
		UNION
	SELECT "PROGRAM_ADMINISTRATOR", "PROGRAM_APPROVED", "PROGRAM_CONFIGURE"
		UNION
	SELECT "PROGRAM_ADMINISTRATOR", "PROGRAM_DEACTIVATED", "PROGRAM_CONFIGURE"
		UNION
	SELECT "PROGRAM_ADMINISTRATOR", "PROGRAM_APPROVED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "PROGRAM_ADMINISTRATOR", "PROGRAM_DEACTIVATED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "PROGRAM_APPROVER", "PROGRAM_APPROVED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "PROGRAM_APPROVER", "PROGRAM_DEACTIVATED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "PROGRAM_PRACTITIONER", "PROGRAM_APPROVED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "PROGRAM_PRACTITIONER", "PROGRAM_DEACTIVATED", "PROGRAM_CREATE_PROJECT"
		UNION
	SELECT "PROGRAM_ADMINISTRATOR", "PROJECT_APPROVED", "PROJECT_CONFIGURE"
		UNION
	SELECT "PROGRAM_ADMINISTRATOR", "PROJECT_DEACTIVATED", "PROJECT_CONFIGURE"
		UNION
	SELECT "PROJECT_ADMINISTRATOR", "PROJECT_APPROVED", "PROJECT_CONFIGURE"
		UNION
	SELECT "PROJECT_ADMINISTRATOR", "PROJECT_DEACTIVATED", "PROJECT_CONFIGURE"
		UNION
	SELECT "PROJECT_PRIMARY_SUPERVISOR", "PROJECT_APPROVED", "PROJECT_CONFIGURE"
		UNION
	SELECT "PROJECT_PRIMARY_SUPERVISOR", "PROJECT_DEACTIVATED", "PROJECT_CONFIGURE"
;

INSERT INTO USER_ROLE (program_id, user_id, role_id, requesting_user_id, assigned_timestamp)
	SELECT ADVERT.id, USER_ROLE.user_id, "PROGRAM_PRACTITIONER",
		ADVERT.user_id, MIN(USER_ROLE.assigned_timestamp)
	FROM USER_ROLE INNER JOIN APPLICATION
		ON USER_ROLE.application_id = APPLICATION.id
	INNER JOIN ADVERT
		ON APPLICATION.program_id = ADVERT.id
	WHERE USER_ROLE.role_id IN ("APPLICATION_ADMINISTRATOR", "APPLICATION_INTERVIEWER",
		"APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_REVIEWER", 
		"APPLICATION_SECONDARY_SUPERVISOR", "APPLICATION_ADMINISTRATOR_PREVIOUS", 
		"APPLICATION_INTERVIEWER_PREVIOUS", "APPLICATION_PRIMARY_SUPERVISOR_PREVIOUS", 
		"APPLICATION_REVIEWER_PREVIOUS", "APPLICATION_SECONDARY_SUPERVISOR_PREVIOUS")
	GROUP BY APPLICATION.program_id, USER_ROLE.user_id
;

ALTER TABLE ACTION_REQUIRED
	ADD COLUMN system_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER system_id,
	ADD COLUMN program_id INT(10) UNSIGNED AFTER institution_id,
	ADD COLUMN project_id INT(10) UNSIGNED AFTER program_id,
	ADD UNIQUE INDEX (system_id, role_id, action_id),
	ADD UNIQUE INDEX (institution_id, role_id, action_id),
	ADD UNIQUE INDEX (program_id, role_id, action_id),
	ADD UNIQUE INDEX (project_id, role_id, action_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	ADD FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	ADD FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	MODIFY COLUMN application_id INT(10) UNSIGNED
;

DELETE FROM ACTION_REQUIRED
WHERE (role_id = "APPLICATION_REVIEWER"
	AND action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR")
	OR (role_id = "PROGRAM_ADMINISTRATOR"
		AND action_id = "APPLICATION_CONFIRM_OFFER_RECOMMENDATION")
	OR (role_id = "PROGRAM_APPROVER"
		AND action_id = "APPLICATION_COMPLETE_INTERVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR")
;
