CREATE TABLE TELEPHONE(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  telephone_type VARCHAR(200) NOT NULL,
  number VARCHAR(2000) NOT NULL,
  referee_id INTEGER UNSIGNED,
  CONSTRAINT referee_id_telephone_fk FOREIGN KEY (referee_id) REFERENCES APPLICATION_FORM_REFEREE(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS referee_id_telephone_fk;
