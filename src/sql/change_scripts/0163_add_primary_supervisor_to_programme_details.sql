ALTER TABLE APPLICATION_FORM_PROGRAMME_DETAIL ADD COLUMN primary_supervisor_id INTEGER unsigned, 
ADD CONSTRAINT programme_supervisor_fk FOREIGN KEY (primary_supervisor_id) REFERENCES SUPERVISOR(id);