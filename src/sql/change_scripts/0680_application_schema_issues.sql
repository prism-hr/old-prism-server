/* Location filtering for organisations and opportunities */

CREATE TABLE INSTITUTION_DOMICILE_REGION (
	id VARCHAR(10) NOT NULL,
	institution_domicile_id VARCHAR(10) NOT NULL,
	parent_region_id VARCHAR(10),
	region_type VARCHAR(250) NOT NULL,
	name VARCHAR(250) NOT NULL,
	other_name TEXT,
	enabled INT(1) UNSIGNED NOT NULL,
	PRIMARY KEY (id),
	UNIQUE INDEX (institution_domicile_id, parent_region_id, region_type, name),
	INDEX (parent_region_id),
	INDEX (region_type),
	INDEX (name),
	INDEX (enabled),
	FOREIGN KEY (institution_domicile_id) REFERENCES INSTITUTION_DOMICILE (id),
	FOREIGN KEY (parent_region_id) REFERENCES INSTITUTION_DOMICILE_REGION (id)
) ENGINE = INNODB
;

CREATE TABLE INSTITUTION_ADDRESS LIKE ADDRESS
;

ALTER TABLE INSTITUTION_ADDRESS
	ADD COLUMN institution_id INT(10) UNSIGNED NOT NULL AFTER id,
	ADD INDEX (institution_id),
	ADD FOREIGN KEY (institution_id) REFERENCES INSTITUTION (id),
	CHANGE COLUMN domicile_id institution_domicile_id VARCHAR(10) NOT NULL,
	ADD FOREIGN KEY (institution_domicile_id) REFERENCES INSTITUTION_DOMICILE (id),
	ADD COLUMN institution_domicile_region_id VARCHAR(10) AFTER institution_domicile_id,
	ADD INDEX (institution_domicile_region_id),
	ADD FOREIGN KEY (institution_domicile_region_id) REFERENCES INSTITUTION_DOMICILE_REGION (id)
;

ALTER TABLE INSTITUTION
	DROP INDEX domicile_id,
	DROP INDEX institution_domicile_id,
	ADD UNIQUE INDEX (institution_domicile_id, name),
	ADD COLUMN institution_address_id INT(10) UNSIGNED,
	ADD INDEX (institution_address_id),
	ADD FOREIGN KEY (institution_address_id) REFERENCES INSTITUTION_ADDRESS (id)
;

ALTER TABLE ADVERT
	ADD COLUMN institution_address_id INT(10) UNSIGNED,
	ADD INDEX (institution_address_id),
	ADD FOREIGN KEY (institution_address_id) REFERENCES INSTITUTION_ADDRESS (id),
	DROP COLUMN funding
;

INSERT INTO INSTITUTION_DOMICILE_REGION (id, institution_domicile_id, parent_region_id, region_type, name, enabled)
VALUES ("GB-ENG", "GB", "GB-ENG", "Country", "England", 1),
	("GB-LDN", "GB", "GB-ENG", "City Corporation", "London, City of", 1)
;

INSERT INTO INSTITUTION_ADDRESS (institution_id, institution_domicile_id, institution_domicile_region_id, address_line_1, address_town, address_code)
VALUES (5243, "GB", "GB-LDN", "Gower Street",  "London", "WC1E 6BT")
;

UPDATE ADVERT
SET institution_address_id = 1
;

UPDATE INSTITUTION
SET institution_address_id = 1
;

ALTER TABLE ADVERT
	MODIFY COLUMN institution_address_id INT(10) UNSIGNED NOT NULL
;

ALTER TABLE ADVERT
	ADD COLUMN fee_interval VARCHAR(10) AFTER month_study_duration,
	ADD COLUMN fee_value DECIMAL(10,2) AFTER fee_interval,
	ADD COLUMN fee_annualised DECIMAL (10,2) AFTER fee_value,
	ADD COLUMN pay_interval VARCHAR(10) AFTER fee_annualised,
	ADD COLUMN pay_value DECIMAL(10,2) AFTER pay_interval,
	ADD COLUMN pay_annualised DECIMAL (10,2) AFTER pay_value 
;
