ALTER TABLE ADVERT
	ADD COLUMN last_edited_timestamp TIMESTAMP NOT NULL DEFAULT now() ON UPDATE now(),
	ADD INDEX (last_edited_timestamp)
;

UPDATE ADVERT SET last_edited_timestamp = now()
;
