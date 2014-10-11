ALTER TABLE PROGRAM
	ADD COLUMN is_imported INT(1) UNSIGNED AFTER institution_id
;

UPDATE PROGRAM
SET is_imported = 1
WHERE program_import_id IS NOT NULL
;

UPDATE PROGRAM
SET is_imported = 0
WHERE program_import_id IS NULL
;

ALTER TABLE PROGRAM
	MODIFY COLUMN is_imported INT(1) UNSIGNED NOT NULL
;

ALTER TABLE PROGRAM
	DROP FOREIGN KEY program_feed_fk,
	DROP COLUMN program_import_id
;

RENAME TABLE PROGRAM_IMPORT TO IMPORTED_ENTITY_FEED
;

ALTER TABLE IMPORTED_ENTITY_FEED
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL DEFAULT 5243 AFTER id,
	ADD COLUMN imported_entity_type_id VARCHAR(50) AFTER institution_id,
	ADD COLUMN username VARCHAR(50) AFTER imported_entity_type_id,
	ADD COLUMN password VARCHAR(50) AFTER username,
	CHANGE COLUMN feed_url location VARCHAR(100) NOT NULL,
	ADD UNIQUE INDEX (institution_id, imported_entity_type_id),
	DROP INDEX program_feed_institution_fk,
	ADD INDEX (imported_entity_type_id),
	ADD FOREIGN KEY (imported_entity_type_id) REFERENCES IMPORTED_ENTITY_TYPE (id)
;

INSERT INTO IMPORTED_ENTITY_TYPE (id)
VALUES ("PROGRAM")
;

UPDATE IMPORTED_ENTITY_FEED
SET imported_entity_type_id = "PROGRAM",
	username = "prism",
	password = "tCP5++Vm"
;

ALTER TABLE IMPORTED_ENTITY_FEED
	MODIFY COLUMN imported_entity_type_id VARCHAR(50)  NOT NULL
;

INSERT INTO IMPORTED_ENTITY_FEED (institution_id, imported_entity_type_id, username, password, location)
	SELECT 5243, "COUNTRY" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/countriesOfBirth.xml"
		UNION
	SELECT 5243, "DOMICILE" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/countriesOfDomicile.xml"
		UNION
	SELECT 5243, "DISABILITY" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/disabilities.xml"
		UNION
	SELECT 5243, "ETHNICITY" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/ethnicities.xml"
		UNION
	SELECT 5243, "NATIONALITY" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/nationalities.xml"
		UNION
	SELECT 5243, "QUALIFICATION_TYPE" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/qualifications.xml"
		UNION
	SELECT 5243, "REFERRAL_SOURCE" , "reference", "wiI2+sZm", "https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/sourcesOfInterest.xml"
;
