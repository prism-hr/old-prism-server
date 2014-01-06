ALTER TABLE APPLICATION_ROLE
	ADD COLUMN do_send_role_notification INT(1) UNSIGNED NOT NULL DEFAULT 0,
	ADD INDEX (do_send_role_notification)
;

INSERT INTO APPLICATION_ROLE (id, update_visibility, do_send_update_notification, do_send_role_notification)
VALUES ("SAFETYNET", 0, 0, 0),
	("PRIMARYSUPERVISOR", 1, 0, 1),
	("SECONDARYSUPERVISOR", 1, 0, 1),
	("PROJECTAUTHOR", 1, 0, 0),
	("PROJECTPRIMARYSUPERVISOR", 1, 0, 0),
	("PROJECTSECONDARYSUPERVISOR", 1, 0, 1)
;

UPDATE APPLICATION_ROLE
SET do_send_role_notification = 1
WHERE id IN ("SUPERADMINISTRATOR", "ADMITTER", "ADMINISTRATOR", "APPROVER", "VIEWER", "PROJECTADMINISTRATOR")
;

UPDATE APPLICATION_ROLE
SET update_visibility = 0
WHERE id = "REFEREE"
;

CREATE TABLE AUTHORITY_GROUP_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
;

INSERT INTO AUTHORITY_GROUP_TYPE (id)
VALUES ("ADMISSIONSOFFICER"),
	("PROGRAMADMINISTRATOR"),
	("PROGRAMAUTHOR"),
	("PROJECTADMINISTRATOR"),
	("PROJECTCREATOR"),
	("PROJECTEDITOR"),
	("EQUALOPPORTUNITIESVIEWER"),
	("CRIMINALCONVICTIONSVIEWER"),
	("COMMENTAUTHOR"),
	("POTENTIALSUPERVISOR"),
	("RECRUITMENTOFFICER"),
	("RECRUITMENTDIRECTOR"),
	("APPLICATIONAUTHOR"),
	("REFERENCEAUTHOR")
;

CREATE TABLE AUTHORITY_GROUP (
	authority_group_type_id VARCHAR(50) NOT NULL,
	application_role_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (authority_group_type_id, application_role_id),
	INDEX (application_role_id),
	FOREIGN KEY (authority_group_type_id) REFERENCES AUTHORITY_GROUP_TYPE (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)
) ENGINE = INNODB
;

