/* Opportunity categories for adverts */

CREATE TABLE ADVERT_OPPORTUNITY_CATEGORY (
	id INT(10) UNSIGNED NOT NULL,
	parent_category_id INT(10) UNSIGNED,
	name VARCHAR(250) NOT NULL,
	enabled INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	INDEX (parent_category_id),
	UNIQUE INDEX (name),
	INDEX (enabled),
	FOREIGN KEY (parent_category_id) REFERENCES ADVERT_OPPORTUNITY_CATEGORY (id)
) ENGINE = INNODB
;

CREATE TABLE ADVERT_CATEGORY (
	advert_id INT(10) UNSIGNED,
	advert_opportunity_category_id INT(10) UNSIGNED,
	PRIMARY KEY (advert_id, advert_opportunity_category_id),
	INDEX (advert_opportunity_category_id),
	FOREIGN KEY (advert_id) REFERENCES ADVERT (id),
	FOREIGN KEY (advert_opportunity_category_id) REFERENCES ADVERT_OPPORTUNITY_CATEGORY (id)
) ENGINE = INNODB
;
