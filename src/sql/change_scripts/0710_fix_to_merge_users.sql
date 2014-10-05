DROP PROCEDURE SP_MERGE_USER
;

DROP PROCEDURE SP_CLEAR_WORKFLOW
;

UPDATE USER
SET first_name = TRIM(first_name),
	first_name_2 = TRIM(first_name_2),
	first_name_3 = TRIM(first_name_3),
	last_name = TRIM(last_name),
	email = TRIM(email)
;
