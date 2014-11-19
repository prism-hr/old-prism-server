SET FOREIGN_KEY_CHECKS = 0
;

ALTER TABLE SYSTEM_USER_ROLE
	ADD INDEX (registered_user_id),
	DROP PRIMARY KEY
;

ALTER TABLE INSTITUTION_USER_ROLE
	ADD INDEX (institution_id),
	DROP PRIMARY KEY
;

ALTER TABLE PROGRAM_USER_ROLE
	ADD INDEX (program_id),
	DROP PRIMARY KEY
;

ALTER TABLE PROJECT_USER_ROLE
	ADD INDEX (project_id),
	DROP PRIMARY KEY
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	ADD INDEX (application_form_id),
	DROP PRIMARY KEY
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN ADVERT
	ON REGISTERED_USER.id = ADVERT.registered_user_id
SET ADVERT.registered_user_id = USER.id
;

ALTER TABLE ADVERT
	DROP FOREIGN KEY advert_ibfk_1,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_advert_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN APPLICATION
	ON REGISTERED_USER.id = APPLICATION.registered_user_id
SET APPLICATION.registered_user_id = USER.id
;

ALTER TABLE APPLICATION
	DROP FOREIGN KEY applicant_fk,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_application_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN APPLICATION_ACTION_REQUIRED
	ON REGISTERED_USER.id = APPLICATION_ACTION_REQUIRED.registered_user_id
SET APPLICATION_ACTION_REQUIRED.registered_user_id = USER.id
;

ALTER TABLE APPLICATION_ACTION_REQUIRED
	DROP FOREIGN KEY application_action_required_ibfk_2,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_application_action_required_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN APPLICATION_FORM_REFEREE
	ON REGISTERED_USER.id = APPLICATION_FORM_REFEREE.registered_user_id
SET APPLICATION_FORM_REFEREE.registered_user_id = USER.id
;

ALTER TABLE APPLICATION_FORM_REFEREE
	DROP FOREIGN KEY referee_user_fk,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_application_form_referee_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN APPLICATION_FORM_USER_ROLE
	ON REGISTERED_USER.id = APPLICATION_FORM_USER_ROLE.registered_user_id
SET APPLICATION_FORM_USER_ROLE.registered_user_id = USER.id
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	DROP FOREIGN KEY APPLICATION_FORM_USER_ROLE_ibfk_2,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_application_form_user_role_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN COMMENT
	ON REGISTERED_USER.id = COMMENT.user_id
SET COMMENT.user_id = USER.id
;

ALTER TABLE COMMENT
	DROP FOREIGN KEY review_user_fk,
	CHANGE COLUMN user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_comment_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN EVENT
	ON REGISTERED_USER.id = EVENT.registered_user_id
SET EVENT.registered_user_id = USER.id
;

ALTER TABLE EVENT
	DROP FOREIGN KEY event_user_fk,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_event_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN INSTITUTION_USER_ROLE
	ON REGISTERED_USER.id = INSTITUTION_USER_ROLE.registered_user_id
SET INSTITUTION_USER_ROLE.registered_user_id = USER.id
;

ALTER TABLE INSTITUTION_USER_ROLE
	DROP FOREIGN KEY institution_user_role_ibfk_2,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_institution_user_role_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN INTERVIEW_PARTICIPANT
	ON REGISTERED_USER.id = INTERVIEW_PARTICIPANT.user_id
SET INTERVIEW_PARTICIPANT.user_id = USER.id
;

ALTER TABLE INTERVIEW_PARTICIPANT
	DROP FOREIGN KEY interview_participant_user_fk,
	CHANGE COLUMN user_id user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_interview_participant_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN INTERVIEWER
	ON REGISTERED_USER.id = INTERVIEWER.registered_user_id
SET INTERVIEWER.registered_user_id = USER.id
;

ALTER TABLE INTERVIEWER
	DROP FOREIGN KEY user_interviewer_fkey,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_interviewer_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN OPPORTUNITY_REQUEST
	ON REGISTERED_USER.id = OPPORTUNITY_REQUEST.author_id
