ALTER TABLE INSTITUTION_DOMICILE
	MODIFY COLUMN location_x DECIMAL(18,14),
	MODIFY COLUMN location_y DECIMAL(18,14),
	MODIFY COLUMN location_view_ne_x DECIMAL(18,14),
	MODIFY COLUMN location_view_ne_y DECIMAL(18,14),
	MODIFY COLUMN location_view_sw_x DECIMAL(18,14),
	MODIFY COLUMN location_view_sw_y DECIMAL(18,14)
;

ALTER TABLE INSTITUTION_DOMICILE_REGION
	MODIFY COLUMN location_x DECIMAL(18,14),
	MODIFY COLUMN location_y DECIMAL(18,14),
	MODIFY COLUMN location_view_ne_x DECIMAL(18,14),
	MODIFY COLUMN location_view_ne_y DECIMAL(18,14),
	MODIFY COLUMN location_view_sw_x DECIMAL(18,14),
	MODIFY COLUMN location_view_sw_y DECIMAL(18,14)
;

ALTER TABLE INSTITUTION_ADDRESS
	MODIFY COLUMN location_x DECIMAL(18,14),
	MODIFY COLUMN location_y DECIMAL(18,14),
	MODIFY COLUMN location_view_ne_x DECIMAL(18,14),
	MODIFY COLUMN location_view_ne_y DECIMAL(18,14),
	MODIFY COLUMN location_view_sw_x DECIMAL(18,14),
	MODIFY COLUMN location_view_sw_y DECIMAL(18,14)
;

UPDATE INSTITUTION_DOMICILE
SET location_x = NULL, location_y = NULL,
	location_view_ne_x = NULL, location_view_ne_y = NULL,
	location_view_sw_x = NULL, location_view_sw_y = NULL
;

UPDATE INSTITUTION_DOMICILE_REGION
SET location_x = NULL, location_y = NULL,
	location_view_ne_x = NULL, location_view_ne_y = NULL,
	location_view_sw_x = NULL, location_view_sw_y = NULL
;

UPDATE INSTITUTION_ADDRESS
SET location_x = NULL, location_y = NULL,
	location_view_ne_x = NULL, location_view_ne_y = NULL,
	location_view_sw_x = NULL, location_view_sw_y = NULL
;

