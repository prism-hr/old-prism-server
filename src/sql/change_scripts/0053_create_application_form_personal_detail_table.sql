CREATE TABLE APPLICATION_FORM_PERSONAL_DETAIL(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED,
  first_name VARCHAR(2000) NOT NULL,
  last_name VARCHAR(2000) NOT NULL,
  email VARCHAR(2000) NOT NULL,
  gender VARCHAR(100) NOT NULL,
  date_of_birth DATE NOT NULL,
  country_id INTEGER UNSIGNED NOT NULL,
  residence_country_id INTEGER UNSIGNED NOT NULL,
  residence_status VARCHAR(500) NOT NULL,
  CONSTRAINT application_form_personal_details_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  CONSTRAINT residence_country_fk FOREIGN KEY (residence_country_id) REFERENCES COUNTRIES(id),
  CONSTRAINT country_fk FOREIGN KEY (country_id) REFERENCES COUNTRIES(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_personal_details_fk;
DROP CONSTRAINT IF EXISTS residence_country_fk;
DROP CONSTRAINT IF EXISTS country_fk;