CREATE TABLE ACTION_TRIGGER_STATE (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	system_id INT(10) UNSIGNED,
	institution_id INT(10) UNSIGNED,
	program_id INT(10) UNSIGNED,
	locale VARCHAR(10),
	program_type VARCHAR(50) NOT NULL,
	action_id VARCHAR(100) NOT NULL,
	state_id VARCHAR(50) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (system_id, locale, program_type, action_id, state_id),
	UNIQUE INDEX (institution_id, program_type, action_id, state_id),
	UNIQUE INDEX (program_id, action_id, state_id),
	INDEX (action_id),
	INDEX (state_id),
	FOREIGN KEY (system_id) REFERENCES SYSTEM (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	FOREIGN KEY (program_id) REFERENCES PROGRAM (id),
	FOREIGN KEY (action_id) REFERENCES ACTION (id),
	FOREIGN KEY (state_id) REFERENCES STATE (id)
) ENGINE = INNODB
;
