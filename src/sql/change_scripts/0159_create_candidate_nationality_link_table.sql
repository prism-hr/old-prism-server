CREATE TABLE CANDIDATE_NATIONALITY_LINK (
  candidate_country_id INTEGER UNSIGNED NOT NULL,
  candidate_personal_details_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT candidate_country_fk FOREIGN KEY (candidate_country_id) REFERENCES COUNTRIES(id),
  CONSTRAINT candidate_personal_details_fk FOREIGN KEY (candidate_personal_details_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL(id)
)
ENGINE = InnoDB;