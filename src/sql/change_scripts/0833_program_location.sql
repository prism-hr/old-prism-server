CREATE TABLE PROGRAM_LOCATION (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	program_id INT(10) UNSIGNED NOT NULL,
	location TEXT NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (program_id, location),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id)
) ENGINE = INNODB
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	ADD COLUMN study_location TEXT AFTER id,
	ADD COLUMN study_division TEXT AFTER study_location,
	ADD COLUMN study_area TEXT AFTER study_division
;

ALTER TABLE APPLICATION_DOCUMENT
	ADD COLUMN research_statement_id  INT(10) UNSIGNED AFTER personal_statement_id,
	ADD INDEX (research_statement_id),
	ADD FOREIGN KEY (research_statement_id) REFERENCES DOCUMENT (id)
;

CREATE TABLE APPLICATION_PRIZE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	application_id INT(10) UNSIGNED NOT NULL,
	provider TEXT NOT NULL,
	title TEXT NOT NULL,
	description TEXT NOT NULL,
	award_date DATE NOT NULL, 
	PRIMARY KEY (id),
	INDEX (application_id),
	FOREIGN KEY (application_id) REFERENCES APPLICATION (id)
) ENGINE = INNODB
;
