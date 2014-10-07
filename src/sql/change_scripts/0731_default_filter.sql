ALTER TABLE RESOURCE_LIST_FILTER
	ADD COLUMN value_string VARCHAR(255) AFTER sort_order
;

ALTER TABLE APPLICATION
	CHANGE COLUMN application_rating_averave application_rating_average DECIMAL(3,2) UNSIGNED
;

ALTER TABLE SYSTEM
	CHANGE COLUMN name title VARCHAR(50) NOT NULL,
	ADD COLUMN last_data_imported_date DATE AFTER title
;
