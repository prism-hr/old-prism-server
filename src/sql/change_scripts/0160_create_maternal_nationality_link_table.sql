CREATE TABLE MATERNAL_NATIONALITY_LINK (
  maternal_country_id INTEGER UNSIGNED NOT NULL,
  maternal_personal_details_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT maternal_country_fk FOREIGN KEY (maternal_country_id) REFERENCES COUNTRIES(id),
  CONSTRAINT maternal_personal_details_fk FOREIGN KEY (maternal_personal_details_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL(id)
)
ENGINE = InnoDB;