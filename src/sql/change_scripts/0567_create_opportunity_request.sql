CREATE TABLE OPPORTUNITY_REQUEST (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	institution_domicile_id INTEGER UNSIGNED NOT NULL,
	institution_code VARCHAR(10),
	other_institution_name VARCHAR(200),
	title VARCHAR(255),
	description VARCHAR(3000),
	author_id INTEGER UNSIGNED NOT NULL,
	CONSTRAINT opportunity_request_author_id FOREIGN KEY (author_id) REFERENCES REGISTERED_USER(id),
	PRIMARY KEY (id)
)
ENGINE = InnoDB
;
