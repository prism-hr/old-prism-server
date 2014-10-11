DROP TABLE ADVERT_INDUSTRY
;

DROP TABLE ADVERT_SUBJECT
;

CREATE TABLE ADVERT_DOMAIN (
	advert_id INT(10) UNSIGNED NOT NULL,
	domain VARCHAR(50) NOT NULL,
	PRIMARY KEY (advert_id, domain),
	UNIQUE INDEX (domain, advert_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
) ENGINE = INNODB
;

CREATE TABLE ADVERT_INDUSTRY (
	advert_id INT(10) UNSIGNED NOT NULL,
	industry VARCHAR(50) NOT NULL,
	PRIMARY KEY (advert_id, industry),
	UNIQUE INDEX (industry, advert_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
) ENGINE = INNODB
;

CREATE TABLE ADVERT_FUNCTION (
	advert_id INT(10) UNSIGNED NOT NULL,
	function VARCHAR(50) NOT NULL,
	PRIMARY KEY (advert_id, function),
	UNIQUE INDEX (function, advert_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id)
) ENGINE = INNODB
;

ALTER TABLE ADVERT_TARGET_INSTITUTION
	DROP INDEX institution_id,
	ADD UNIQUE INDEX (institution_id, advert_id)
;

ALTER TABLE ADVERT_TARGET_PROGRAM_TYPE
	DROP INDEX program_type,
	ADD UNIQUE INDEX (program_type, advert_id)
;
