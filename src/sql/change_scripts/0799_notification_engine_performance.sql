ALTER TABLE APPLICATION
	ADD COLUMN last_reminded_request_individual DATE AFTER updated_timestamp,
	ADD COLUMN last_reminded_request_syndicated DATE AFTER last_reminded_request_individual,
	ADD COLUMN last_notified_update_syndicated DATE AFTER last_reminded_request_syndicated,
	ADD INDEX (last_reminded_request_individual),
	ADD INDEX (last_reminded_request_syndicated),
	ADD INDEX (last_notified_update_syndicated)
;

ALTER TABLE PROJECT
	ADD COLUMN last_reminded_request_individual DATE AFTER updated_timestamp,
	ADD COLUMN last_reminded_request_syndicated DATE AFTER last_reminded_request_individual,
	ADD COLUMN last_notified_update_syndicated DATE AFTER last_reminded_request_syndicated,
	ADD INDEX (last_reminded_request_individual),
	ADD INDEX (last_reminded_request_syndicated),
	ADD INDEX (last_notified_update_syndicated)
;

ALTER TABLE PROGRAM
	ADD COLUMN last_reminded_request_individual DATE AFTER updated_timestamp,
	ADD COLUMN last_reminded_request_syndicated DATE AFTER last_reminded_request_individual,
	ADD COLUMN last_notified_update_syndicated DATE AFTER last_reminded_request_syndicated,
	ADD INDEX (last_reminded_request_individual),
	ADD INDEX (last_reminded_request_syndicated),
	ADD INDEX (last_notified_update_syndicated)
;

ALTER TABLE INSTITUTION
	ADD COLUMN last_reminded_request_individual DATE AFTER updated_timestamp,
	ADD COLUMN last_reminded_request_syndicated DATE AFTER last_reminded_request_individual,
	ADD COLUMN last_notified_update_syndicated DATE AFTER last_reminded_request_syndicated,
	ADD INDEX (last_reminded_request_individual),
	ADD INDEX (last_reminded_request_syndicated),
	ADD INDEX (last_notified_update_syndicated)
;

ALTER TABLE SYSTEM
	ADD COLUMN last_reminded_request_individual DATE AFTER updated_timestamp,
	ADD COLUMN last_reminded_request_syndicated DATE AFTER last_reminded_request_individual,
	ADD COLUMN last_notified_update_syndicated DATE AFTER last_reminded_request_syndicated,
	ADD INDEX (last_reminded_request_individual),
	ADD INDEX (last_reminded_request_syndicated),
	ADD INDEX (last_notified_update_syndicated)
;
