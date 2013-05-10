CREATE TABLE IF NOT EXISTS APPLICATION_FORM_FUNDING(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED,
  award_type VARCHAR(200) NOT NULL,
  description VARCHAR(2000) NOT NULL,
  award_value VARCHAR(100) NOT NULL,
  award_date DATE NOT NULL,
  CONSTRAINT application_form_funding_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_funding_fk;
