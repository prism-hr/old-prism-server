ALTER TABLE APPLICATION_FORM_ACTION_OPTIONAL
	DROP COLUMN raises_urgent_flag
;

CREATE TABLE APPLICATION_ROLE_SCOPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)) ENGINE = INNODB
	SELECT "SYSTEM" AS id
		UNION
	SELECT "INSTITUTION" AS id
		UNION
	SELECT "PROGRAM" AS id
		UNION
	SELECT "PROJECT" AS id
		UNION
	SELECT "APPLICATION" AS id
;

ALTER TABLE APPLICATION_ROLE
	ADD COLUMN application_role_scope_id VARCHAR(50),
	ADD INDEX (application_role_scope_id),
	ADD FOREIGN KEY (application_role_scope_id) REFERENCES APPLICATION_ROLE_SCOPE (id),
	ADD COLUMN old_id VARCHAR(50)
;

SET FOREIGN_KEY_CHECKS = 0
;

UPDATE APPLICATION_ROLE
SET old_id = id, 
	id = "SYSTEM_ADMINISTRATOR",
	application_role_scope_id = "SYSTEM"
WHERE id = "SUPERADMINISTRATOR"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "INSTITUTION_ADMITTER",
	application_role_scope_id = "INSTITUTION"
WHERE id = "ADMITTER"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "PROGRAM_ADMINISTRATOR",
	application_role_scope_id = "PROGRAM"
WHERE id = "ADMINISTRATOR"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "PROGRAM_APPROVER",
	application_role_scope_id = "PROGRAM"
WHERE id = "APPROVER"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "PROGRAM_VIEWER",
	application_role_scope_id = "PROGRAM"
WHERE id = "VIEWER"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "PROJECT_ADMINISTRATOR",
	application_role_scope_id = "PROJECT"
WHERE id = "PROJECTADMINISTRATOR"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_APPLICANT",
	application_role_scope_id = "APPLICATION"
WHERE id = "APPLICANT"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_SUGGESTED_SUPERVISOR",
	application_role_scope_id = "APPLICATION"
WHERE id = "SUGGESTEDSUPERVISOR"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_REFEREE",
	application_role_scope_id = "APPLICATION"
WHERE id = "REFEREE"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_INTERVIEWER",
	application_role_scope_id = "APPLICATION"
WHERE id = "INTERVIEWER"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_REVIEWER",
	application_role_scope_id = "APPLICATION"
WHERE id = "REVIEWER"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_ADMINISTRATOR",
	application_role_scope_id = "APPLICATION"
WHERE id = "STATEADMINISTRATOR"
;

UPDATE APPLICATION_ROLE
SET old_id = id,
	id = "APPLICATION_PRIMARY_SUPERVISOR",
	application_role_scope_id = "APPLICATION"
WHERE id = "SUPERVISOR"
;

UPDATE APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_ROLE
	ON APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = APPLICATION_ROLE.old_id
	SET APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = APPLICATION_ROLE.id
;

UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
	ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.old_id
	SET APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
;

UPDATE PENDING_ROLE_NOTIFICATION INNER JOIN APPLICATION_ROLE
	ON PENDING_ROLE_NOTIFICATION.role_id = APPLICATION_ROLE.old_id
	SET PENDING_ROLE_NOTIFICATION.role_id = APPLICATION_ROLE.id
;

UPDATE USER_ROLE_LINK INNER JOIN APPLICATION_ROLE
	ON USER_ROLE_LINK.application_role_id = APPLICATION_ROLE.old_id
	SET USER_ROLE_LINK.application_role_id = APPLICATION_ROLE.id
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE APPLICATION_ROLE
	MODIFY application_role_scope_id VARCHAR(50) NOT NULL,
	DROP COLUMN old_id
;

INSERT INTO APPLICATION_ROLE (id, update_visibility, do_send_update_notification, application_role_scope_id)
VALUES ("PROJECT_PRIMARY_SUPERVISOR", 1, 0, "PROJECT"),
	("PROJECT_SECONDARY_SUPERVISOR", 1, 0, "PROJECT"),
	("APPLICATION_SECONDARY_SUPERVISOR", 1, 0, "APPLICATION")
;

