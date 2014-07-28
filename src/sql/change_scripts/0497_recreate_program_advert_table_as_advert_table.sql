ALTER TABLE PROGRAM DROP FOREIGN KEY program_advert_id_fk,
	DROP COLUMN program_advert_id
;

DROP TABLE IF EXISTS PROGRAM_ADVERT
;

CREATE TABLE ADVERT (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  program_id INTEGER UNSIGNED NOT NULL,
  is_program_advert TINYINT(1) NOT NULL,
  title VARCHAR(255),
  description VARCHAR(2000) NOT NULL,
  study_duration INTEGER(4) NOT NULL,
  funding VARCHAR(255),
  active TINYINT(1) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT program_fk_in_program_advert FOREIGN KEY (program_id) REFERENCES PROGRAM(id)
) ENGINE = InnoDB
;
