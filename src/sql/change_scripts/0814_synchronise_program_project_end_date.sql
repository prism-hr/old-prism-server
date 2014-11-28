UPDATE PROGRAM
SET end_date = due_date
WHERE state_id IN ("PROGRAM_APPROVED", "PROGRAM_DEACTIVATED")
	AND imported = 0
;

UPDATE PROJECT
SET end_date = due_date
WHERE state_id IN ("PROJECT_APPROVED", "PROJECT_DEACTIVATED")
;
