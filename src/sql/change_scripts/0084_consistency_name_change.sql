ALTER TABLE comment
CHANGE COLUMN application_interview_status application_interview_state VARCHAR(20)
AFTER application_interested
;