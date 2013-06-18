ALTER TABLE REVIEW_COMMENT ADD COLUMN willing_to_work_with_applicant TINYINT(1) UNSIGNED NULL DEFAULT NULL  AFTER willing_to_interview
;

UPDATE REVIEW_COMMENT SET willing_to_work_with_applicant = false WHERE willing_to_interview = false
;

UPDATE REVIEW_COMMENT SET willing_to_work_with_applicant = true WHERE willing_to_interview = true
;