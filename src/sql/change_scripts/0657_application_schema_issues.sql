CREATE TABLE IMPORTED_ENTITY_TYPE (
	id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB
	SELECT "COUNTRY" AS id
		UNION
	SELECT "DISABILITY"
		UNION
	SELECT "DOMICILE"
		UNION
	SELECT "ETHNICITY"
		UNION
	SELECT "NATIONALITY"
		UNION
	SELECT "QUALIFICATION_TYPE"
		UNION
	SELECT "REFERRAL_SOURCE"
;

CREATE TABLE IMPORTED_ENTITY (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	institution_id INT(10) UNSIGNED NOT NULL,
	imported_entity_type_id VARCHAR(50) NOT NULL,
	code VARCHAR(10) NOT NULL,
	name VARCHAR(100) NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL,
	old_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (institution_id, imported_entity_type_id, code),
	UNIQUE INDEX (institution_id, imported_entity_type_id, name),
	INDEX (imported_entity_type_id),
	INDEX (enabled),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (imported_entity_type_id) REFERENCES IMPORTED_ENTITY_TYPE (id)
) ENGINE = INNODB
;

/* Country import */

ALTER TABLE COUNTRY
	DROP FOREIGN KEY countries_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN COUNTRY
	ON APPLICATION_PERSONAL_DETAIL.country_id = COUNTRY.id
INNER JOIN COUNTRY AS ALTERNATE_COUNTRY
	ON COUNTRY.code = ALTERNATE_COUNTRY.code
SET APPLICATION_PERSONAL_DETAIL.country_id = ALTERNATE_COUNTRY.id
WHERE COUNTRY.enabled = 0
	AND ALTERNATE_COUNTRY.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN COUNTRY
	ON APPLICATION_PERSONAL_DETAIL.country_id = COUNTRY.id
INNER JOIN COUNTRY AS ALTERNATE_COUNTRY
	ON COUNTRY.name = ALTERNATE_COUNTRY.name
SET APPLICATION_PERSONAL_DETAIL.country_id = ALTERNATE_COUNTRY.id
WHERE COUNTRY.enabled = 0
	AND ALTERNATE_COUNTRY.enabled = 1
;

DELETE COUNTRY.*
FROM COUNTRY LEFT JOIN APPLICATION_PERSONAL_DETAIL
	ON COUNTRY.id = APPLICATION_PERSONAL_DETAIL.country_id
WHERE COUNTRY.enabled = 0
	AND APPLICATION_PERSONAL_DETAIL.id IS NULL
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN COUNTRY
	ON APPLICATION_PERSONAL_DETAIL.country_id = COUNTRY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM COUNTRY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON COUNTRY.code = MERGE_OBJECT.code
SET APPLICATION_PERSONAL_DETAIL.country_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN COUNTRY
	ON APPLICATION_PERSONAL_DETAIL.country_id = COUNTRY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM COUNTRY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON COUNTRY.name = MERGE_OBJECT.name
SET APPLICATION_PERSONAL_DETAIL.country_id = MERGE_OBJECT.id
;

DELETE COUNTRY.* 
FROM COUNTRY INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM COUNTRY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON COUNTRY.code = MERGE_OBJECT.code
WHERE COUNTRY.id != MERGE_OBJECT.id
;

DELETE COUNTRY.* 
FROM COUNTRY INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM COUNTRY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON COUNTRY.name = MERGE_OBJECT.name
WHERE COUNTRY.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "COUNTRY", name, code, enabled, id
	FROM COUNTRY
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP FOREIGN KEY country_fk
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.country_id = IMPORTED_ENTITY.old_id
SET APPLICATION_PERSONAL_DETAIL.country_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "COUNTRY"
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD FOREIGN KEY (country_id) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE COUNTRY
;


/* Disability import */

ALTER TABLE DISABILITY
	DROP FOREIGN KEY disability_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DISABILITY
	ON APPLICATION_PERSONAL_DETAIL.disability_id = DISABILITY.id
INNER JOIN DISABILITY AS ALTERNATE_DISABILITY
	ON DISABILITY.code = ALTERNATE_DISABILITY.code
SET APPLICATION_PERSONAL_DETAIL.disability_id = ALTERNATE_DISABILITY.id
WHERE DISABILITY.enabled = 0
	AND ALTERNATE_DISABILITY.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DISABILITY
	ON APPLICATION_PERSONAL_DETAIL.disability_id = DISABILITY.id
INNER JOIN DISABILITY AS ALTERNATE_DISABILITY
	ON DISABILITY.name = ALTERNATE_DISABILITY.name
SET APPLICATION_PERSONAL_DETAIL.disability_id = ALTERNATE_DISABILITY.id
WHERE DISABILITY.enabled = 0
	AND ALTERNATE_DISABILITY.enabled = 1
;

DELETE DISABILITY.*
FROM DISABILITY LEFT JOIN APPLICATION_PERSONAL_DETAIL
	ON DISABILITY.id = APPLICATION_PERSONAL_DETAIL.disability_id
WHERE DISABILITY.enabled = 0
	AND APPLICATION_PERSONAL_DETAIL.id IS NULL
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DISABILITY
	ON APPLICATION_PERSONAL_DETAIL.disability_id = DISABILITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM DISABILITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DISABILITY.code = MERGE_OBJECT.code
SET APPLICATION_PERSONAL_DETAIL.disability_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DISABILITY
	ON APPLICATION_PERSONAL_DETAIL.disability_id = DISABILITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM DISABILITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DISABILITY.name = MERGE_OBJECT.name
SET APPLICATION_PERSONAL_DETAIL.disability_id = MERGE_OBJECT.id
;

DELETE DISABILITY.* 
FROM DISABILITY INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM DISABILITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DISABILITY.name = MERGE_OBJECT.name
WHERE DISABILITY.id != MERGE_OBJECT.id
;

DELETE DISABILITY.* 
FROM DISABILITY INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM DISABILITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DISABILITY.code = MERGE_OBJECT.code
WHERE DISABILITY.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "DISABILITY", name, code, enabled, id
	FROM DISABILITY
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP FOREIGN KEY user_disability_fk
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.disability_id = IMPORTED_ENTITY.old_id
SET APPLICATION_PERSONAL_DETAIL.disability_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "DISABILITY"
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD FOREIGN KEY (disability_id) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE DISABILITY
;

/* Ethnicity import */

ALTER TABLE ETHNICITY
	DROP FOREIGN KEY ethnicity_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN ETHNICITY
	ON APPLICATION_PERSONAL_DETAIL.ethnicity_id = ETHNICITY.id
INNER JOIN ETHNICITY AS ALTERNATE_ETHNICITY
	ON ETHNICITY.code = ALTERNATE_ETHNICITY.code
SET APPLICATION_PERSONAL_DETAIL.ethnicity_id = ALTERNATE_ETHNICITY.id
WHERE ETHNICITY.enabled = 0
	AND ALTERNATE_ETHNICITY.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN ETHNICITY
	ON APPLICATION_PERSONAL_DETAIL.ethnicity_id = ETHNICITY.id
INNER JOIN ETHNICITY AS ALTERNATE_ETHNICITY
	ON ETHNICITY.name = ALTERNATE_ETHNICITY.name
SET APPLICATION_PERSONAL_DETAIL.ethnicity_id = ALTERNATE_ETHNICITY.id
WHERE ETHNICITY.enabled = 0
	AND ALTERNATE_ETHNICITY.enabled = 1
;

DELETE ETHNICITY.*
FROM ETHNICITY LEFT JOIN APPLICATION_PERSONAL_DETAIL
	ON ETHNICITY.id = APPLICATION_PERSONAL_DETAIL.ethnicity_id
WHERE ETHNICITY.enabled = 0
	AND APPLICATION_PERSONAL_DETAIL.id IS NULL
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN ETHNICITY
	ON APPLICATION_PERSONAL_DETAIL.ethnicity_id = ETHNICITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM ETHNICITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON ETHNICITY.code = MERGE_OBJECT.code
SET APPLICATION_PERSONAL_DETAIL.ethnicity_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN ETHNICITY
	ON APPLICATION_PERSONAL_DETAIL.ethnicity_id = ETHNICITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM ETHNICITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON ETHNICITY.name = MERGE_OBJECT.name
SET APPLICATION_PERSONAL_DETAIL.ethnicity_id = MERGE_OBJECT.id
;

DELETE ETHNICITY.* 
FROM ETHNICITY INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM ETHNICITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON ETHNICITY.code = MERGE_OBJECT.code
WHERE ETHNICITY.id != MERGE_OBJECT.id
;

DELETE ETHNICITY.* 
FROM ETHNICITY INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM ETHNICITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON ETHNICITY.name = MERGE_OBJECT.name
WHERE ETHNICITY.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "ETHNICITY", name, code, enabled, id
	FROM ETHNICITY
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP FOREIGN KEY user_ethnicity_fk
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.ethnicity_id = IMPORTED_ENTITY.old_id
SET APPLICATION_PERSONAL_DETAIL.ethnicity_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "ETHNICITY"
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD FOREIGN KEY (ethnicity_id) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE ETHNICITY
;

/* Clean up institution */

ALTER TABLE INSTITUTION
	DROP INDEX domicile_code_idx,
	ADD UNIQUE INDEX (code)
;

/* Domicile import */

ALTER TABLE DOMICILE
	DROP FOREIGN KEY domicile_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE ADDRESS INNER JOIN DOMICILE
	ON ADDRESS.domicile_id = DOMICILE.id
INNER JOIN DOMICILE AS ALTERNATE_DOMICILE
	ON DOMICILE.code = ALTERNATE_DOMICILE.code
SET ADDRESS.domicile_id = ALTERNATE_DOMICILE.id
WHERE DOMICILE.enabled = 0
	AND ALTERNATE_DOMICILE.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DOMICILE
	ON APPLICATION_PERSONAL_DETAIL.domicile_id = DOMICILE.id
INNER JOIN DOMICILE AS ALTERNATE_DOMICILE
	ON DOMICILE.code = ALTERNATE_DOMICILE.code
SET APPLICATION_PERSONAL_DETAIL.domicile_id = ALTERNATE_DOMICILE.id
WHERE DOMICILE.enabled = 0
	AND ALTERNATE_DOMICILE.enabled = 1
;

UPDATE INSTITUTION INNER JOIN DOMICILE
	ON INSTITUTION.domicile_id = DOMICILE.id
INNER JOIN DOMICILE AS ALTERNATE_DOMICILE
	ON DOMICILE.code = ALTERNATE_DOMICILE.code
SET INSTITUTION.domicile_id = ALTERNATE_DOMICILE.id
WHERE DOMICILE.enabled = 0
	AND ALTERNATE_DOMICILE.enabled = 1
;

DELETE DOMICILE.* 
FROM DOMICILE LEFT JOIN ADDRESS
	ON DOMICILE.id = ADDRESS.domicile_id
LEFT JOIN APPLICATION_PERSONAL_DETAIL
	ON DOMICILE.id = APPLICATION_PERSONAL_DETAIL.id
LEFT JOIN INSTITUTION
	ON DOMICILE.id = INSTITUTION.domicile_id
WHERE DOMICILE.enabled = 0
	AND ADDRESS.id IS NULL
	AND APPLICATION_PERSONAL_DETAIL.id IS NULL
	AND INSTITUTION.id IS NULL
;

UPDATE ADDRESS INNER JOIN DOMICILE
	ON ADDRESS.domicile_id = DOMICILE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM DOMICILE
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.code = MERGE_OBJECT.code
SET ADDRESS.domicile_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DOMICILE
	ON APPLICATION_PERSONAL_DETAIL.domicile_id = DOMICILE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM DOMICILE
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.code = MERGE_OBJECT.code
SET APPLICATION_PERSONAL_DETAIL.domicile_id = MERGE_OBJECT.id
;

UPDATE INSTITUTION INNER JOIN DOMICILE
	ON INSTITUTION.domicile_id = DOMICILE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM DOMICILE
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.code = MERGE_OBJECT.code
SET INSTITUTION.domicile_id = MERGE_OBJECT.id
;

UPDATE ADDRESS INNER JOIN DOMICILE
	ON ADDRESS.domicile_id = DOMICILE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM DOMICILE
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.name = MERGE_OBJECT.name
SET ADDRESS.domicile_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN DOMICILE
	ON APPLICATION_PERSONAL_DETAIL.domicile_id = DOMICILE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM DOMICILE
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.name = MERGE_OBJECT.name
SET APPLICATION_PERSONAL_DETAIL.domicile_id = MERGE_OBJECT.id
;

UPDATE INSTITUTION INNER JOIN DOMICILE
	ON INSTITUTION.domicile_id = DOMICILE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM DOMICILE
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.name = MERGE_OBJECT.name
SET INSTITUTION.domicile_id = MERGE_OBJECT.id
;

DELETE DOMICILE.* 
FROM DOMICILE INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM DOMICILE
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.code = MERGE_OBJECT.code
WHERE DOMICILE.id != MERGE_OBJECT.id
;

DELETE DOMICILE.* 
FROM DOMICILE INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM DOMICILE
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON DOMICILE.name = MERGE_OBJECT.name
WHERE DOMICILE.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "DOMICILE", name, code, enabled, id
	FROM DOMICILE
;

ALTER TABLE ADDRESS
	DROP FOREIGN KEY domicile_address_fk
;

UPDATE ADDRESS INNER JOIN IMPORTED_ENTITY
	ON ADDRESS.domicile_id = IMPORTED_ENTITY.old_id
SET ADDRESS.domicile_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "DOMICILE"
;

ALTER TABLE ADDRESS
	ADD FOREIGN KEY (domicile_id) REFERENCES IMPORTED_ENTITY (id)
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP FOREIGN KEY domicile_fk
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.domicile_id = IMPORTED_ENTITY.old_id
SET APPLICATION_PERSONAL_DETAIL.domicile_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "DOMICILE"
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD FOREIGN KEY (domicile_id) REFERENCES IMPORTED_ENTITY (id)
;

ALTER TABLE INSTITUTION
	DROP FOREIGN KEY institution_ibfk_3
;

UPDATE INSTITUTION INNER JOIN IMPORTED_ENTITY
	ON INSTITUTION.domicile_id = IMPORTED_ENTITY.old_id
SET INSTITUTION.domicile_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "DOMICILE"
;

ALTER TABLE INSTITUTION
	ADD FOREIGN KEY (domicile_id) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE DOMICILE
;

/* Nationality import */

ALTER TABLE NATIONALITY
	DROP FOREIGN KEY language_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id1 = NATIONALITY.id
INNER JOIN NATIONALITY AS ALTERNATE_NATIONALITY
	ON NATIONALITY.code = ALTERNATE_NATIONALITY.code
SET APPLICATION_PERSONAL_DETAIL.nationality_id1 = ALTERNATE_NATIONALITY.id
WHERE NATIONALITY.enabled = 0
	AND ALTERNATE_NATIONALITY.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id2 = NATIONALITY.id
INNER JOIN NATIONALITY AS ALTERNATE_NATIONALITY
	ON NATIONALITY.code = ALTERNATE_NATIONALITY.code
SET APPLICATION_PERSONAL_DETAIL.nationality_id2 = ALTERNATE_NATIONALITY.id
WHERE NATIONALITY.enabled = 0
	AND ALTERNATE_NATIONALITY.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id1 = NATIONALITY.id
INNER JOIN NATIONALITY AS ALTERNATE_NATIONALITY
	ON NATIONALITY.name = ALTERNATE_NATIONALITY.name
SET APPLICATION_PERSONAL_DETAIL.nationality_id1 = ALTERNATE_NATIONALITY.id
WHERE NATIONALITY.enabled = 0
	AND ALTERNATE_NATIONALITY.enabled = 1
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id2 = NATIONALITY.id
INNER JOIN NATIONALITY AS ALTERNATE_NATIONALITY
	ON NATIONALITY.name = ALTERNATE_NATIONALITY.name
SET APPLICATION_PERSONAL_DETAIL.nationality_id2 = ALTERNATE_NATIONALITY.id
WHERE NATIONALITY.enabled = 0
	AND ALTERNATE_NATIONALITY.enabled = 1
;

DELETE NATIONALITY.*
FROM NATIONALITY LEFT JOIN APPLICATION_PERSONAL_DETAIL
	ON NATIONALITY.id = APPLICATION_PERSONAL_DETAIL.nationality_id1
	OR NATIONALITY.id = APPLICATION_PERSONAL_DETAIL.nationality_id2
WHERE NATIONALITY.enabled = 0
	AND APPLICATION_PERSONAL_DETAIL.id IS NULL
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id1 = NATIONALITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM NATIONALITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON NATIONALITY.code = MERGE_OBJECT.code
SET APPLICATION_PERSONAL_DETAIL.nationality_id1 = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id2 = NATIONALITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM NATIONALITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON NATIONALITY.code = MERGE_OBJECT.code
SET APPLICATION_PERSONAL_DETAIL.nationality_id2 = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id1 = NATIONALITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM NATIONALITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON NATIONALITY.name = MERGE_OBJECT.name
SET APPLICATION_PERSONAL_DETAIL.nationality_id1 = MERGE_OBJECT.id
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN NATIONALITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id2 = NATIONALITY.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM NATIONALITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON NATIONALITY.name = MERGE_OBJECT.name
SET APPLICATION_PERSONAL_DETAIL.nationality_id2 = MERGE_OBJECT.id
;

DELETE NATIONALITY.* 
FROM NATIONALITY INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM NATIONALITY
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON NATIONALITY.code = MERGE_OBJECT.code
WHERE NATIONALITY.id != MERGE_OBJECT.id
;

DELETE NATIONALITY.* 
FROM NATIONALITY INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM NATIONALITY
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON NATIONALITY.name = MERGE_OBJECT.name
WHERE NATIONALITY.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "NATIONALITY", name, code, enabled, id
	FROM NATIONALITY
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	DROP FOREIGN KEY application_personal_detail_ibfk_1,
	DROP FOREIGN KEY application_personal_detail_ibfk_2
;	

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id1 = IMPORTED_ENTITY.old_id
SET APPLICATION_PERSONAL_DETAIL.nationality_id1 = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "NATIONALITY"
;

UPDATE APPLICATION_PERSONAL_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PERSONAL_DETAIL.nationality_id2 = IMPORTED_ENTITY.old_id
SET APPLICATION_PERSONAL_DETAIL.nationality_id2 = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "NATIONALITY"
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD FOREIGN KEY (nationality_id1) REFERENCES IMPORTED_ENTITY (id),
	ADD FOREIGN KEY (nationality_id2) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE NATIONALITY
;

/* Fix program instance */

DELETE PROGRAM_INSTANCE.* 
FROM PROGRAM_INSTANCE INNER JOIN (
	SELECT MAX(id) AS id,
		program_id AS program_id
	FROM PROGRAM_INSTANCE
	GROUP BY program_id, academic_year, program_study_option_id
	HAVING COUNT(id) > 1) AS DUPLICATE_PROGRAM_INSTANCE
	ON PROGRAM_INSTANCE.program_id = DUPLICATE_PROGRAM_INSTANCE.program_id
WHERE PROGRAM_INSTANCE.id != DUPLICATE_PROGRAM_INSTANCE.id
;

ALTER TABLE PROGRAM_INSTANCE
	ADD UNIQUE INDEX (program_id, academic_year, program_study_option_id),
	DROP INDEX program_instance_prog_fk
;

/* Qualification type import */

ALTER TABLE QUALIFICATION_TYPE
	DROP FOREIGN KEY qualification_type_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN QUALIFICATION_TYPE
	ON APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = QUALIFICATION_TYPE.id
INNER JOIN QUALIFICATION_TYPE AS ALTERNATE_QUALIFICATION_TYPE
	ON QUALIFICATION_TYPE.code = ALTERNATE_QUALIFICATION_TYPE.code
SET APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = ALTERNATE_QUALIFICATION_TYPE.id
WHERE QUALIFICATION_TYPE.enabled = 0
	AND ALTERNATE_QUALIFICATION_TYPE.enabled = 1
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN QUALIFICATION_TYPE
	ON APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = QUALIFICATION_TYPE.id
INNER JOIN QUALIFICATION_TYPE AS ALTERNATE_QUALIFICATION_TYPE
	ON QUALIFICATION_TYPE.name = ALTERNATE_QUALIFICATION_TYPE.name
SET APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = ALTERNATE_QUALIFICATION_TYPE.id
WHERE QUALIFICATION_TYPE.enabled = 0
	AND ALTERNATE_QUALIFICATION_TYPE.enabled = 1
;

DELETE QUALIFICATION_TYPE.*
FROM QUALIFICATION_TYPE LEFT JOIN APPLICATION_QUALIFICATION
	ON QUALIFICATION_TYPE.id = APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id
WHERE QUALIFICATION_TYPE.enabled = 0
	AND APPLICATION_QUALIFICATION.id IS NULL
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN QUALIFICATION_TYPE
	ON APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = QUALIFICATION_TYPE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM QUALIFICATION_TYPE
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON QUALIFICATION_TYPE.code = MERGE_OBJECT.code
SET APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN QUALIFICATION_TYPE
	ON APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = QUALIFICATION_TYPE.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM QUALIFICATION_TYPE
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON QUALIFICATION_TYPE.name = MERGE_OBJECT.name
SET APPLICATION_QUALIFICATION.QUALIFICATION_TYPE_id = MERGE_OBJECT.id
;

DELETE QUALIFICATION_TYPE.* 
FROM QUALIFICATION_TYPE INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM QUALIFICATION_TYPE
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON QUALIFICATION_TYPE.code = MERGE_OBJECT.code
WHERE QUALIFICATION_TYPE.id != MERGE_OBJECT.id
;

DELETE QUALIFICATION_TYPE.* 
FROM QUALIFICATION_TYPE INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM QUALIFICATION_TYPE
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON QUALIFICATION_TYPE.name = MERGE_OBJECT.name
WHERE QUALIFICATION_TYPE.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "QUALIFICATION_TYPE", name, code, enabled, id
	FROM QUALIFICATION_TYPE
;

ALTER TABLE APPLICATION_QUALIFICATION
	DROP FOREIGN KEY qualification_type_fk
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_QUALIFICATION.qualification_type_id = IMPORTED_ENTITY.old_id
SET APPLICATION_QUALIFICATION.qualification_type_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "QUALIFICATION_TYPE"
;

ALTER TABLE APPLICATION_QUALIFICATION
	ADD FOREIGN KEY (qualification_type_id) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE QUALIFICATION_TYPE
;

/* Sources of interest import */

ALTER TABLE SOURCES_OF_INTEREST
	DROP FOREIGN KEY sources_of_interest_enabled_object_fk,
	DROP COLUMN enabled_object_id
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN SOURCES_OF_INTEREST
	ON APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = SOURCES_OF_INTEREST.id
INNER JOIN SOURCES_OF_INTEREST AS ALTERNATE_SOURCES_OF_INTEREST
	ON SOURCES_OF_INTEREST.code = ALTERNATE_SOURCES_OF_INTEREST.code
SET APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = ALTERNATE_SOURCES_OF_INTEREST.id
WHERE SOURCES_OF_INTEREST.enabled = 0
	AND ALTERNATE_SOURCES_OF_INTEREST.enabled = 1
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN SOURCES_OF_INTEREST
	ON APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = SOURCES_OF_INTEREST.id
INNER JOIN SOURCES_OF_INTEREST AS ALTERNATE_SOURCES_OF_INTEREST
	ON SOURCES_OF_INTEREST.name = ALTERNATE_SOURCES_OF_INTEREST.name
SET APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = ALTERNATE_SOURCES_OF_INTEREST.id
WHERE SOURCES_OF_INTEREST.enabled = 0
	AND ALTERNATE_SOURCES_OF_INTEREST.enabled = 1
;

DELETE SOURCES_OF_INTEREST.*
FROM SOURCES_OF_INTEREST LEFT JOIN APPLICATION_PROGRAM_DETAIL
	ON SOURCES_OF_INTEREST.id = APPLICATION_PROGRAM_DETAIL.sources_of_interest_id
WHERE SOURCES_OF_INTEREST.enabled = 0
	AND APPLICATION_PROGRAM_DETAIL.id IS NULL
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN SOURCES_OF_INTEREST
	ON APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = SOURCES_OF_INTEREST.id
INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM SOURCES_OF_INTEREST
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON SOURCES_OF_INTEREST.code = MERGE_OBJECT.code
SET APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = MERGE_OBJECT.id
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN SOURCES_OF_INTEREST
	ON APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = SOURCES_OF_INTEREST.id
INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM SOURCES_OF_INTEREST
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON SOURCES_OF_INTEREST.name = MERGE_OBJECT.name
SET APPLICATION_PROGRAM_DETAIL.sources_of_interest_id = MERGE_OBJECT.id
;

DELETE SOURCES_OF_INTEREST.* 
FROM SOURCES_OF_INTEREST INNER JOIN (
	SELECT MAX(id) AS id,
		code AS code
	FROM SOURCES_OF_INTEREST
	GROUP BY code
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON SOURCES_OF_INTEREST.code = MERGE_OBJECT.code
WHERE SOURCES_OF_INTEREST.id != MERGE_OBJECT.id
;

DELETE SOURCES_OF_INTEREST.* 
FROM SOURCES_OF_INTEREST INNER JOIN (
	SELECT MAX(id) AS id,
		name AS name
	FROM SOURCES_OF_INTEREST
	GROUP BY name
	HAVING COUNT(id) > 1) AS MERGE_OBJECT
	ON SOURCES_OF_INTEREST.name = MERGE_OBJECT.name
WHERE SOURCES_OF_INTEREST.id != MERGE_OBJECT.id
;

INSERT INTO IMPORTED_ENTITY (institution_id, imported_entity_type_id, name, code, enabled, old_id)
	SELECT 5243, "REFERRAL_SOURCE", name, code, enabled, id
	FROM SOURCES_OF_INTEREST
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	DROP FOREIGN KEY sources_of_interest_fk,
	DROP INDEX sources_of_interest_fk,
	CHANGE COLUMN sources_of_interest_id referral_source_id INT(10) UNSIGNED,
	ADD INDEX (referral_source_id)
;

UPDATE APPLICATION_PROGRAM_DETAIL INNER JOIN IMPORTED_ENTITY
	ON APPLICATION_PROGRAM_DETAIL.referral_source_id = IMPORTED_ENTITY.old_id
SET APPLICATION_PROGRAM_DETAIL.referral_source_id = IMPORTED_ENTITY.id
WHERE IMPORTED_ENTITY.imported_entity_type_id = "REFERRAL_SOURCE"
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	ADD FOREIGN KEY (referral_source_id) REFERENCES IMPORTED_ENTITY (id)
;

DROP TABLE SOURCES_OF_INTEREST
;

/* Fix constraints on application program detail table */

UPDATE APPLICATION INNER JOIN APPLICATION_PROGRAM_DETAIL
	ON APPLICATION.application_program_detail_id = APPLICATION_PROGRAM_DETAIL.id
SET APPLICATION.application_program_detail_id = NULL
WHERE APPLICATION_PROGRAM_DETAIL.referral_source_id IS NULL
	AND APPLICATION_PROGRAM_DETAIL.sources_of_interest_text IS NULL
	AND APPLICATION_PROGRAM_DETAIL.start_date IS NULL
;

DELETE APPLICATION_SUPERVISOR.*
FROM APPLICATION_SUPERVISOR INNER JOIN APPLICATION_PROGRAM_DETAIL
	ON APPLICATION_SUPERVISOR.application_program_detail_id = APPLICATION_PROGRAM_DETAIL.id
WHERE APPLICATION_PROGRAM_DETAIL.referral_source_id IS NULL
	AND APPLICATION_PROGRAM_DETAIL.sources_of_interest_text IS NULL
	AND APPLICATION_PROGRAM_DETAIL.start_date IS NULL
;

DELETE FROM APPLICATION_PROGRAM_DETAIL
WHERE referral_source_id IS NULL
	AND sources_of_interest_text IS NULL
	AND start_date IS NULL
;

UPDATE APPLICATION_PROGRAM_DETAIL
SET referral_source_id = 12
WHERE referral_source_id IS NULL
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	MODIFY COLUMN referral_source_id INT(10) UNSIGNED NOT NULL,
	DROP COLUMN sources_of_interest_text
;
