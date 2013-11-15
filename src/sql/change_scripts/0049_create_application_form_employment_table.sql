CREATE TABLE APPLICATION_FORM_EMPLOYMENT_POSITION(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED,
  employer VARCHAR(200) NOT NULL,
  title VARCHAR(2000) NOT NULL,
  remit VARCHAR(2000) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  language  VARCHAR(200) NOT NULL,
  CONSTRAINT application_form_employment_position_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_funding_fk;
