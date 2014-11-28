UPDATE APPLICATION INNER JOIN (
	SELECT COMMENT.application_id AS application_id, 
		COMMENT.transition_state_id AS transition_state_id,
		COMMENT.state_id AS state_id 
	FROM COMMENT INNER JOIN (
		SELECT application_id AS application_id,
			MAX(created_timestamp) AS created_timestamp
		FROM COMMENT
		WHERE application_id IS NOT NULL
			AND transition_state_id IS NOT NULL
		GROUP BY application_id) AS TRANSITION_TIMESTAMP
		ON COMMENT.application_id = TRANSITION_TIMESTAMP.application_id
			AND COMMENT.created_timestamp = TRANSITION_TIMESTAMP.created_timestamp
	WHERE COMMENT.transition_state_id IS NOT NULL) AS TRANSITION_COMMENT
	ON APPLICATION.id = TRANSITION_COMMENT.application_id
SET APPLICATION.state_id = TRANSITION_COMMENT.transition_state_id,
	APPLICATION.previous_state_id = TRANSITION_COMMENT.state_id
;
