CREATE TABLE SUPERVISOR(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  personal_detail_id INTEGER UNSIGNED,
  email VARCHAR(2000) NOT NULL,
  primary_supervisor VARCHAR(50),
  aware_supervisor VARCHAR(50),
  CONSTRAINT application_form_personal_detail_supervisor_fk FOREIGN KEY (personal_detail_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS application_form_personal_detail_supervisor_fk;
