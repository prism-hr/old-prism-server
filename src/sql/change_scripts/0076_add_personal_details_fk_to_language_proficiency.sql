ALTER TABLE LANGUAGE_PROFICIENCY ADD COLUMN personal_details_id INTEGER UNSIGNED,  ADD CONSTRAINT prof_details_fk FOREIGN KEY (personal_details_id) REFERENCES APPLICATION_FORM_PERSONAL_DETAIL(id);