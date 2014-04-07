INSERT INTO ROLE (id, do_send_update_notification, scope_id, update_scope_id)
VALUES ("APPLICATION_REVIEWER_PREVIOUS", 0, "APPLICATION", "INTERNAL"),
	("APPLICATION_INTERVIEWER_PREVIOUS", 0, "APPLICATION", "INTERNAL"),
	("APPLICATION_PRIMARY_SUPERVISOR_PREVIOUS", 0, "APPLICATION", "INTERNAL"),
	("APPLICATION_SECONDARY_SUPERVISOR_PREVIOUS", 0, "APPLICATION", "INTERNAL"),
	("APPLICATION_ADMINISTRATOR_PREVIOUS", 0, "APPLICATION", "INTERNAL"),
	("APPLICATION_REFEREE_PREVIOUS", 0, "APPLICATION", "EXTERNAL")
;

ALTER TABLE ROLE
	ADD COLUMN role_on_expiry_id VARCHAR(50),
	ADD INDEX (role_on_expiry_id),
	ADD FOREIGN KEY (role_on_expiry_id) REFERENCES ROLE (id),
	DROP FOREIGN KEY fk_application_role_update_scope_id,
	MODIFY COLUMN update_scope_id VARCHAR(50) NOT NULL,
	ADD FOREIGN KEY (update_scope_id) REFERENCES UPDATE_SCOPE (id)
;

UPDATE ROLE
SET role_on_expiry_id = CONCAT(id , "_PREVIOUS")
WHERE id IN ("APPLICATION_REVIEWER", "APPLICATION_INTERVIEWER",
	"APPLICATION_PRIMARY_SUPERVISOR", "APPLICATION_SECONDARY_SUPERVISOR",
	"APPLICATION_ADMINISTRATOR", "APPLICATION_REFEREE")
;

UPDATE APPLICATION_USER_ROLE LEFT JOIN APPLICATION_ACTION_REQUIRED
	ON APPLICATION_USER_ROLE.id = APPLICATION_ACTION_REQUIRED.application_user_role_id
INNER JOIN ROLE
	ON APPLICATION_USER_ROLE.role_id = ROLE.id
SET APPLICATION_USER_ROLE.role_id = CONCAT(APPLICATION_USER_ROLE.role_id, "_PREVIOUS")
WHERE APPLICATION_ACTION_REQUIRED.id IS NULL
	AND ROLE.role_on_expiry_id IS NOT NULL
;

INSERT INTO APPLICATION_ACTION_OPTIONAL
	SELECT NULL AS id, ROLE.role_on_expiry_id, APPLICATION_ACTION_OPTIONAL.state_id, 
		APPLICATION_ACTION_OPTIONAL.action_id
	FROM APPLICATION_ACTION_OPTIONAL INNER JOIN ROLE
		ON APPLICATION_ACTION_OPTIONAL.role_id = ROLE.id
	WHERE ROLE.role_on_expiry_id IS NOT NULL
;

DELETE 
FROM APPLICATION_ACTION_REQUIRED
WHERE action_id = "APPLICATION_COMPLETE_APPROVAL_STAGE"
;

CREATE TABLE APPLICATION_ACTION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	application_id INT(10) UNSIGNED NOT NULL,
	role_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	deadline_timestamp DATE,
	bind_deadline_to_due_date INT(1) UNSIGNED,
	raises_urgent_flag INT(1) UNSIGNED NOT NULL,
	assigned_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE INDEX (application_id, role_id, action_id),
	INDEX (role_id),
	INDEX (action_id),
	INDEX (deadline_timestamp),
	INDEX (bind_deadline_to_due_date),
	INDEX (raises_urgent_flag),
	INDEX (assigned_timestamp),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
	SELECT NULL AS id, APPLICATION_USER_ROLE.application_id AS application_id,
		APPLICATION_USER_ROLE.role_id AS role_id, 
		APPLICATION_ACTION_REQUIRED.action_id AS action_id,
		APPLICATION_ACTION_REQUIRED.deadline_timestamp AS deadline_timestamp,
		APPLICATION_ACTION_REQUIRED.bind_deadline_to_due_date AS bind_deadline_to_due_date,
		APPLICATION_ACTION_REQUIRED.raises_urgent_flag AS raises_urgent_flag,
		APPLICATION_ACTION_REQUIRED.assigned_timestamp AS assigned_timestamp
	FROM APPLICATION_USER_ROLE INNER JOIN APPLICATION_ACTION_REQUIRED
		ON APPLICATION_USER_ROLE.id = APPLICATION_ACTION_REQUIRED.application_user_role_id
	GROUP BY APPLICATION_USER_ROLE.application_id, APPLICATION_USER_ROLE.role_id,
		APPLICATION_ACTION_REQUIRED.action_id
;

DROP TABLE APPLICATION_ACTION_REQUIRED
;

RENAME TABLE APPLICATION_ACTION TO APPLICATION_ACTION_REQUIRED
;

DELETE APPLICATION_USER_ROLE.*
FROM APPLICATION_USER_ROLE INNER JOIN ROLE
	ON APPLICATION_USER_ROLE.role_id = ROLE.id
WHERE ROLE.scope_id != "APPLICATION"
;

CREATE TABLE SYSTEM_ACTION_OPTIONAL (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, action_id),
	INDEX (action_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

CREATE TABLE INSTITUTION_ACTION_OPTIONAL (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, action_id),
	INDEX (action_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

CREATE TABLE PROGRAM_ACTION_REQUIRED (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	program_id INT(10) UNSIGNED NOT NULL,
	role_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	deadline_timestamp DATE NOT NULL,
	raises_urgent_flag INT(1) UNSIGNED NOT NULL,
	assigned_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (id),
	UNIQUE INDEX (program_id, role_id, action_id),
	INDEX (role_id),
	INDEX (action_id),
	INDEX (deadline_timestamp),
	INDEX (raises_urgent_flag),
	INDEX (assigned_timestamp),
	FOREIGN KEY (program_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

CREATE TABLE PROGRAM_ACTION_OPTIONAL (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, state_id, action_id),
	INDEX (state_id),
	INDEX (action_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

CREATE TABLE PROJECT_ACTION_OPTIONAL (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, state_id, action_id),
	INDEX (state_id),
	INDEX (action_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

CREATE TABLE ROLE_INHERITANCE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	role_id VARCHAR(50) NOT NULL,
	inherited_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (role_id, inherited_role_id),
	INDEX (inherited_role_id),
	FOREIGN KEY (role_id) REFERENCES ROLE (id),
	FOREIGN KEY (inherited_role_id) REFERENCES ROLE (id)
) ENGINE = INNODB
;

INSERT INTO ROLE_INHERITANCE (role_id, inherited_role_id)
	SELECT ROLE.id, ROLE2.id
	FROM ROLE INNER JOIN ROLE AS ROLE2
	WHERE ROLE.scope_id = "SYSTEM"
		AND ROLE2.scope_id IN ("INSTITUTION", "PROGRAM")
		AND ROLE2.id != "PROGRAM_VIEWER"
;

INSERT INTO ROLE_INHERITANCE (role_id, inherited_role_id)
	SELECT ROLE.id, ROLE2.id
	FROM ROLE INNER JOIN ROLE AS ROLE2
	WHERE ROLE.scope_id = "INSTITUTION"
		AND ROLE2.id = "PROGRAM_VIEWER"
;
