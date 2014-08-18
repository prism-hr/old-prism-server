ALTER TABLE APPLICATION
	ADD COLUMN sequence_identifier VARCHAR (50),
	ADD INDEX (sequence_identifier)
;

ALTER TABLE PROJECT
	ADD COLUMN sequence_identifier VARCHAR (50),
	ADD INDEX (sequence_identifier)
;
	
ALTER TABLE PROGRAM
	ADD COLUMN sequence_identifier VARCHAR (50),
	ADD INDEX (sequence_identifier)	
;

ALTER TABLE INSTITUTION
	ADD COLUMN sequence_identifier VARCHAR (50),
	ADD INDEX (sequence_identifier)
;

ALTER TABLE SYSTEM
	ADD COLUMN sequence_identifier VARCHAR (50),
	ADD INDEX (sequence_identifier)
;
