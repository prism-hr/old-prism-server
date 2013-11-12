ALTER TABLE APPLICATION_FORM
	ADD COLUMN next_status VARCHAR(50) AFTER status,
	ADD INDEX (next_status),
	ADD FOREIGN KEY (status) REFERENCES STATE (id),
	ADD FOREIGN KEY (next_status) REFERENCES STATE (id)
;

UPDATE APPLICATION_FORM LEFT JOIN REVIEW_ROUND
	ON APPLICATION_FORM.latest_review_round_id = REVIEW_ROUND.id
LEFT JOIN INTERVIEW
	ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
LEFT JOIN APPROVAL_ROUND
	ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
LEFT JOIN (
	SELECT MAX(STATECHANGE_COMMENT.id) AS id,
		MAX(COMMENT.created_timestamp) AS created_timestamp,
		COMMENT.application_form_id AS application_form_id
	FROM STATECHANGE_COMMENT INNER JOIN COMMENT
		ON STATECHANGE_COMMENT.id = COMMENT.id
	GROUP BY COMMENT.application_form_id) AS LATEST_STATECHANGE_COMMENT
	ON APPLICATION_FORM.id = LATEST_STATECHANGE_COMMENT.application_form_id
LEFT JOIN STATECHANGE_COMMENT
	ON LATEST_STATECHANGE_COMMENT.id = STATECHANGE_COMMENT.id
SET APPLICATION_FORM.next_status = 
	CASE
		WHEN APPLICATION_FORM.status = "VALIDATION" 
			AND APPLICATION_FORM.latest_review_round_id IS NULL
			AND APPLICATION_FORM.latest_interview_id IS NULL
			AND APPLICATION_FORM.latest_approval_round_id IS NULL THEN
			STATECHANGE_COMMENT.next_status
		WHEN APPLICATION_FORM.status = "REVIEW" 
			AND REVIEW_ROUND.created_date < LATEST_STATECHANGE_COMMENT.created_timestamp THEN
			STATECHANGE_COMMENT.next_status
		WHEN APPLICATION_FORM.status = "INTERVIEW"
			AND INTERVIEW.created_date < LATEST_STATECHANGE_COMMENT.created_timestamp THEN
			STATECHANGE_COMMENT.next_status
		WHEN APPLICATION_FORM.status = "APPROVAL" 
			AND APPROVAL_ROUND.created_date < LATEST_STATECHANGE_COMMENT.created_timestamp THEN
			STATECHANGE_COMMENT.next_status
	END
WHERE APPLICATION_FORM.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
;

INSERT INTO APPLICATION_ROLE (id, update_visibility, do_send_update_notification)
VALUES ("SUGGESTEDSUPERVISOR", 0, 0)
;

ALTER TABLE APPLICATION_FORM_ACTION_OPTIONAL
	ADD COLUMN raises_urgent_flag INT(1) UNSIGNED DEFAULT 0,
	ADD INDEX (raises_urgent_flag)
;

CREATE PROCEDURE SELECT_USER_APPLICATION_FORM_ACTION_LIST (
	IN in_registered_user_in INT(10) UNSIGNED, 
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_state_id VARCHAR(50))
BEGIN

	(SELECT APPLICATION_FORM_ACTION_REQUIRED.action_id AS action,
		MAX(APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag) AS raisesUrgentFlag
	FROM APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
	WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
	GROUP BY APPLICATION_FORM_ACTION_REQUIRED.action_id ASC)
		UNION
	(SELECT APPLICATION_FORM_ACTION_OPTIONAL.action_id AS action,
		APPLICATION_FORM_ACTION_OPTIONAL.raises_urgent_flag AS raisesUrgentFlag
	FROM APPLICATION_FORM_ACTION_OPTIONAL INNER JOIN APPLICATION_FORM_USER_ROLE
		ON APPLICATION_FORM_ACTION_OPTIONAL.application_role_id = APPLICATION_FORM_USER_ROLE.application_role_id
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id	
		AND APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		AND APPLICATION_FORM_ACTION_OPTIONAL.state_id = in_state_id
	GROUP BY APPLICATION_FORM_ACTION_OPTIONAL.action_id ASC);
	
END
;

CREATE PROCEDURE UPDATE_APPLICATION_FORM_ACTION_REQUIRED_DEADLINE (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_deadline_timestamp DATE)
BEGIN
	
	DECLARE in_raises_urgent_flag INT(1) UNSIGNED;
	
	SET in_raises_urgent_flag = (
		SELECT IF (in_deadline_timestamp <= CURRENT_DATE(),
						1,
						0));

	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED 
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	SET APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp = in_deadline_timestamp,
		APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = in_raises_urgent_flag
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date = 1;
		
END
;

CREATE PROCEDURE INSERT_APPLICATION_FORM_USER_ROLE_UPDATE (
	IN in_application_form_id INT(10) UNSIGNED, 
	IN in_registered_user_id INT(10) UNSIGNED, 
	IN in_update_timestamp DATE, 
	IN in_update_visibility INT(1) UNSIGNED)
BEGIN

	UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
		ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
	SET APPLICATION_FORM_USER_ROLE.update_timestamp = in_update_timestamp,
		APPLICATION_FORM_USER_ROLE.raises_update_flag = 
			IF (APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id,
				0,
				1)
	WHERE APPLICATION_FORM_USER_ROLE.application_form_id = in_application_form_id
		AND APPLICATION_ROLE.update_visibility >= in_update_visibility;
	
END
;