INSERT INTO AUTHORITY_GROUP (authority_group_type_id, application_role_id)
VALUES ("ADMISSIONSOFFICER", "SUPERADMINISTRATOR"),
	("ADMISSIONSOFFICER", "ADMITTER"),
	("PROGRAMADMINISTRATOR", "ADMINISTRATOR"),
	("PROGRAMADMINISTRATOR", "SUPERADMINISTRATOR"),
	("PROGRAMAUTHOR", "ADMINISTRATOR"),
	("PROGRAMAUTHOR", "SUPERADMINISTRATOR"),
	("PROJECTADMINISTRATOR", "ADMINISTRATOR"),
	("PROJECTADMINISTRATOR", "PROJECTADMINISTRATOR"),
	("PROJECTADMINISTRATOR", "SUPERADMINISTRATOR"),
	("PROJECTCREATOR", "ADMINISTRATOR"),
	("PROJECTCREATOR", "APPROVER"),
	("PROJECTCREATOR", "INTERVIEWER"),
	("PROJECTCREATOR", "PROJECTADMINISTRATOR"),
	("PROJECTCREATOR", "PROJECTAUTHOR"),
	("PROJECTCREATOR", "REVIEWER"),
	("PROJECTCREATOR", "STATEADMINISTRATOR"),
	("PROJECTCREATOR", "PRIMARYSUPERVISOR"),
	("PROJECTCREATOR", "SECONDARYSUPERVISOR"),
	("PROJECTCREATOR", "SUPERADMINISTRATOR"),
	("PROJECTEDITOR", "ADMINISTRATOR"),
	("PROJECTEDITOR", "PROJECTADMINISTRATOR"),
	("PROJECTEDITOR", "PROJECTAUTHOR"),
	("PROJECTEDITOR", "PROJECTPRIMARYSUPERVISOR"),
	("PROJECTEDITOR", "SUPERADMINISTRATOR"),
	("EQUALOPPORTUNITIESVIEWER", "APPLICANT"),
	("CRIMINALCONVICTIONSVIEWER", "ADMINISTRATOR"),
	("CRIMINALCONVICTIONSVIEWER", "ADMITTER"),
	("CRIMINALCONVICTIONSVIEWER", "APPLICANT"),
	("CRIMINALCONVICTIONSVIEWER", "APPROVER"),
	("CRIMINALCONVICTIONSVIEWER", "PROJECTADMINISTRATOR"),
	("CRIMINALCONVICTIONSVIEWER", "STATEADMINISTRATOR"),
	("CRIMINALCONVICTIONSVIEWER", "SUPERADMINISTRATOR"),
	("COMMENTAUTHOR", "ADMINISTRATOR"),
	("COMMENTAUTHOR", "ADMITTER"),
	("COMMENTAUTHOR", "APPROVER"),
	("COMMENTAUTHOR", "INTERVIEWER"),
	("COMMENTAUTHOR", "PRIMARYSUPERVISOR"),
	("COMMENTAUTHOR", "SECONDARYSUPERVISOR"),
	("COMMENTAUTHOR", "PROJECTADMINISTRATOR"),
	("COMMENTAUTHOR", "PROJECTAUTHOR"),
	("COMMENTAUTHOR", "PROJECTPRIMARYSUPERVISOR"),
	("COMMENTAUTHOR", "PROJECTSECONDARYSUPERVISOR"),
	("COMMENTAUTHOR", "REVIEWER"),
	("COMMENTAUTHOR", "STATEADMINISTRATOR"),
	("COMMENTAUTHOR", "SUPERADMINISTRATOR"),
	("COMMENTAUTHOR", "VIEWER"),	
	("POTENTIALSUPERVISOR", "APPROVER"),
	("POTENTIALSUPERVISOR", "INTERVIEWER"),
	("POTENTIALSUPERVISOR", "PRIMARYSUPERVISOR"),
	("POTENTIALSUPERVISOR", "SECONDARYSUPERVISOR"),
	("POTENTIALSUPERVISOR", "PROJECTPRIMARYSUPERVISOR"),
	("POTENTIALSUPERVISOR", "PROJECTSECONDARYSUPERVISOR"),
	("POTENTIALSUPERVISOR", "REVIEWER"),
	("POTENTIALSUPERVISOR", "VIEWER"),
	("RECRUITMENTOFFICER", "ADMINISTRATOR"),
	("RECRUITMENTOFFICER", "PROJECTADMINISTRATOR"),
	("RECRUITMENTOFFICER", "STATEADMINISTRATOR"),
	("RECRUITMENTDIRECTOR", "APPROVER"),
	("RECRUITMENTDIRECTOR", "SUPERADMINISTRATOR"),
	("APPLICATIONAUTHOR", "APPLICANT"),
	("REFERENCEAUTHOR", "REFEREE")
;

UPDATE APPLICATION_FORM_USER_ROLE
SET application_role_id = "PRIMARYSUPERVISOR"
WHERE application_role_id = "SUPERVISOR"
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id, raises_urgent_flag)
	SELECT APPLICATION_ROLE.id, APPLICATION_FORM_ACTION_OPTIONAL.state_id, APPLICATION_FORM_ACTION_OPTIONAL.action_id, 
		APPLICATION_FORM_ACTION_OPTIONAL.raises_urgent_flag 
	FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_ROLE
	WHERE APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = "SUPERVISOR"
		AND APPLICATION_ROLE.id IN ("PRIMARYSUPERVISOR", "SECONDARYSUPERVISOR")
;

INSERT INTO APPLICATION_FORM_ACTION_OPTIONAL (application_role_id, state_id, action_id, raises_urgent_flag)
	SELECT APPLICATION_ROLE.id, APPLICATION_FORM_ACTION_OPTIONAL.state_id, APPLICATION_FORM_ACTION_OPTIONAL.action_id, 
		APPLICATION_FORM_ACTION_OPTIONAL.raises_urgent_flag 
	FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_ROLE
	WHERE APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = "VIEWER"
		AND APPLICATION_ROLE.id IN ("PROJECTAUTHOR", "PROJECTPRIMARYSUPERVISOR", "PROJECTSECONDARYSUPERVISOR")
;

DELETE FROM APPLICATION_FORM_ACTION_OPTIONAL
WHERE application_role_id = "SUPERVISOR"
;

