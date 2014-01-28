ALTER TABLE STATECHANGE_COMMENT
	ADD COLUMN delegate_administrator_id INT(10) UNSIGNED,
	ADD INDEX (delegate_administrator_id),
	ADD FOREIGN KEY (delegate_administrator_id) REFERENCES REGISTERED_USER (id)
;