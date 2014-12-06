ALTER TABLE APPLICATION_ADDITIONAL_INFORMATION
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_ADDRESS
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_DOCUMENT
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_EMPLOYMENT_POSITION
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_FUNDING
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_LANGUAGE_QUALIFICATION
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_PASSPORT
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_PERSONAL_DETAIL
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_PRIZE
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_PROGRAM_DETAIL
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_QUALIFICATION
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_REFEREE
	ADD COLUMN last_updated_timestamp DATETIME
;

ALTER TABLE APPLICATION_SUPERVISOR
	ADD COLUMN last_updated_timestamp DATETIME
;

UPDATE APPLICATION_ADDITIONAL_INFORMATION
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_ADDRESS
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_DOCUMENT
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_EMPLOYMENT_POSITION
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_FUNDING
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_LANGUAGE_QUALIFICATION
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_PASSPORT
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_PERSONAL_DETAIL
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_PRIZE
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_PROGRAM_DETAIL
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_QUALIFICATION
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_REFEREE
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;

UPDATE APPLICATION_SUPERVISOR
	SET last_updated_timestamp = CURRENT_TIMESTAMP()
;
