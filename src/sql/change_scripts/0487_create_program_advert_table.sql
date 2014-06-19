CREATE TABLE PROGRAM_ADVERT (
  program_id INTEGER UNSIGNED NOT NULL,
  description VARCHAR(2000),
  duration_of_study_in_month INTEGER(4),
  funding_information VARCHAR(500),
  is_currently_accepting_applications TINYINT(1),
  CONSTRAINT program_fk_in_program_advert FOREIGN KEY (program_id) REFERENCES PROGRAM(id),
  UNIQUE KEY (program_id) 
) ENGINE = InnoDB
;