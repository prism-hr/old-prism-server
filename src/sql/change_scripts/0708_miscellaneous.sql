ALTER TABLE APPLICATION
	MODIFY COLUMN application_personal_detail_id INT(10) UNSIGNED AFTER application_program_detail_id
;

ALTER TABLE ADDRESS
	MODIFY COLUMN address_code VARCHAR(12)
;

UPDATE ADDRESS
SET address_code = NULL
WHERE address_code = "Not provided"
;
