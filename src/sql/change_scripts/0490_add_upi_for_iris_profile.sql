ALTER TABLE REGISTERED_USER ADD COLUMN upi VARCHAR(20) NULL DEFAULT NULL  AFTER filtering_id
;

ALTER TABLE REGISTERED_USER ADD INDEX `upi_idx` (upi)
;
