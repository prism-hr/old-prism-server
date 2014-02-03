CREATE TABLE PROJECT (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  author_id INTEGER UNSIGNED NOT NULL,
  program_id INTEGER UNSIGNED NOT NULL,
  advert_id INTEGER UNSIGNED NOT NULL,
  closing_date DATE,
  primary_supervisor_id INTEGER UNSIGNED NOT NULL,
  secondary_supervisor_id INTEGER UNSIGNED,
  CONSTRAINT project_author_fk FOREIGN KEY (author_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT project_program_fk FOREIGN KEY (program_id) REFERENCES PROGRAM(id),
  CONSTRAINT project_advert_fk FOREIGN KEY (advert_id) REFERENCES ADVERT(id),
  CONSTRAINT project_primary_supervisor_fk FOREIGN KEY (primary_supervisor_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT project_secondary_supervisor_fk FOREIGN KEY (secondary_supervisor_id) REFERENCES REGISTERED_USER(id),
  PRIMARY KEY (id)
) ENGINE = InnoDB
;
