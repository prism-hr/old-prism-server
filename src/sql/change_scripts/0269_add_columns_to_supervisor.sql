ALTER TABLE SUPERVISOR
	ADD COLUMN registered_user_id INTEGER UNSIGNED, 
	ADD COLUMN  last_notified DATETIME, 	
 	ADD CONSTRAINT user_supervisor_fk FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER(id);