SET OPPORTUNITY_REQUEST.author_id = USER.id
;

ALTER TABLE OPPORTUNITY_REQUEST
	DROP FOREIGN KEY opportunity_request_author_id,
	CHANGE COLUMN author_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_opportunity_request_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN OPPORTUNITY_REQUEST_COMMENT
	ON REGISTERED_USER.id = OPPORTUNITY_REQUEST_COMMENT.author_id
SET OPPORTUNITY_REQUEST_COMMENT.author_id = USER.id
;

ALTER TABLE OPPORTUNITY_REQUEST_COMMENT
	DROP FOREIGN KEY opportunity_request_comment_author_fk,
	CHANGE COLUMN author_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_opportunity_request_comment_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN PENDING_ROLE_NOTIFICATION
	ON REGISTERED_USER.id = PENDING_ROLE_NOTIFICATION.user_id
SET PENDING_ROLE_NOTIFICATION.user_id = USER.id
;

ALTER TABLE PENDING_ROLE_NOTIFICATION
	DROP FOREIGN KEY user_pending_not_fk,
	CHANGE COLUMN user_id user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_pending_role_notification_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN PENDING_ROLE_NOTIFICATION
	ON REGISTERED_USER.id = PENDING_ROLE_NOTIFICATION.added_by_user_id
SET PENDING_ROLE_NOTIFICATION.added_by_user_id = USER.id
;

ALTER TABLE PENDING_ROLE_NOTIFICATION
	DROP FOREIGN KEY pendin_role_added_fk,
	CHANGE COLUMN added_by_user_id added_by_user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_pending_role_notification_added_by_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN PROGRAM_USER_ROLE
	ON REGISTERED_USER.id = PROGRAM_USER_ROLE.registered_user_id
SET PROGRAM_USER_ROLE.registered_user_id = USER.id
;

ALTER TABLE PROGRAM_USER_ROLE
	DROP FOREIGN KEY program_user_role_ibfk_2,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_program_user_role_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN PROJECT_USER_ROLE
	ON REGISTERED_USER.id = PROJECT_USER_ROLE.registered_user_id
SET PROJECT_USER_ROLE.registered_user_id = USER.id
;

ALTER TABLE PROJECT_USER_ROLE
	DROP FOREIGN KEY project_user_role_ibfk_2,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_project_user_role_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN REFERENCE_COMMENT
	ON REGISTERED_USER.id = REFERENCE_COMMENT.provided_by
SET REFERENCE_COMMENT.provided_by = USER.id
;

ALTER TABLE REFERENCE_COMMENT
	DROP FOREIGN KEY provided_by_fk,
	CHANGE COLUMN provided_by user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_reference_comment_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN RESEARCH_OPPORTUNITIES_FEED
	ON REGISTERED_USER.id = RESEARCH_OPPORTUNITIES_FEED.registered_user_id
SET RESEARCH_OPPORTUNITIES_FEED.registered_user_id = USER.id
;

ALTER TABLE RESEARCH_OPPORTUNITIES_FEED
	DROP FOREIGN KEY research_opportunities_feed_ibfk_1,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_research_opportunities_feed_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN REVIEWER
	ON REGISTERED_USER.id = REVIEWER.registered_user_id
SET REVIEWER.registered_user_id = USER.id
;

ALTER TABLE REVIEWER
	DROP FOREIGN KEY user_reviewer_fk,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_reviewer_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN STATECHANGE_COMMENT
	ON REGISTERED_USER.id = STATECHANGE_COMMENT.delegate_administrator_id
SET STATECHANGE_COMMENT.delegate_administrator_id = USER.id
;

ALTER TABLE STATECHANGE_COMMENT
	DROP FOREIGN KEY STATECHANGE_COMMENT_ibfk_1,
	CHANGE COLUMN delegate_administrator_id user_id INT(10) UNSIGNED,
	ADD CONSTRAINT fk_statechange_comment_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

ALTER TABLE SUGGESTED_SUPERVISOR
	ADD INDEX (id),
	DROP PRIMARY KEY
