ALTER TABLE COMMENT
	CHANGE COLUMN application_qualified application_eligible VARCHAR(10),
	DROP COLUMN application_competent_in_work_language,
	DROP FOREIGN KEY comment_ibfk_13,
	DROP COLUMN application_residence_state_id,
	ADD COLUMN application_interested INT(1) UNSIGNED AFTER application_eligible,
	ADD INDEX (application_id, application_interested)
;

UPDATE COMMENT
SET application_interested = IF(application_desire_to_interview = 1 OR application_desire_to_recruit = 1, 1, 0)
;
