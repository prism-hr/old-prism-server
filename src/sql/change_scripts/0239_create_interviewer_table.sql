CREATE TABLE INTERVIEWER (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  last_notified DATETIME, 	
  registered_user_id INTEGER UNSIGNED,
  application_form_id INTEGER UNSIGNED,
  CONSTRAINT user_interviewer_fkey FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT app_interviewer_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY (id)
)
ENGINE = InnoDB;