CREATE TABLE IF NOT EXISTS APPLICATION_FORM_ADDITIONAL_INFO(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  application_form_id INTEGER UNSIGNED NOT NULL,
  info_text VARCHAR(5000),
  has_convictions VARCHAR(10) NOT NULL,
  convictions_text VARCHAR(100),
  CONSTRAINT application_form_additional_info_fk FOREIGN KEY (application_form_id) REFERENCES APPLICATION_FORM(id),
  PRIMARY KEY(id)
)
ENGINE = InnoDB
;