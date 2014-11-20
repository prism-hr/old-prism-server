DELETE
FROM STATE_DURATION
WHERE system_id IS NULL
;

UPDATE STATE_DURATION
SET system_default = 1
;

DELETE 
FROM NOTIFICATION_CONFIGURATION
WHERE system_id IS NULL
;

UPDATE NOTIFICATION_CONFIGURATION
SET system_default = 1
;

ALTER TABLE COMMENT
	ADD COLUMN comment_custom_question TEXT AFTER comment_custom_question_id
;

UPDATE COMMENT INNER JOIN COMMENT_CUSTOM_QUESTION
	ON COMMENT.comment_custom_question_id = COMMENT_CUSTOM_QUESTION.id
SET COMMENT.comment_custom_question = COMMENT_CUSTOM_QUESTION.content
;

ALTER TABLE COMMENT
	DROP FOREIGN KEY comment_ibfk_17,
	DROP COLUMN comment_custom_question_id
;