INSERT INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, 
	application_role_id, raises_update_flag)
	SELECT APPLICATION_FORM.id, PROJECT.primary_supervisor_id, "PROJECT_PRIMARY_SUPERVISOR",
		MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag)
	FROM APPLICATION_FORM INNER JOIN PROJECT
		ON APPLICATION_FORM.project_id = PROJECT.id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
	GROUP BY PROJECT.primary_supervisor_id, APPLICATION_FORM.id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, 
	application_role_id, raises_update_flag)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "APPLICATION_SECONDARY_SUPERVISOR",
		MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag)
	FROM APPLICATION_FORM INNER JOIN APPROVAL_ROUND
		ON APPLICATION_FORM.id = APPROVAL_ROUND.application_form_id
	INNER JOIN SUPERVISOR
		ON APPROVAL_ROUND.id = SUPERVISOR.approval_round_id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
		AND SUPERVISOR.is_primary = 0
	GROUP BY SUPERVISOR.registered_user_id, APPLICATION_FORM.id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, 
	application_role_id, raises_update_flag)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "APPLICATION_PRIMARY_SUPERVISOR", 
		MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag)
	FROM APPLICATION_FORM INNER JOIN COMMENT
		ON APPLICATION_FORM.id = COMMENT.application_form_id
	INNER JOIN SUPERVISION_CONFIRMATION_COMMENT
		ON COMMENT.id = SUPERVISION_CONFIRMATION_COMMENT.id
	INNER JOIN SUPERVISOR
		ON SUPERVISION_CONFIRMATION_COMMENT.supervisor_id = SUPERVISOR.id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
	GROUP BY SUPERVISION_CONFIRMATION_COMMENT.supervisor_id, APPLICATION_FORM.id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, 
	application_role_id, raises_update_flag)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "APPLICATION_SECONDARY_SUPERVISOR", 
		MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag)
	FROM APPLICATION_FORM INNER JOIN COMMENT
		ON APPLICATION_FORM.id = COMMENT.application_form_id
	INNER JOIN SUPERVISION_CONFIRMATION_COMMENT
		ON COMMENT.id = SUPERVISION_CONFIRMATION_COMMENT.id
	INNER JOIN SUPERVISOR
		ON SUPERVISION_CONFIRMATION_COMMENT.secondary_supervisor_id = SUPERVISOR.id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
	GROUP BY SUPERVISION_CONFIRMATION_COMMENT.secondary_supervisor_id, APPLICATION_FORM.id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, 
	application_role_id, raises_update_flag)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "APPLICATION_PRIMARY_SUPERVISOR", 
		MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag)
	FROM APPLICATION_FORM INNER JOIN COMMENT
		ON APPLICATION_FORM.id = COMMENT.application_form_id
	INNER JOIN OFFER_RECOMMENDED_COMMENT
		ON COMMENT.id = OFFER_RECOMMENDED_COMMENT.id
	INNER JOIN SUPERVISOR
		ON OFFER_RECOMMENDED_COMMENT.supervisor_id = SUPERVISOR.id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
	GROUP BY OFFER_RECOMMENDED_COMMENT.supervisor_id, APPLICATION_FORM.id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, 
	application_role_id, raises_update_flag)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "APPLICATION_SECONDARY_SUPERVISOR", 
		MAX(APPLICATION_FORM_USER_ROLE.raises_update_flag)
	FROM APPLICATION_FORM INNER JOIN COMMENT
		ON APPLICATION_FORM.id = COMMENT.application_form_id
	INNER JOIN OFFER_RECOMMENDED_COMMENT
		ON COMMENT.id = OFFER_RECOMMENDED_COMMENT.id
	INNER JOIN SUPERVISOR
		ON OFFER_RECOMMENDED_COMMENT.supervisor_id = SUPERVISOR.id
	INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
	GROUP BY OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id, APPLICATION_FORM.id
;

UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN (
	SELECT application_form_id AS application_form_id, 
		registered_user_id AS registered_user_id, 
		MIN(raises_update_flag) AS raises_update_flag
	FROM APPLICATION_FORM_USER_ROLE
	GROUP BY application_form_id, registered_user_id)
	AS UPDATE_STATUS
	ON APPLICATION_FORM_USER_ROLE.application_form_id = UPDATE_STATUS.application_form_id
		AND APPLICATION_FORM_USER_ROLE.registered_user_id = UPDATE_STATUS.registered_user_id
