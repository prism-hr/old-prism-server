DROP TABLE IF EXISTS OFFER_RECOMMENDED_COMMENT;

CREATE TABLE OFFER_RECOMMENDED_COMMENT (
	id INT(10) UNSIGNED NOT NULL,
	project_title VARCHAR(255) NULL DEFAULT NULL,
	project_abstract VARCHAR(2000) NULL DEFAULT NULL,
	recommended_start_date DATE NULL DEFAULT NULL,
	recommended_conditions_available TINYINT(1) NULL DEFAULT NULL,
	recommended_conditions VARCHAR(1000) NULL DEFAULT NULL,
	comment_type VARCHAR(50) NULL DEFAULT NULL
)
ENGINE=InnoDB;

INSERT INTO OFFER_RECOMMENDED_COMMENT (id, project_title, project_abstract, recommended_start_date,
	recommended_conditions_available, recommended_conditions, comment_type)
	SELECT APPROVAL_COMMENT.id, APPROVAL_COMMENT.project_title, APPROVAL_COMMENT.project_abstract,
		APPROVAL_COMMENT.recommended_start_date, APPROVAL_COMMENT.recommended_conditions_available, 
		APPROVAL_COMMENT.recommended_conditions, "OFFER_RECOMMENDED_COMMENT"
	FROM APPLICATION_FORM
		INNER JOIN (SELECT MAX(APPROVAL_ROUND.id) AS approval_id,
							APPROVAL_ROUND.application_form_id AS application_id
						FROM APPROVAL_ROUND
						WHERE APPROVAL_ROUND.application_form_id IS NOT NULL
						GROUP BY APPROVAL_ROUND.application_form_id) AS LATEST_APPROVAL
			ON APPLICATION_FORM.id = LATEST_APPROVAL.application_id
		INNER JOIN (SELECT MAX(COMMENT.id) AS comment_id,
							COUNT(COMMENT.id) AS comment_count,
							COMMENT.application_form_id AS application_id
						FROM COMMENT INNER JOIN APPROVAL_COMMENT
							ON COMMENT.id = APPROVAL_COMMENT.id
						GROUP BY COMMENT.application_form_id) AS LATEST_APPROVAL_COMMENT
			ON APPLICATION_FORM.id = LATEST_APPROVAL_COMMENT.application_id
		INNER JOIN APPROVAL_ROUND 
			ON LATEST_APPROVAL.approval_id = APPROVAL_ROUND.id
		INNER JOIN APPROVAL_COMMENT
			ON LATEST_APPROVAL_COMMENT.comment_id = APPROVAL_COMMENT.id
		INNER JOIN COMMENT
			ON APPROVAL_COMMENT.id = COMMENT.id
	WHERE APPLICATION_FORM.status = "APPROVED"
		AND LATEST_APPROVAL_COMMENT.comment_count > 1
		AND APPROVAL_ROUND.created_date <= COMMENT.created_timestamp;
		
DELETE APPROVAL_COMMENT.*
FROM APPROVAL_COMMENT INNER JOIN OFFER_RECOMMENDED_COMMENT
	ON APPROVAL_COMMENT.id = OFFER_RECOMMENDED_COMMENT.id;
	
DELETE OFFER_RECOMMENDED_COMMENT.*
FROM OFFER_RECOMMENDED_COMMENT
WHERE LENGTH(OFFER_RECOMMENDED_COMMENT.project_title) = 0;