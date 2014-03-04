CREATE TABLE INTERVIEW (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  due_date DATE, 	
  last_notified DATETIME, 	
  application_form_id INTEGER UNSIGNED,
  further_details VARCHAR(5000),
  location_url VARCHAR(5000),
  CONSTRAINT app_interview_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY (id)
)
ENGINE = InnoDB;