CREATE TABLE USER_ACCOUNT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	password VARCHAR(32) NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL DEFAULT 0,
	first_name_2 VARCHAR(30),
	first_name_3 VARCHAR(30),
	application_filter_group_id INT(10) UNSIGNED,
	application_list_last_access_timestamp DATETIME,
	email VARCHAR(255) NOT NULL,
	PRIMARY KEY(id),
	INDEX (password),
	INDEX (application_filter_group_id),
	FOREIGN KEY (application_filter_group_id) REFERENCES APPLICATION_FILTER_GROUP (id)
) ENGINE = INNODB
;

INSERT INTO USER_ACCOUNT (password, enabled, first_name_2, first_name_3, 
	application_filter_group_id, application_list_last_access_timestamp, email)
	SELECT password, enabled, firstName2, firstName3, filtering_id, 
		application_list_last_access_timestamp, email
	FROM REGISTERED_USER
	WHERE password IS NOT NULL
;	

CREATE TABLE USER (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	first_name VARCHAR(30) NOT NULL,
	last_name VARCHAR(40) NOT NULL,
	email VARCHAR(255) NOT NULL,
	activation_code VARCHAR(40) NOT NULL,
	action_id VARCHAR(50),
	advert_id INT(10) UNSIGNED,
	application_id INT(10) UNSIGNED,
	is_system_user INT(1) UNSIGNED DEFAULT 0,
	user_id INT(10) UNSIGNED,
	user_account_id INT(10) UNSIGNED,
	PRIMARY KEY (id),
	UNIQUE INDEX (email),
	INDEX (last_name, first_name),
	UNIQUE INDEX (activation_code),
	INDEX (user_id),
	INDEX (action_id),
	INDEX (advert_id),
	INDEX (application_id),
	FOREIGN KEY (user_id) REFERENCES USER (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id)
) ENGINE = INNODB
;

INSERT INTO USER (first_name, last_name, email, activation_code)
	SELECT firstname, lastname, email, UUID()
	FROM PERSON
	GROUP BY email
;

INSERT INTO USER (first_name, last_name, email, activation_code)
	SELECT firstName, lastName, email, 
		IF (activationCode IS NOT NULL, activationCode, UUID())
	FROM REGISTERED_USER
	ON DUPLICATE KEY UPDATE
		first_name = firstName,
		last_name = lastName,
		activation_code = 
			IF (activationCode IS NOT NULL, activationCode, UUID())
;

INSERT IGNORE INTO USER (first_name, last_name, email, activation_code)
	SELECT firstname, lastname, email, UUID()
	FROM APPLICATION_FORM_REFEREE
	WHERE registered_user_id IS NULL
;

UPDATE USER INNER JOIN REGISTERED_USER
	ON USER.email = REGISTERED_USER.email
INNER JOIN REGISTERED_USER AS PRIMARY_REGISTERED_USER
	ON REGISTERED_USER.primary_account_id = PRIMARY_REGISTERED_USER.id
INNER JOIN USER AS PRIMARY_USER
	ON PRIMARY_REGISTERED_USER.email = PRIMARY_USER.email
SET USER.user_id = PRIMARY_USER.id
;

UPDATE USER
SET user_id = id
WHERE user_id IS NULL
;

UPDATE USER
SET is_system_user = 1
WHERE email = "prism@ucl.ac.uk"
;

UPDATE USER INNER JOIN USER_ACCOUNT
	ON USER.email = USER_ACCOUNT.email
SET USER.user_account_id = USER_ACCOUNT.id
;

ALTER TABLE USER_ACCOUNT
	DROP COLUMN email
;

CREATE TABLE USER_IDENTITY_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "STUDY_APPLICANT" AS id
		UNION
	SELECT "STAFF_PROFILE" AS id
;

CREATE TABLE USER_INSTITUTION_IDENTITY (
	user_id INT(10) UNSIGNED NOT NULL,
	institution_id INT(10) UNSIGNED NOT NULL,
	user_identity_type_id VARCHAR(50) NOT NULL,
	identifier VARCHAR(50) NOT NULL,
	PRIMARY KEY (user_id, institution_id, user_identity_type_id),
	INDEX (institution_id),
	INDEX (user_identity_type_id),
	FOREIGN KEY (user_id) REFERENCES USER (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (user_identity_type_id) REFERENCES USER_IDENTITY_TYPE (id)
) ENGINE = INNODB
;

INSERT INTO USER_INSTITUTION_IDENTITY
	SELECT USER.id, 5243, "STUDY_APPLICANT", REGISTERED_USER.ucl_user_id
	FROM USER INNER JOIN REGISTERED_USER
		ON USER.email = REGISTERED_USER.email
	WHERE REGISTERED_USER.ucl_user_id IS NOT NULL
;

INSERT INTO USER_INSTITUTION_IDENTITY
	SELECT USER.id, 5243, "STAFF_PROFILE", REGISTERED_USER.upi
	FROM USER INNER JOIN REGISTERED_USER
		ON USER.email = REGISTERED_USER.email
	WHERE REGISTERED_USER.upi IS NOT NULL
;

CREATE TABLE NOTIFICATION_PURPOSE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "UPDATE" AS id
		UNION
	SELECT "TASK" AS id
;

CREATE TABLE USER_BATCH_NOTIFICATION (
	user_id INT(10) UNSIGNED NOT NULL,
	application_role_scope_id VARCHAR(50) NOT NULL,
	notification_purpose_id VARCHAR(50) NOT NULL,
	last_notification_timestamp DATETIME,
	PRIMARY KEY (user_id, application_role_scope_id, notification_purpose_id),
	INDEX (application_role_scope_id),
	INDEX (notification_purpose_id),
	FOREIGN KEY (user_id) REFERENCES USER (id),
	FOREIGN KEY (application_role_scope_id) REFERENCES APPLICATION_ROLE_SCOPE (id),
	FOREIGN KEY (notification_purpose_id) REFERENCES NOTIFICATION_PURPOSE (id)
) ENGINE = INNODB
;

INSERT INTO USER_BATCH_NOTIFICATION
	SELECT USER.id, "APPLICATION", "TASK", REGISTERED_USER.latest_task_notification_date
	FROM USER INNER JOIN REGISTERED_USER
		ON USER.email = REGISTERED_USER.email
	WHERE REGISTERED_USER.latest_task_notification_date IS NOT NULL
;

INSERT INTO USER_BATCH_NOTIFICATION
	SELECT USER.id, "APPLICATION", "UPDATE", 
		REGISTERED_USER.latest_update_notification_date
	FROM USER INNER JOIN REGISTERED_USER
		ON USER.email = REGISTERED_USER.email
	WHERE REGISTERED_USER.latest_update_notification_date IS NOT NULL
;

INSERT INTO USER_BATCH_NOTIFICATION
	SELECT USER.id, "PROGRAM", "TASK", 
		REGISTERED_USER.latest_opportunity_request_notification_date
	FROM USER INNER JOIN REGISTERED_USER
		ON USER.email = REGISTERED_USER.email
	WHERE REGISTERED_USER.latest_opportunity_request_notification_date IS NOT NULL
;