UPDATE PENDING_ROLE_NOTIFICATION
SET role_id = "PRIMARYSUPERVISOR"
WHERE role_id = "SUPERVISOR"
;

UPDATE PENDING_ROLE_NOTIFICATION
SET role_id = "PRIMARYSUPERVISOR"
WHERE role_id = "SUPERVISOR"
;

DROP TABLE USER_ROLE_LINK
;

DELETE FROM APPLICATION_ROLE
WHERE id = "SUPERVISOR"
;

CREATE TABLE SYSTEM_USER_ROLE (
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50),
	PRIMARY KEY(registered_user_id, application_role_id),
	INDEX (application_role_id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)
) ENGINE = INNODB
;

CREATE TABLE PROGRAM_USER_ROLE (
	program_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50),
	PRIMARY KEY(program_id, registered_user_id, application_role_id),
	INDEX (registered_user_id),
	INDEX (application_role_id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)
) ENGINE = INNODB
;

INSERT INTO PROGRAM_USER_ROLE (program_id, registered_user_id, application_role_id)
	SELECT program_id, administrator_id, "ADMINISTRATOR"
	FROM PROGRAM_ADMINISTRATOR_LINK
;

INSERT INTO PROGRAM_USER_ROLE (program_id, registered_user_id, application_role_id)
	SELECT program_id, registered_user_id, "APPROVER"
	FROM PROGRAM_APPROVER_LINK
;

INSERT INTO PROGRAM_USER_ROLE (program_id, registered_user_id, application_role_id)
	SELECT program_id, viewer_id, "VIEWER"
	FROM PROGRAM_VIEWER_LINK
;

DROP TABLE PROGRAM_ADMINISTRATOR_LINK
;

DROP TABLE PROGRAM_APPROVER_LINK
;

DROP TABLE PROGRAM_VIEWER_LINK
;

CREATE TABLE PROJECT_USER_ROLE (
	project_id INT(10) UNSIGNED NOT NULL,
	registered_user_id INT(10) UNSIGNED NOT NULL,
	application_role_id VARCHAR(50),
	PRIMARY KEY(project_id, registered_user_id, application_role_id),
	INDEX (registered_user_id),
	INDEX (application_role_id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER (id),
	FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE (id)
) ENGINE = INNODB
;

INSERT INTO PROJECT_USER_ROLE (project_id, registered_user_id, application_role_id)
	SELECT id, administrator_id, "PROJECTADMINISTRATOR"
	FROM PROJECT
	WHERE disabled = 0
;

INSERT INTO PROJECT_USER_ROLE (project_id, registered_user_id, application_role_id)
	SELECT id, author_id, "PROJECTAUTHOR"
	FROM PROJECT
	WHERE disabled = 0
;

INSERT INTO PROJECT_USER_ROLE (project_id, registered_user_id, application_role_id)
	SELECT id, primary_supervisor_id, "PROJECTPRIMARYSUPERVISOR"
	FROM PROJECT
	WHERE disabled = 0
;

INSERT INTO PROJECT_USER_ROLE (project_id, registered_user_id, application_role_id)
	SELECT id, secondary_supervisor_id, "PROJECTSECONDARYSUPERVISOR"
	FROM PROJECT
	WHERE disabled = 0
		AND secondary_supervisor_id IS NOT NULL
;

UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
	ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
INNER JOIN PROJECT 
	ON APPLICATION_FORM.project_id = PROJECT.id
		AND PROJECT.primary_supervisor_id = APPLICATION_FORM_USER_ROLE.registered_user_id
SET APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTPRIMARYSUPERVISOR",
	APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 0
WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTADMINISTRATOR"
	AND PROJECT.administrator_id != PROJECT.primary_supervisor_id
;

DELETE APPLICATION_FORM_ACTION_REQUIRED.*
FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
	ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "PROJECTPRIMARYSUPERVISOR"
;

INSERT INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp)
	SELECT APPLICATION_FORM.id, PROJECT.primary_supervisor_id, "PROJECTPRIMARYSUPERVISOR", false,
		APPLICATION_FORM.last_updated
	FROM APPLICATION_FORM INNER JOIN PROJECT
		ON APPLICATION_FORM.project_id = PROJECT.id
	WHERE PROJECT.primary_supervisor_id = PROJECT.administrator_id
;

