ALTER TABLE APPLICATION
	MODIFY COLUMN sequence_identifier VARCHAR(23)
;
ALTER TABLE PROJECT
	MODIFY COLUMN sequence_identifier VARCHAR(23)
;
ALTER TABLE PROGRAM
	MODIFY COLUMN sequence_identifier VARCHAR(23)
;
ALTER TABLE INSTITUTION
	MODIFY COLUMN sequence_identifier VARCHAR(23)
;
ALTER TABLE SYSTEM
	MODIFY COLUMN sequence_identifier VARCHAR(23)
;
ALTER TABLE ADVERT
	MODIFY COLUMN sequence_identifier VARCHAR(23)
;
UPDATE APPLICATION
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), "000", LPAD(id, 10, "0"))
;
UPDATE PROJECT
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), "000", LPAD(id, 10, "0"))
;
UPDATE PROGRAM
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), "000", LPAD(id, 10, "0"))
;
UPDATE INSTITUTION
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), "000", LPAD(id, 10, "0"))
;
UPDATE SYSTEM
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), "000", LPAD(id, 10, "0"))
;
UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.advert_id
SET ADVERT.sequence_identifier = CONCAT(UNIX_TIMESTAMP(PROJECT.updated_timestamp), "000", LPAD(ADVERT.id, 10, "0"))
;
UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
SET ADVERT.sequence_identifier = CONCAT(UNIX_TIMESTAMP(PROGRAM.updated_timestamp), "000", LPAD(ADVERT.id, 10, "0"))
;
