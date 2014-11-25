CREATE TABLE SCORING_DEFINITION (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  program_id INTEGER UNSIGNED,
  stage VARCHAR(15) NOT NULL,
  content LONGTEXT NOT NULL,

  CONSTRAINT program_fk FOREIGN KEY (program_id) REFERENCES PROGRAM(id),
  PRIMARY KEY(id),
  UNIQUE KEY (id, program_id, stage)
) ENGINE = InnoDB
;