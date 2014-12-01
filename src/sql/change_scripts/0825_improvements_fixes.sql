UPDATE COMMENT INNER JOIN (
	SELECT application_id AS application_id,
		created_timestamp AS created_timestamp
	FROM COMMENT
	WHERE action_id = "APPLICATION_COMPLETE_VALIDATION_STAGE"
	GROUP BY application_id
	ORDER BY application_id, created_timestamp DESC, id DESC) AS VALIDATION_COMMENT
	ON COMMENT.application_id = VALIDATION_COMMENT.application_id
SET COMMENT.created_timestamp = VALIDATION_COMMENT.created_timestamp - INTERVAL 1 SECOND,
	COMMENT.state_id = "APPLICATION_VALIDATION_PENDING_COMPLETION"
WHERE COMMENT.action_id = "APPLICATION_CONFIRM_ELIGIBILITY"
;
