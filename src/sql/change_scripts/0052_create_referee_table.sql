CREATE TABLE APPLICATION_FORM_REFEREE(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED,
  firstname VARCHAR(200) NOT NULL,
  lastname VARCHAR(200) NOT NULL,
  relationship VARCHAR(200) NOT NULL,
  job_employer VARCHAR(200),
  job_title VARCHAR(200),
  address_location VARCHAR(200),
  address_postcode VARCHAR(200),
  address_country VARCHAR(200),
  email VARCHAR(200) NOT NULL,
  telephone_id INTEGER UNSIGNED,
  messenger_id INTEGER UNSIGNED,
  CONSTRAINT application_form_referee_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  CONSTRAINT telephone_referee_fk FOREIGN KEY (telephone_id) REFERENCES TELEPHONE(id),
  CONSTRAINT messenger_referee_fk FOREIGN KEY (messenger_id) REFERENCES MESSENGER(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_referee_fk;
DROP CONSTRAINT IF EXISTS telephone_referee_fk;
DROP CONSTRAINT IF EXISTS messenger_referee_fk;


