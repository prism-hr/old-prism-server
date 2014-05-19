ALTER TABLE PROJECT
	ADD COLUMN administrator_id INT(10) UNSIGNED AFTER id,
	ADD CONSTRAINT `project_administrator_registered_user_fk` FOREIGN KEY (administrator_id) REFERENCES REGISTERED_USER (id)
;
