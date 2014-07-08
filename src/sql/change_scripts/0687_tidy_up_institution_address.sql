ALTER TABLE INSTITUTION_ADDRESS
	CHANGE COLUMN address_region address_district VARCHAR(255) AFTER address_town,
	DROP FOREIGN KEY institution_address_ibfk_1,
	DROP COLUMN institution_id
;

ALTER TABLE INSTITUTION
	DROP FOREIGN KEY institution_ibfk_6,
	DROP COLUMN institution_domicile_id
;
