CREATE TABLE REVIEWER (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  last_notified DATETIME NOT NULL, 	
  registered_user_id INTEGER UNSIGNED NOT NULL,
  application_form_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT user_reviewer_fk FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT application_reviewer_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY (id)
)
ENGINE = InnoDB;