;

UPDATE USER INNER JOIN PERSON
	ON USER.email = PERSON.email
INNER JOIN SUGGESTED_SUPERVISOR
	ON PERSON.id = SUGGESTED_SUPERVISOR.id
SET SUGGESTED_SUPERVISOR.id = USER.id
;

ALTER TABLE SUGGESTED_SUPERVISOR
	DROP FOREIGN KEY suggested_supervisor_fk,
	CHANGE COLUMN id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_suggested_supervisor_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN SUPERVISOR
	ON REGISTERED_USER.id = SUPERVISOR.registered_user_id
SET SUPERVISOR.registered_user_id = USER.id
;

ALTER TABLE SUPERVISOR
	DROP FOREIGN KEY user_supervisor_fk,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_supervisor_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN SYSTEM_USER_ROLE
	ON REGISTERED_USER.id = SYSTEM_USER_ROLE.registered_user_id
SET SYSTEM_USER_ROLE.registered_user_id = USER.id
;

ALTER TABLE SYSTEM_USER_ROLE
	DROP FOREIGN KEY system_user_role_ibfk_1,
	CHANGE COLUMN registered_user_id user_id INT(10) UNSIGNED NOT NULL,
	ADD CONSTRAINT fk_system_user_role_user_id FOREIGN KEY (user_id) REFERENCES USER (id)
;

ALTER TABLE SYSTEM_USER_ROLE
	ADD PRIMARY KEY (user_id, application_role_id),
	DROP INDEX registered_user_id
;

ALTER TABLE INSTITUTION_USER_ROLE
	ADD PRIMARY KEY (institution_id, user_id, application_role_id),
	DROP INDEX institution_id
;

ALTER TABLE PROGRAM_USER_ROLE
	ADD PRIMARY KEY (program_id, user_id, application_role_id),
	DROP INDEX program_id
;

ALTER TABLE PROJECT_USER_ROLE
	ADD PRIMARY KEY (project_id, user_id, application_role_id),
	DROP INDEX project_id
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	ADD PRIMARY KEY (application_form_id, user_id, application_role_id),
	DROP INDEX application_form_id
;

SET FOREIGN_KEY_CHECKS = 1
;

ALTER TABLE APPLICATION_FORM_UPDATE
	ADD INDEX (application_form_id),
	DROP PRIMARY KEY,
	DROP INDEX registered_user_id
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN APPLICATION_FORM_UPDATE
	ON REGISTERED_USER.id = APPLICATION_FORM_UPDATE.registered_user_id
SET APPLICATION_FORM_UPDATE.registered_user_id = USER.id
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
SET action_id = IF(direct_to_url LIKE "/referee%", "PROVIDE_REFERENCE", "PROVIDE_REVIEW"),
	application_id = (
		SELECT id
		FROM APPLICATION
		WHERE application_number = SUBSTRING(direct_to_url, POSITION("=" IN direct_to_url) + 1))
WHERE REGISTERED_USER.direct_to_url IS NOT NULL
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
SET action_id = "COMPLETE_APPLICATION",
	advert_id = REPLACE(SUBSTRING(REPLACE(REPLACE(REPLACE(SUBSTRING(original_querystring, 
		POSITION("advert" IN REGISTERED_USER.original_querystring)), "advert:", ""), "||", "|"), 
		"project:", ""), 1, 2), "|", "")
WHERE REGISTERED_USER.original_querystring LIKE "%advert%"
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
SET action_id = "COMPLETE_APPLICATION",
	advert_id = (
		SELECT PROGRAM.id
		FROM PROGRAM
		WHERE PROGRAM.code = (
			IF(SUBSTRING(REGISTERED_USER.original_querystring, 9, 12) LIKE "ABC%",
				"ABC",
				SUBSTRING(REGISTERED_USER.original_querystring, 9, 12))))
WHERE REGISTERED_USER.original_querystring LIKE "%program%"
	AND USER.advert_id IS NULL
;

DROP TABLE REGISTERED_USER
;

DROP TABLE PERSON
;
