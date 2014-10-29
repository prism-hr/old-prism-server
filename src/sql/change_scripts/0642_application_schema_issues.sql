CREATE TABLE SYSTEM (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (name)
) ENGINE = INNODB
;

CREATE TABLE USER_ROLE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	project_id INT(10) UNSIGNED,
	application_id INT(10) UNSIGNED,
	user_id INT(10) UNSIGNED NOT NULL,
	role_id VARCHAR(50) NOT NULL,	
	requesting_user_id INT(10) UNSIGNED NOT NULL,
	assigned_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, user_id, role_id),
	UNIQUE INDEX (institution_id, user_id, role_id),
	UNIQUE INDEX (program_id, user_id, role_id),
	UNIQUE INDEX (project_id, user_id, role_id),
	UNIQUE INDEX (application_id, user_id, role_id),
	INDEX (user_id),
	INDEX (role_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id),
	FOREIGN KEY (user_id) REFERENCES USER (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

INSERT INTO SYSTEM (name)
VALUES ("PRiSM")
;

INSERT INTO USER_ROLE (system_id, user_id, role_id, requesting_user_id)
	SELECT LAST_INSERT_ID(), user_id, role_id, requesting_user_id
	FROM SYSTEM_USER_ROLE
;

INSERT INTO USER_ROLE (institution_id, user_id, role_id, requesting_user_id)
	SELECT institution_id, user_id, role_id, requesting_user_id
	FROM INSTITUTION_USER_ROLE
;

INSERT INTO USER_ROLE (program_id, user_id, role_id, requesting_user_id)
	SELECT program_id, user_id, role_id, requesting_user_id
	FROM PROGRAM_USER_ROLE
;

INSERT INTO USER_ROLE (project_id, user_id, role_id, requesting_user_id)
	SELECT project_id, user_id, role_id, requesting_user_id
	FROM PROJECT_USER_ROLE
;

INSERT INTO USER_ROLE (application_id, user_id, role_id, requesting_user_id)
	SELECT application_id, user_id, role_id, requesting_user_id
	FROM APPLICATION_USER_ROLE
;

ALTER TABLE APPLICATION
	ADD INDEX (due_date)
;

ALTER TABLE INSTITUTION
	ADD COLUMN system_id INT(10) UNSIGNED,
	ADD INDEX (system_id),
	ADD FOREIGN KEY (system_id) REFERENCES SYSTEM (id)
;

UPDATE INSTITUTION
SET system_id = 1
;

ALTER TABLE INSTITUTION
	MODIFY system_id INT(10) UNSIGNED NOT NULL
;

UPDATE APPLICATION_ACTION_REQUIRED
SET action_id = REPLACE(action_id, "AS_PROGRAM_ADMINISTRATOR", "AS_APPLICATION_ADMINISTRATOR")
WHERE role_id = "APPLICATION_ADMINISTRATOR"
;

DELETE FROM APPLICATION_ACTION_REQUIRED
WHERE role_id IN ("APPLICATION_REVIEWER", "APPLICATION_INTERVIEWER", "APPLICATION_PRIMARY_SUPERVISOR",
	"APPLICATION_REFEREE", "APPLICATION_APPLICANT")
AND action_id IN ("APPLICATION_ASSIGN_SUPERVISORS", "APPLICATION_COMPLETE_REVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR", 
	"APPLICATION_CONFIRM_OFFER_RECOMMENDATION", "APPLICATION_CONFIRM_REJECTION", "APPLICATION_MOVE_TO_DIFFERENT_STAGE")
;

DELETE FROM APPLICATION_ACTION_REQUIRED
WHERE role_id = "PROGRAM_APPROVER"
AND action_id IN ("APPLICATION_COMPLETE_REVIEW_STAGE_AS_PROGRAM_ADMINISTRATOR", "APPLICATION_COMPLETE_VALIDATION_STAGE")
;

DELETE FROM APPLICATION_ACTION_REQUIRED
WHERE role_id = "PROGRAM_VIEWER"
;
