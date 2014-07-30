UPDATE ADDRESS
SET address_line_1 = "Not provided"
WHERE LENGTH(TRIM(address_line_1)) = 0
;

UPDATE ADDRESS
SET address_line_2 = NULL
WHERE LENGTH(TRIM(address_line_2)) = 0
;

UPDATE ADDRESS
SET address_town = "Not provided"
WHERE LENGTH(TRIM(address_town)) = 0
;

UPDATE ADDRESS
SET address_region = NULL
WHERE LENGTH(TRIM(address_region)) = 0
;

UPDATE ADDRESS
SET address_town = "Not provided"
WHERE address_town IS NULL
;

UPDATE ADDRESS
SET address_code = "Not provided"
WHERE address_code IS NULL
;

ALTER TABLE ADDRESS
	MODIFY COLUMN address_code VARCHAR(12) NOT NULL
;

ALTER TABLE APPLICATION_ADDITIONAL_INFORMATION
	DROP COLUMN has_convictions
;

UPDATE APPLICATION_REFEREE
SET include_in_export = 0
WHERE include_in_export IS NULL
;

ALTER TABLE APPLICATION_REFEREE
	MODIFY COLUMN include_in_export INT(1) UNSIGNED NOT NULL
;

ALTER TABLE INSTITUTION
	ADD COLUMN is_ucl_institution INT(1) DEFAULT 0 AFTER institution_address_id,
	ADD INDEX (is_ucl_institution)
;

UPDATE INSTITUTION
SET is_ucl_institution = 1
;
