SELECT ROUND(AVG(COMMENT.application_rating), 2)
	FROM COMMENT
WHERE COMMENT.application_id = APPLICATION_LIST_BLOCK.id