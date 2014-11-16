CREATE TABLE VALIDATION_COMMENT (
  id INTEGER UNSIGNED NOT NULL,
  qualified_for_phd VARCHAR(30),
  english_compentency_ok VARCHAR(30),
  home_or_overseas VARCHAR(30),
  CONSTRAINT validation_comment_fk FOREIGN KEY (id) REFERENCES COMMENT(id)
)
ENGINE = InnoDB;