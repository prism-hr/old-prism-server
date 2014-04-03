CALL SP_FILL_MISSING_PROOF_OF_AWARD_DOCUMENTS (20493)
;

DROP PROCEDURE SP_FILL_MISSING_PROOF_OF_AWARD_DOCUMENTS
;

UPDATE APPLICATION_QUALIFICATION
SET send_to_ucl = 0
WHERE send_to_ucl IS NULL
;

UPDATE APPLICATION_QUALIFICATION
SET title = "N/A"
WHERE title IS NULL
;

UPDATE APPLICATION_QUALIFICATION
SET qualification_language = "N/A"
WHERE qualification_language IS NULL
;

ALTER TABLE APPLICATION_QUALIFICATION
	DROP FOREIGN KEY proof_of_award_fk,
	CHANGE COLUMN proof_of_award_id document_id INT(10) UNSIGNED NOT NULL,
	ADD FOREIGN KEY (document_id) REFERENCES DOCUMENT (id),
	CHANGE COLUMN send_to_ucl export INT(1) UNSIGNED NOT NULL DEFAULT 0,
	MODIFY COLUMN title VARCHAR(200) NOT NULL,
	MODIFY COLUMN qualification_language VARCHAR(70) NOT NULL
;

CREATE TRIGGER TR_CREATE_CUSTOM_INSTITUTION_CODE 
BEFORE INSERT ON INSTITUTION 
FOR EACH ROW 
BEGIN
	
	DECLARE code_integer_part INT(10) UNSIGNED;
	
	SET code_integer_part = (
		SELECT COUNT(id) + 1
		FROM INSTITUTION
		WHERE code LIKE "CUST%");
		
	SET NEW.code = ( 
		SELECT CONCAT("CUST", LPAD(CONCAT("", code_integer_part, ""), 6, "0")));
	
END
;

INSERT IGNORE INTO INSTITUTION (name, domicile_code, enabled)
	SELECT APPLICATION_QUALIFICATION.other_institution_name,
		IF(DOMICILE2.code IS NOT NULL, DOMICILE2.code, DOMICILE.code), 1
	FROM APPLICATION_QUALIFICATION INNER JOIN DOMICILE
		ON APPLICATION_QUALIFICATION.institution_domicile_id = DOMICILE.id
	LEFT JOIN DOMICILE AS DOMICILE2
		ON DOMICILE.enabled_object_id = DOMICILE2.id
	WHERE (APPLICATION_QUALIFICATION.other_institution_name IS NOT NULL
		AND LENGTH(APPLICATION_QUALIFICATION.other_institution_name) > 0)
	GROUP BY APPLICATION_QUALIFICATION.other_institution_name
;

INSERT IGNORE INTO INSTITUTION (name, domicile_code, enabled)
	SELECT APPLICATION_QUALIFICATION.institution_name,
		IF(DOMICILE2.code IS NOT NULL, DOMICILE2.code, DOMICILE.code), 1
	FROM APPLICATION_QUALIFICATION INNER JOIN INSTITUTION
		ON APPLICATION_QUALIFICATION.other_institution_name = INSTITUTION.name
	INNER JOIN DOMICILE
		ON INSTITUTION.domicile_code = DOMICILE.code
	LEFT JOIN DOMICILE AS DOMICILE2
		ON DOMICILE.enabled_object_id = DOMICILE2.id
	LEFT JOIN INSTITUTION AS INSTITUTION_BY_CODE
		ON APPLICATION_QUALIFICATION.institution_code = INSTITUTION_BY_CODE.code	
	WHERE APPLICATION_QUALIFICATION.institution_name NOT IN ("Other...", "Other")
		AND INSTITUTION_BY_CODE.code IS NULL
;

DROP TRIGGER TR_CREATE_CUSTOM_INSTITUTION_CODE
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN INSTITUTION
	ON APPLICATION_QUALIFICATION.other_institution_name = INSTITUTION.name
SET APPLICATION_QUALIFICATION.institution_code = INSTITUTION.code
WHERE APPLICATION_QUALIFICATION.institution_code = "OTHER"
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN INSTITUTION
	ON APPLICATION_QUALIFICATION.institution_name = INSTITUTION.name
LEFT JOIN INSTITUTION AS INSTITUTION_BY_CODE
	ON APPLICATION_QUALIFICATION.institution_code = INSTITUTION_BY_CODE.code
SET APPLICATION_QUALIFICATION.institution_code = INSTITUTION.code
WHERE INSTITUTION_BY_CODE.code IS NULL
;

ALTER TABLE APPLICATION_QUALIFICATION
	ADD COLUMN institution_id INT(10) UNSIGNED AFTER institution_code,
	ADD INDEX (institution_id),
	ADD CONSTRAINT application_qualification_ibfk_3 FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id)
;

UPDATE APPLICATION_QUALIFICATION INNER JOIN INSTITUTION
	ON APPLICATION_QUALIFICATION.institution_code = INSTITUTION.code
SET APPLICATION_QUALIFICATION.institution_id = INSTITUTION.id
;

DELETE
FROM APPLICATION_QUALIFICATION
WHERE institution_id IS NULL
;

ALTER TABLE APPLICATION_QUALIFICATION
	DROP COLUMN institution_name,
	DROP COLUMN other_institution_name,
	DROP FOREIGN KEY domicile_id_fk,
	DROP COLUMN institution_domicile_id,
	DROP COLUMN institution_code,
	MODIFY COLUMN institution_id INT(10) UNSIGNED NOT NULL
;

DROP TABLE CANDIDATE_NATIONALITY_LINK
;