INSERT INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp)
	SELECT APPLICATION_FORM.id, PROJECT.secondary_supervisor_id, "PROJECTSECONDARYSUPERVISOR", false,
		APPLICATION_FORM.last_updated
	FROM APPLICATION_FORM INNER JOIN PROJECT
		ON APPLICATION_FORM.project_id = PROJECT.id
	WHERE PROJECT.secondary_supervisor_id IS NOT NULL
;

ALTER TABLE PROJECT
	DROP FOREIGN KEY project_administrator_registered_user_fk,
	DROP COLUMN administrator_id,
	DROP FOREIGN KEY project_author_fk,
	DROP COLUMN author_id,
	DROP FOREIGN KEY project_primary_supervisor_fk,
	DROP COLUMN primary_supervisor_id,
	DROP FOREIGN KEY project_secondary_supervisor_fk,
	DROP COLUMN secondary_supervisor_id
;

INSERT INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "SECONDARYSUPERVISOR", 
		false, APPLICATION_FORM.last_updated
	FROM APPLICATION_FORM INNER JOIN APPROVAL_ROUND 
		ON APPLICATION_FORM.id = APPROVAL_ROUND.application_form_id
	INNER JOIN SUPERVISOR
		ON APPROVAL_ROUND.id = SUPERVISOR.approval_round_id
	WHERE SUPERVISOR.is_primary = 0
	GROUP BY APPLICATION_FORM.id, SUPERVISOR.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp)
	SELECT COMMENT.application_form_id, SUPERVISOR.registered_user_id, "SECONDARYSUPERVISOR", 
		false, APPLICATION_FORM.last_updated
	FROM SUPERVISOR INNER JOIN SUPERVISION_CONFIRMATION_COMMENT
		ON SUPERVISOR.id = SUPERVISION_CONFIRMATION_COMMENT.secondary_supervisor_id
	INNER JOIN COMMENT
		ON SUPERVISION_CONFIRMATION_COMMENT.id = COMMENT.id
	INNER JOIN APPLICATION_FORM
		ON COMMENT.application_form_id = APPLICATION_FORM.id
	GROUP BY APPLICATION_FORM.id, SUPERVISOR.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp)
	SELECT COMMENT.application_form_id, SUPERVISOR.registered_user_id, "PRIMARYSUPERVISOR", 
		false, APPLICATION_FORM.last_updated
	FROM SUPERVISOR INNER JOIN OFFER_RECOMMENDED_COMMENT
		ON SUPERVISOR.id = OFFER_RECOMMENDED_COMMENT.supervisor_id
	INNER JOIN COMMENT
		ON OFFER_RECOMMENDED_COMMENT.id = COMMENT.id
	INNER JOIN APPLICATION_FORM
		ON COMMENT.application_form_id = APPLICATION_FORM.id
	GROUP BY APPLICATION_FORM.id, SUPERVISOR.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, update_timestamp)
	SELECT COMMENT.application_form_id, SUPERVISOR.registered_user_id, "SECONDARYSUPERVISOR", 
		false, APPLICATION_FORM.last_updated
	FROM SUPERVISOR INNER JOIN OFFER_RECOMMENDED_COMMENT
		ON SUPERVISOR.id = OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id
	INNER JOIN COMMENT
		ON OFFER_RECOMMENDED_COMMENT.id = COMMENT.id
	INNER JOIN APPLICATION_FORM
		ON COMMENT.application_form_id = APPLICATION_FORM.id
	GROUP BY APPLICATION_FORM.id, SUPERVISOR.registered_user_id
;

INSERT INTO SYSTEM_USER_ROLE (registered_user_id, application_role_id)
	SELECT registered_user_id, application_role_id
	FROM APPLICATION_FORM_USER_ROLE
	GROUP BY registered_user_id, application_role_id
;

INSERT IGNORE INTO SYSTEM_USER_ROLE (registered_user_id, application_role_id)
	SELECT registered_user_id, application_role_id
	FROM PROGRAM_USER_ROLE
	GROUP BY registered_user_id, application_role_id
;

INSERT IGNORE INTO SYSTEM_USER_ROLE (registered_user_id, application_role_id)
	SELECT registered_user_id, application_role_id
	FROM PROJECT_USER_ROLE
	GROUP BY registered_user_id, application_role_id
;

INSERT INTO SYSTEM_USER_ROLE (registered_user_id, application_role_id)
	SELECT registered_user_id, "SAFETYNET"
	FROM SYSTEM_USER_ROLE
	GROUP BY registered_user_id
;
