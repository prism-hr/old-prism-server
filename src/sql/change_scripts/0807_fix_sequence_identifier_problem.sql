ALTER TABLE APPLICATION
	DROP INDEX sequence_identifier
;

ALTER TABLE APPLICATION
	ADD UNIQUE INDEX (sequence_identifier)
;

ALTER TABLE PROJECT
	DROP INDEX sequence_identifier
;

ALTER TABLE PROJECT
	ADD UNIQUE INDEX (sequence_identifier)
;

ALTER TABLE PROGRAM
	DROP INDEX sequence_identifier
;

ALTER TABLE PROGRAM
	ADD UNIQUE INDEX (sequence_identifier)
;

ALTER TABLE INSTITUTION
	DROP INDEX sequence_identifier
;

ALTER TABLE INSTITUTION
	ADD UNIQUE INDEX (sequence_identifier)
;

ALTER TABLE SYSTEM
	DROP INDEX sequence_identifier
;

ALTER TABLE SYSTEM
	ADD UNIQUE INDEX (sequence_identifier)
;

ALTER TABLE ADVERT
	ADD UNIQUE INDEX (sequence_identifier)
;

UPDATE APPLICATION
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), LPAD(id, 10, "0"))
;

UPDATE PROJECT
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), LPAD(id, 10, "0"))
;

UPDATE PROGRAM
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), LPAD(id, 10, "0"))
;

UPDATE INSTITUTION
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), LPAD(id, 10, "0"))
;

UPDATE SYSTEM
SET sequence_identifier = CONCAT(UNIX_TIMESTAMP(updated_timestamp), LPAD(id, 10, "0"))
;

UPDATE ADVERT INNER JOIN PROJECT
	ON ADVERT.id = PROJECT.advert_id
SET ADVERT.sequence_identifier = CONCAT(UNIX_TIMESTAMP(PROJECT.updated_timestamp), LPAD(ADVERT.id, 10, "0"))
;

UPDATE ADVERT INNER JOIN PROGRAM
	ON ADVERT.id = PROGRAM.advert_id
SET ADVERT.sequence_identifier = CONCAT(UNIX_TIMESTAMP(PROGRAM.updated_timestamp), LPAD(ADVERT.id, 10, "0"))
;
