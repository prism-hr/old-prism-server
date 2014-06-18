ALTER TABLE PROGRAM_EXPORT
	DROP FOREIGN KEY program_export_ibfk_1,
	CHANGE COLUMN program_export_format_id program_export_format VARCHAR(50) NOT NULL
;

ALTER TABLE PROGRAM
	DROP FOREIGN KEY program_ibfk_1,
	CHANGE COLUMN program_type_id program_type VARCHAR(50) NOT NULL AFTER institution_id
;

ALTER TABLE PROGRAM
	DROP FOREIGN KEY program_ibfk_1,
	CHANGE COLUMN program_type_id program_type VARCHAR(50) NOT NULL AFTER institution_id
;

DROP TABLE PROGRAM_STUDY_DURATION
;

DROP TABLE PROGRAM_TYPE
;

ALTER TABLE ADVERT
	MODIFY COLUMN description TEXT,
	CHANGE COLUMN study_duration month_study_duration INT(4) UNSIGNED,
	MODIFY COLUMN funding TEXT
;

UPDATE ADVERT
SET description = CONCAT(description, funding)
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type, code, name, enabled)
	SELECT 5243, "STUDY_OPTION", id, display_name, 1
	FROM PROGRAM_STUDY_OPTION
;

ALTER TABLE PROGRAM_INSTANCE
	MODIFY COLUMN program_id INT(10) UNSIGNED NOT NULL AFTER id,
	ADD COLUMN study_option_id INT(10) UNSIGNED AFTER program_id,
	ADD INDEX (study_option_id),
	ADD FOREIGN KEY (study_option_id) REFERENCES IMPORTED_ENTITY (id)
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	ADD COLUMN study_option_id INT(10) UNSIGNED AFTER id,
	ADD INDEX (study_option_id),
	ADD FOREIGN KEY (study_option_id) REFERENCES IMPORTED_ENTITY (id)
;

UPDATE PROGRAM_INSTANCE INNER JOIN IMPORTED_ENTITY
	ON PROGRAM_INSTANCE.program_study_option_id = IMPORTED_ENTITY.code
	AND IMPORTED_ENTITY.imported_entity_type = "STUDY_OPTION"
SET PROGRAM_INSTANCE.study_option_id = IMPORTED_ENTITY.id
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PROGRAM_DETAIL.program_study_option_id = IMPORTED_ENTITY.code
	AND IMPORTED_ENTITY.imported_entity_type = "STUDY_OPTION"
SET APPLICATION_PROGRAM_DETAIL.study_option_id = IMPORTED_ENTITY.id
;

ALTER TABLE PROGRAM_INSTANCE
	DROP INDEX program_id,
	ADD UNIQUE INDEX (program_id, academic_year, study_option_id),
	DROP FOREIGN KEY program_instance_ibfk_1,
	DROP COLUMN program_study_option_id
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	DROP FOREIGN KEY application_program_detail_ibfk_1,
	DROP COLUMN program_study_option_id
;

DROP TABLE PROGRAM_STUDY_OPTION
;

ALTER TABLE ROLE_TRANSITION
	DROP FOREIGN KEY role_transition_ibfk_3,
	CHANGE COLUMN role_transition_type_id role_transition_type VARCHAR(50) NOT NULL
;

DROP TABLE ROLE_TRANSITION_TYPE
;

ALTER TABLE USER_INSTITUTION_IDENTITY
	DROP FOREIGN KEY user_institution_identity_ibfk_3,
	CHANGE COLUMN user_identity_type_id user_identity_type VARCHAR(50) NOT NULL
;

DROP TABLE USER_IDENTITY_TYPE
;
