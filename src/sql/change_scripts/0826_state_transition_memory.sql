CREATE TABLE RESOURCE_STATE_TRANSITION_SUMMARY (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	project_id INT(10) UNSIGNED,
	state_group_id VARCHAR(50) NOT NULL,
	transition_state_selection VARCHAR(255) NOT NULL,
	frequency INT(10) UNSIGNED NOT NULL,
	updated_timestamp DATETIME NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, state_group_id, transition_state_selection),
	UNIQUE INDEX (institution_id, state_group_id, transition_state_selection),
	UNIQUE INDEX (program_id, state_group_id, transition_state_selection),
	UNIQUE INDEX (project_id, state_group_id, transition_state_selection),
	INDEX (state_group_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (state_group_id) REFERENCES STATE_GROUP (id)
) ENGINE = INNODB
;

CREATE TABLE COMMENT_STATE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	comment_id INT(10) UNSIGNED NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	primary_state INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (comment_id, state_id),
	INDEX (state_id),
	FOREIGN KEY (comment_id) REFERENCES COMMENT (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id)
) ENGINE = INNODB
;

CREATE TABLE RESOURCE_PREVIOUS_STATE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED NULL DEFAULT NULL,
	institution_id INT(10) UNSIGNED NULL DEFAULT NULL,
	program_id INT(10) UNSIGNED NULL DEFAULT NULL,
	project_id INT(10) UNSIGNED NULL DEFAULT NULL,
	application_id INT(10) UNSIGNED NULL DEFAULT NULL,
	previous_state_id VARCHAR(50) NOT NULL,
	primary_state INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, previous_state_id),
	UNIQUE INDEX institution_id (institution_id, previous_state_id),
	UNIQUE INDEX (program_id, previous_state_id),
	UNIQUE INDEX (project_id, previous_state_id),
	UNIQUE INDEX (application_id, previous_state_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (project_id) REFERENCES PROJECT (id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id)
) ENGINE = INNODB
;

ALTER TABLE RESOURCE_STATE
	DROP INDEX system_id,
	DROP INDEX institution_id,
	DROP INDEX program_id,
	DROP INDEX project_id,
	DROP INDEX application_id,
	ADD UNIQUE INDEX system_id (system_id, state_id),
	ADD UNIQUE INDEX institution_id (institution_id, state_id),
	ADD UNIQUE INDEX program_id (program_id, state_id),
	ADD UNIQUE INDEX project_id (project_id, state_id),
	ADD UNIQUE INDEX application_id (application_id, state_id)
;
