ALTER TABLE PROGRAM
	ADD COLUMN end_date DATE AFTER previous_state_id
;

ALTER TABLE PROJECT
	ADD COLUMN end_date DATE AFTER previous_state_id
;
