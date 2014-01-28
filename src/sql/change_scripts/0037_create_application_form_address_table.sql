CREATE TABLE IF NOT EXISTS APPLICATION_FORM_ADDRESS(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED NOT NULL,
  post_code VARCHAR(50) NOT NULL,
  country VARCHAR(100) NOT NULL,
  street VARCHAR(2000) NOT NULL,
  city VARCHAR(100) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  CONSTRAINT application_form_address_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_address_fk;
