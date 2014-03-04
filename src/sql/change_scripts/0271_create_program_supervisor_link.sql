CREATE TABLE PROGRAM_SUPERVISOR_LINK (
	supervisor_id INTEGER UNSIGNED, 
	program_id INTEGER UNSIGNED, 
	CONSTRAINT supervisor_program_fk FOREIGN KEY (program_id) REFERENCES PROGRAM(id),
	CONSTRAINT supervisor_user_fk FOREIGN KEY (supervisor_id) REFERENCES REGISTERED_USER(id)
)
ENGINE = InnoDB;

