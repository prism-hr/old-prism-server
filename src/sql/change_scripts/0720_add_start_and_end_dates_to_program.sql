ALTER TABLE PROGRAM
	ADD COLUMN start_date DATE AFTER due_date,
	ADD COLUMN end_date DATE AFTER start_date
;
