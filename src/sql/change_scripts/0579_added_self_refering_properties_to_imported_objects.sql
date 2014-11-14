ALTER TABLE COUNTRIES
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT countries_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES COUNTRIES(id)
;

ALTER TABLE DISABILITY
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT disability_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES DISABILITY(id)
;

ALTER TABLE DOMICILE
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT domicile_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES DOMICILE(id)
;

ALTER TABLE ETHNICITY
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT ethnicity_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES ETHNICITY(id)
;

ALTER TABLE LANGUAGE
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT language_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES LANGUAGE(id)
;

ALTER TABLE INSTITUTION_REFERENCE
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT institution_reference_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES INSTITUTION_REFERENCE(id)
;

ALTER TABLE QUALIFICATION_TYPE
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT qualification_type_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES QUALIFICATION_TYPE(id)
;

ALTER TABLE SOURCES_OF_INTEREST
ADD COLUMN enabled_object_id INT(10) UNSIGNED,
ADD CONSTRAINT sources_of_interest_enabled_object_fk FOREIGN KEY (enabled_object_id) REFERENCES SOURCES_OF_INTEREST(id)
;
