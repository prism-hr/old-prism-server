CREATE TABLE PROGRAM_FEED (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	feed_url VARCHAR(100) NOT NULL,
	institution_id INTEGER UNSIGNED NOT NULL,
	CONSTRAINT program_feed_institution_fk FOREIGN KEY (institution_id) REFERENCES INSTITUTION(id),
	PRIMARY KEY (id)
)
ENGINE = InnoDB
;

ALTER TABLE PROGRAM
	ADD COLUMN institution_id INTEGER UNSIGNED NOT NULL,
	ADD COLUMN feed_id INTEGER UNSIGNED
;

INSERT INTO PROGRAM_FEED (feed_url, institution_id) 
	VALUES ('https://swiss.adcom.ucl.ac.uk/studentrefdata/reference/prism/prismProgrammes.xml',
			(SELECT id FROM INSTITUTION WHERE code = 'UK0275'))
;
	

UPDATE PROGRAM
	SET institution_id = (SELECT id FROM INSTITUTION WHERE code = 'UK0275')
;

UPDATE PROGRAM
	SET feed_id = (SELECT id FROM PROGRAM_FEED)
;

ALTER TABLE PROGRAM
	ADD CONSTRAINT program_institution_fk FOREIGN KEY (institution_id) REFERENCES INSTITUTION(id),
	ADD CONSTRAINT program_feed_fk FOREIGN KEY (feed_id) REFERENCES PROGRAM_FEED(id)
;

ALTER TABLE PROGRAM
	DROP INDEX code,
	ADD UNIQUE INDEX institution_code (institution_id, code)
;
