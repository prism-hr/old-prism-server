CREATE TABLE APPLICATION_FORM_REVIEWER_LINK(
  reviewer_id INTEGER UNSIGNED NOT NULL,
  application_form_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT application_form_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  CONSTRAINT reviewer_user_fk FOREIGN KEY (reviewer_id) REFERENCES REGISTERED_USER(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS reviewer_user_fk;
DROP CONSTRAINT IF EXISTS application_form_fk;