SET APPLICATION_FORM_USER_ROLE.raises_update_flag = UPDATE_STATUS.raises_update_flag
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL
	SELECT APPLICATION_ROLE.id, STATE.id, ACTION.id
	FROM APPLICATION_ROLE INNER JOIN STATE INNER JOIN ACTION
	WHERE APPLICATION_ROLE.id IN ("PROJECT_PRIMARY_SUPERVISOR", 
		"PROJECT_PRIMARY_SUPERVISOR", "APPLICATION_SECONDARY_SUPERVISOR")
		AND STATE.id NOT IN ("UNSUBMITTED", "VALIDATION")
		AND ACTION.id IN ("COMMENT", "EMAIL_APPLICANT", "VIEW_AS_RECRUITER")
;

CREATE TABLE SYSTEM_USER_ROLE (
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (registered_user_id, application_role_id),
	INDEX (application_role_id, registered_user_id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)) ENGINE = INNODB
	SELECT registered_user_id, application_role_id
	FROM USER_ROLE_LINK
	WHERE application_role_id = "SYSTEM_ADMINISTRATOR"
;

CREATE TABLE INSTITUTION_USER_ROLE (
	institution_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (institution_id, registered_user_id, application_role_id),
	INDEX (institution_id, application_role_id, registered_user_id),
	INDEX (registered_user_id, institution_id, application_role_id),
	INDEX (registered_user_id, application_role_id, institution_id),
	INDEX (application_role_id, institution_id, registered_user_id),
	INDEX (application_role_id, registered_user_id, institution_id),	
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)) ENGINE = INNODB
	SELECT 5243 AS institution_id, registered_user_id, application_role_id
	FROM USER_ROLE_LINK
	WHERE application_role_id = "INSTITUTION_ADMITTER"
;

CREATE TABLE PROGRAM_USER_ROLE (
	program_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (program_id, registered_user_id, application_role_id),
	INDEX (program_id, application_role_id, registered_user_id),
	INDEX (registered_user_id, program_id, application_role_id),
	INDEX (registered_user_id, application_role_id, program_id),
	INDEX (application_role_id, program_id, registered_user_id),
	INDEX (application_role_id, registered_user_id, program_id),	
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)) ENGINE = INNODB
	SELECT PROGRAM_ADMINISTRATOR_LINK.program_id AS program_id, 
		PROGRAM_ADMINISTRATOR_LINK.administrator_id AS registered_user_id,
		"PROGRAM_ADMINISTRATOR" AS application_role_id
	FROM PROGRAM_ADMINISTRATOR_LINK
		UNION
	SELECT PROGRAM_APPROVER_LINK.program_id AS program_id, 
		PROGRAM_APPROVER_LINK.registered_user_id AS registered_user_id,
		"PROGRAM_APPROVER" AS application_role_id
	FROM PROGRAM_APPROVER_LINK
		UNION
	SELECT PROGRAM_VIEWER_LINK.program_id AS program_id, 
		PROGRAM_VIEWER_LINK.viewer_id AS registered_user_id,
		"PROGRAM_VIEWER" AS application_role_id
	FROM PROGRAM_VIEWER_LINK
;

