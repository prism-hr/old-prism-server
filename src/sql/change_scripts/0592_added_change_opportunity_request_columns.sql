ALTER TABLE OPPORTUNITY_REQUEST
ADD COLUMN request_type VARCHAR(6) NOT NULL,
ADD COLUMN source_program_id INT(10) UNSIGNED,
ADD CONSTRAINT opportunity_request_source_program_fk FOREIGN KEY (source_program_id) REFERENCES PROGRAM(id)
;
