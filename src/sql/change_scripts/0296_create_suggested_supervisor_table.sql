CREATE TABLE SUGGESTED_SUPERVISOR (
  id INTEGER UNSIGNED NOT NULL,
  aware tinyint(1),
  CONSTRAINT suggested_supervisor_fk FOREIGN KEY (id) REFERENCES PERSON(id)
)
ENGINE = InnoDB;