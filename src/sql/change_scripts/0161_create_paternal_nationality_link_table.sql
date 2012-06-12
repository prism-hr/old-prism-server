CREATE TABLE PATERNAL_NATIONALITY_LINK (
  paternal_country_id INTEGER UNSIGNED NOT NULL,
  paternal_personal_details_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT paternal_country_fk FOREIGN KEY (paternal_country_id) REFERENCES COUNTRIES(id),
  CONSTRAINT paternal_personal_details_fk FOREIGN KEY (paternal_personal_details_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL(id)
)
ENGINE = InnoDB;