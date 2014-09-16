CREATE TABLE ADVERT_RECRUITMENT_PREFERENCE (
	advert_id INT(10) UNSIGNED NOT NULL,
	institution_id INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (advert_id, institution_id),
	INDEX (institution_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id),
	FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id)
) ENGINE = INNODB
;
