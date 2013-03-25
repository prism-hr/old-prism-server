CREATE TABLE APPLICATIONS_FILTER (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id INT(10) UNSIGNED NOT NULL,
	search_category VARCHAR(50),
	search_term VARCHAR(50),
	PRIMARY KEY(id),
	KEY registered_user_fk(user_id),
	CONSTRAINT registered_user_fk FOREIGN KEY(user_id) REFERENCES REGISTERED_USER(id)
)
ENGINE = InnoDB
;
