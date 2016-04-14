ALTER TABLE comment
ADD COLUMN application_interview_status VARCHAR(20)
AFTER application_interested
;

UPDATE comment
SET application_interview_status = 'TAKEN_PLACE'
WHERE action_id = 'APPLICATION_ASSIGN_INTERVIEWERS'
      AND application_interview_datetime < created_timestamp
;

UPDATE comment
SET application_interview_status = 'SCHEDULED'
WHERE action_id = 'APPLICATION_ASSIGN_INTERVIEWERS'
      AND application_interview_datetime >= created_timestamp
;

UPDATE comment
SET application_interview_status = 'TO_BE_SCHEDULED'
WHERE action_id = 'APPLICATION_ASSIGN_INTERVIEWERS'
      AND application_interview_datetime IS NULL
;

ALTER TABLE comment_competence
MODIFY COLUMN rating INT(1) UNSIGNED NULL
;
