CREATE TABLE APPLICATION_FORM (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	title ENUM('Miss', 'Mr', 'Mrs', 'Ms') NOT NULL,
	gender ENUM('Female', 'Male') NOT NULL,
	dob DATE NOT NULL NOT NULL,
	country_ob VARCHAR(200) NOT NULL,
	nationality VARCHAR(200) NOT NULL,
	description_of_research VARCHAR(500) NOT NULL,
	registered_user_id INTEGER UNSIGNED NOT NULL,
	reviewer_user_id INTEGER UNSIGNED, 
	approver_user_id INTEGER UNSIGNED, 
	approved boolean,
	CONSTRAINT registered_user_fk FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER(id),
	CONSTRAINT reviewer_user_fk FOREIGN KEY (reviewer_user_id) REFERENCES REGISTERED_USER(id),
	CONSTRAINT approver_user_fk FOREIGN KEY (approver_user_id) REFERENCES REGISTERED_USER(id),
	PRIMARY KEY(id)
)

ENGINE = InnoDB;

--//@UNDO

DROP TABLE IF EXISTS APPLICATION_FORM;