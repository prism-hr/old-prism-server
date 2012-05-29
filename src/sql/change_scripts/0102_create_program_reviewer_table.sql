CREATE TABLE PROGRAM_REVIEWER_LINK (
  reviewer_id INTEGER UNSIGNED NOT NULL,
  program_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT reviewer_program_user_fk FOREIGN KEY (reviewer_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT reviewer_program_program_fk FOREIGN KEY (program_id) REFERENCES PROGRAM(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS reviewer_program_user_fk;
DROP CONSTRAINT IF EXISTS reviewer_program_program_fk;
DROP TABLE IF EXISTS PROGRAM_REVIEWER_LINK;