CREATE TABLE PROJECT_USER_ROLE (
	project_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (project_id, registered_user_id, application_role_id),
	INDEX (project_id, application_role_id, registered_user_id),
	INDEX (registered_user_id, project_id, application_role_id),
	INDEX (registered_user_id, application_role_id, project_id),
	INDEX (application_role_id, project_id, registered_user_id),
	INDEX (application_role_id, registered_user_id, project_id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)) ENGINE = INNODB
	SELECT id AS project_id, primary_supervisor_id AS registered_user_id, 
		"PROJECT_PRIMARY_SUPERVISOR" AS application_role_id
	FROM PROJECT
		UNION
	SELECT id AS project_id, administrator_id AS registered_user_id, 
		"PROJECT_ADMINISTRATOR" AS application_role_id
	FROM PROJECT
	WHERE administrator_id IS NOT NULL
		UNION
	SELECT id AS project_id, secondary_supervisor_id AS registered_user_id, 
		"PROJECT_SECONDARY_SUPERVISOR" AS application_role_id
	FROM PROJECT
	WHERE secondary_supervisor_id IS NOT NULL
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	ADD UNIQUE INDEX (application_form_id, registered_user_id, application_role_id,
		is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag),
	ADD INDEX (application_form_id, application_role_id, registered_user_id,
		is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag),
	ADD INDEX (registered_user_id, application_form_id, application_role_id,
		is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag),
	ADD INDEX (registered_user_id, application_role_id, application_form_id,
			is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag),
	ADD INDEX (application_role_id, application_form_id, registered_user_id,
			is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag),
	ADD INDEX (application_role_id, registered_user_id, application_form_id,
			is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag),
	DROP INDEX application_form_id,
	DROP INDEX registered_user_id,
	DROP INDEX application_role_id,
	DROP INDEX is_interested_in_applicant,
	DROP INDEX update_timestamp,
	DROP INDEX raises_update_flag,
	DROP INDEX raises_urgent_flag
;

DROP TABLE PROGRAM_ADMINISTRATOR_LINK
;

DROP TABLE PROGRAM_APPROVER_LINK
;

DROP TABLE PROGRAM_VIEWER_LINK
;

DROP TABLE USER_ROLE_LINK
;

ALTER TABLE PROJECT
	DROP FOREIGN KEY project_administrator_registered_user_fk,
	DROP COLUMN administrator_id,
	DROP FOREIGN KEY project_primary_supervisor_fk,
	DROP COLUMN primary_supervisor_id,
	DROP FOREIGN KEY project_secondary_supervisor_fk,
	DROP COLUMN secondary_supervisor_id
;

ALTER TABLE APPLICATION_FORM
	DROP COLUMN is_editable_by_applicant
;

CREATE TABLE APPLICATION_FORM_UPDATE (
	application_form_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	raises_update_flag INT(1) UNSIGNED NOT NULL,
	created_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (application_form_id, registered_user_id),
	UNIQUE INDEX (registered_user_id, application_form_id)
) ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_UPDATE
	SELECT application_form_id, registered_user_id, MIN(raises_update_flag), update_timestamp
	FROM APPLICATION_FORM_USER_ROLE
	GROUP BY application_form_id, registered_user_id
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	DROP COLUMN update_timestamp,
	DROP COLUMN raises_update_flag
;

CREATE TABLE APPLICATION_FORM_ACTION_REQUIRED_COPY (
	application_form_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	action_id VARCHAR(50) NOT NULL,
	deadline_timestamp DATE NOT NULL,
	bind_deadline_to_due_date INT(1) UNSIGNED NOT NULL,
	raises_urgent_flag INT(1) UNSIGNED NOT NULL DEFAULT '0',
	assigned_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (application_form_id, registered_user_id, application_role_id, action_id),
	INDEX (registered_user_id),
	INDEX (application_role_id),
	INDEX action_id (action_id),
	INDEX deadline_timestamp (deadline_timestamp),
	INDEX bind_deadline_to_due_date (bind_deadline_to_due_date),
	INDEX raises_urgent_flag (raises_urgent_flag),
	INDEX assigned_timestamp (assigned_timestamp),
	FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id)
) ENGINE = INNODB
;

INSERT INTO APPLICATION_FORM_ACTION_REQUIRED_COPY
	SELECT APPLICATION_FORM_USER_ROLE.application_form_id, APPLICATION_FORM_USER_ROLE.registered_user_id,
		APPLICATION_FORM_USER_ROLE.application_role_id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
		APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, 
		APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
		APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag,
		APPLICATION_FORM_ACTION_REQUIRED.assigned_timestamp
	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
;

DROP TABLE APPLICATION_FORM_ACTION_REQUIRED
;

RENAME TABLE APPLICATION_FORM_ACTION_REQUIRED_COPY TO APPLICATION_FORM_ACTION_REQUIRED
;

SET FOREIGN_KEY_CHECKS = 0
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	DROP PRIMARY KEY,
	DROP COLUMN id,
	ADD PRIMARY KEY(application_form_id, registered_user_id, application_role_id),
	DROP INDEX application_form_id
;
