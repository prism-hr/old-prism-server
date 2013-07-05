DROP TABLE IF EXISTS PROGRAM_ADVERT
;

CREATE TABLE PROGRAM_ADVERT (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  description VARCHAR(2000),
  duration_of_study_in_month INTEGER(4),
  funding_information VARCHAR(500),
  is_currently_accepting_applications TINYINT(1),
  PRIMARY KEY (id)
) ENGINE = InnoDB
;

ALTER TABLE PROGRAM ADD COLUMN program_advert_id INTEGER UNSIGNED,
	ADD CONSTRAINT program_advert_id_fk FOREIGN KEY(program_advert_id) REFERENCES PROGRAM_ADVERT(id)
;