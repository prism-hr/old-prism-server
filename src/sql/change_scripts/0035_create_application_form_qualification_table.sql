CREATE TABLE IF NOT EXISTS APPLICATION_FORM_QUALIFICATION(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  applicant_id INTEGER UNSIGNED NOT NULL,
  application_form_id INTEGER UNSIGNED NOT NULL,
  degree VARCHAR(200) NOT NULL,
  institution VARCHAR(200) NOT NULL,
  grade VARCHAR(100) NOT NULL,
  date_taken VARCHAR(20),
  CONSTRAINT application_form_qual_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  CONSTRAINT applicant_user_fk FOREIGN KEY (applicant_id) REFERENCES REGISTERED_USER(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS applicant_user_fk;
DROP CONSTRAINT IF EXISTS application_form_qual_fk;
