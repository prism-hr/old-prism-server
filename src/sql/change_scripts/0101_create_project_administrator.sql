CREATE TABLE PROGRAM_ADMINISTRATOR_LINK (
  administrator_id INTEGER UNSIGNED NOT NULL,
  program_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT administrator_program_user_fk FOREIGN KEY (administrator_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT administrator_program_program_fk FOREIGN KEY (program_id) REFERENCES PROGRAM(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS administrator_program_user_fk;
DROP CONSTRAINT IF EXISTS administrator_program_program_fk;
DROP TABLE IF EXISTS PROGRAM_ADMINISTRATOR_LINK;