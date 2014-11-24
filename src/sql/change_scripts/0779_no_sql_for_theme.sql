DROP TABLE APPLICATION_THEME
;

ALTER TABLE APPLICATION
	ADD COLUMN theme TEXT AFTER application_additional_information_id
;
