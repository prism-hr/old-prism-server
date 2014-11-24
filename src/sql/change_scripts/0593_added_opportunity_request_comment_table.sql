CREATE TABLE OPPORTUNITY_REQUEST_COMMENT (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	opportunity_request_id INT(10) UNSIGNED NOT NULL,
	author_id INT(10) UNSIGNED NOT NULL,
	comment_type VARCHAR(7) NOT NULL,
	content TEXT,
	created_timestamp TIMESTAMP NOT NULL DEFAULT now(),
	CONSTRAINT opportunity_request_comment_opportunity_request_fk FOREIGN KEY (opportunity_request_id) REFERENCES OPPORTUNITY_REQUEST(id),
	CONSTRAINT opportunity_request_comment_author_fk FOREIGN KEY (author_id) REFERENCES REGISTERED_USER(id),
	PRIMARY KEY (id)
)
ENGINE = InnoDB
;