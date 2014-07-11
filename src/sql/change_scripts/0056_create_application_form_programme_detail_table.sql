CREATE TABLE IF NOT EXISTS APPLICATION_FORM_PROGRAMME_DETAIL(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED,
  programme_name VARCHAR(2000) NOT NULL,
  project_name VARCHAR(2000),
  study_option VARCHAR(200) NOT NULL,
  referrer VARCHAR(200) NOT NULL,
  start_date DATE NOT NULL,
  CONSTRAINT application_form_programme_details_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_programme_details_fk;
