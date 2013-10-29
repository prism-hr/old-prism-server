ALTER TABLE APPROVAL_COMMENT
	ADD COLUMN supervisor_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN secondary_supervisor_id INT(10) UNSIGNED AFTER supervisor_id,
	ADD PRIMARY KEY (id),
	ADD INDEX (supervisor_id),
	ADD INDEX (secondary_supervisor_id),
	ADD FOREIGN KEY (id) REFERENCES COMMENT (id),
	ADD FOREIGN KEY (supervisor_id) REFERENCES SUPERVISOR (id),
	ADD FOREIGN KEY (secondary_supervisor_id) REFERENCES SUPERVISOR (id)
;

ALTER TABLE SUPERVISION_CONFIRMATION_COMMENT
	ADD COLUMN secondary_supervisor_id INT(10) UNSIGNED AFTER supervisor_id,
	ADD INDEX (secondary_supervisor_id),
	ADD FOREIGN KEY (secondary_supervisor_id) REFERENCES SUPERVISOR (id)
;	

ALTER TABLE OFFER_RECOMMENDED_COMMENT
	ADD COLUMN supervisor_id INT(10) UNSIGNED AFTER id,
	ADD COLUMN secondary_supervisor_id INT(10) UNSIGNED AFTER supervisor_id,
	ADD INDEX (supervisor_id),
	ADD INDEX (secondary_supervisor_id),
	ADD FOREIGN KEY (supervisor_id) REFERENCES SUPERVISOR (id),
	ADD FOREIGN KEY (secondary_supervisor_id) REFERENCES SUPERVISOR (id)
;

UPDATE OFFER_RECOMMENDED_COMMENT INNER JOIN COMMENT
	ON OFFER_RECOMMENDED_COMMENT.id = COMMENT.id
INNER JOIN COMMENT AS COMMENT2
	ON COMMENT.application_form_id = COMMENT2.application_form_id
INNER JOIN APPROVAL_EVALUATION_COMMENT 
	ON COMMENT2.id = APPROVAL_EVALUATION_COMMENT.id
INNER JOIN SUPERVISOR
	ON APPROVAL_EVALUATION_COMMENT.approval_id = SUPERVISOR.approval_round_id
INNER JOIN STATECHANGE_COMMENT
	ON APPROVAL_EVALUATION_COMMENT.id = STATECHANGE_COMMENT.id
SET OFFER_RECOMMENDED_COMMENT.supervisor_id = SUPERVISOR.id
WHERE SUPERVISOR.is_primary = 1
	AND STATECHANGE_COMMENT.next_status = "APPROVED"
;

UPDATE OFFER_RECOMMENDED_COMMENT INNER JOIN COMMENT
	ON OFFER_RECOMMENDED_COMMENT.id = COMMENT.id
INNER JOIN COMMENT AS COMMENT2
	ON COMMENT.application_form_id = COMMENT2.application_form_id
INNER JOIN APPROVAL_EVALUATION_COMMENT 
	ON COMMENT2.id = APPROVAL_EVALUATION_COMMENT.id
INNER JOIN SUPERVISOR
	ON APPROVAL_EVALUATION_COMMENT.approval_id = SUPERVISOR.approval_round_id
INNER JOIN STATECHANGE_COMMENT
	ON APPROVAL_EVALUATION_COMMENT.id = STATECHANGE_COMMENT.id
SET OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id = SUPERVISOR.id
WHERE SUPERVISOR.is_primary = 0
	AND STATECHANGE_COMMENT.next_status = "APPROVED"
;

UPDATE OFFER_RECOMMENDED_COMMENT
SET OFFER_RECOMMENDED_COMMENT.supervisor_id = OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id
WHERE OFFER_RECOMMENDED_COMMENT.supervisor_id IS NULL
	AND OFFER_RECOMMENDED_COMMENT.secondary_supervisor_id IS NOT NULL
;

UPDATE APPROVAL_COMMENT INNER JOIN COMMENT
	ON APPROVAL_COMMENT.id = COMMENT.id
LEFT JOIN (
	SELECT APPROVAL_ROUND.id AS approval_round_id, 
		APPROVAL_ROUND.application_form_id AS application_form_id,
		APPROVAL_ROUND.created_date AS approval_round_start, 
		COMMENT.created_timestamp AS approval_round_end
	FROM APPROVAL_ROUND LEFT JOIN APPROVAL_EVALUATION_COMMENT
		ON APPROVAL_ROUND.id = APPROVAL_EVALUATION_COMMENT.approval_id
	LEFT JOIN COMMENT 
		ON APPROVAL_EVALUATION_COMMENT.id = COMMENT.id) AS APPROVAL_ROUND_BOUNDARY
	ON COMMENT.application_form_id = APPROVAL_ROUND_BOUNDARY.application_form_id
INNER JOIN SUPERVISOR
	ON APPROVAL_ROUND_BOUNDARY.approval_round_id = SUPERVISOR.approval_round_id
SET APPROVAL_COMMENT.supervisor_id = SUPERVISOR.id
WHERE SUPERVISOR.is_primary = 1
	AND COMMENT.created_timestamp >= APPROVAL_ROUND_BOUNDARY.approval_round_start
	AND (COMMENT.created_timestamp <= APPROVAL_ROUND_BOUNDARY.approval_round_end
		OR APPROVAL_ROUND_BOUNDARY.approval_round_end IS NULL)
;

UPDATE APPROVAL_COMMENT INNER JOIN COMMENT
	ON APPROVAL_COMMENT.id = COMMENT.id
LEFT JOIN (
	SELECT APPROVAL_ROUND.id AS approval_round_id, 
		APPROVAL_ROUND.application_form_id AS application_form_id,
		APPROVAL_ROUND.created_date AS approval_round_start, 
		COMMENT.created_timestamp AS approval_round_end
	FROM APPROVAL_ROUND LEFT JOIN APPROVAL_EVALUATION_COMMENT
		ON APPROVAL_ROUND.id = APPROVAL_EVALUATION_COMMENT.approval_id
	LEFT JOIN COMMENT 
		ON APPROVAL_EVALUATION_COMMENT.id = COMMENT.id) AS APPROVAL_ROUND_BOUNDARY
	ON COMMENT.application_form_id = APPROVAL_ROUND_BOUNDARY.application_form_id
INNER JOIN SUPERVISOR
	ON APPROVAL_ROUND_BOUNDARY.approval_round_id = SUPERVISOR.approval_round_id
SET APPROVAL_COMMENT.secondary_supervisor_id = SUPERVISOR.id
WHERE SUPERVISOR.is_primary = 0
	AND COMMENT.created_timestamp >= APPROVAL_ROUND_BOUNDARY.approval_round_start
	AND (COMMENT.created_timestamp <= APPROVAL_ROUND_BOUNDARY.approval_round_end
		OR APPROVAL_ROUND_BOUNDARY.approval_round_end IS NULL)
;

UPDATE APPROVAL_COMMENT
SET APPROVAL_COMMENT.supervisor_id = APPROVAL_COMMENT.secondary_supervisor_id
WHERE APPROVAL_COMMENT.supervisor_id IS NULL
	AND APPROVAL_COMMENT.secondary_supervisor_id IS NOT NULL
;

UPDATE SUPERVISION_CONFIRMATION_COMMENT INNER JOIN COMMENT
	ON SUPERVISION_CONFIRMATION_COMMENT.id = COMMENT.id
LEFT JOIN (
	SELECT APPROVAL_ROUND.id AS approval_round_id, 
		APPROVAL_ROUND.application_form_id AS application_form_id,
		APPROVAL_ROUND.created_date AS approval_round_start, 
		COMMENT.created_timestamp AS approval_round_end
	FROM APPROVAL_ROUND LEFT JOIN APPROVAL_EVALUATION_COMMENT
		ON APPROVAL_ROUND.id = APPROVAL_EVALUATION_COMMENT.approval_id
	LEFT JOIN COMMENT 
		ON APPROVAL_EVALUATION_COMMENT.id = COMMENT.id) AS APPROVAL_ROUND_BOUNDARY
	ON COMMENT.application_form_id = APPROVAL_ROUND_BOUNDARY.application_form_id
INNER JOIN SUPERVISOR
	ON APPROVAL_ROUND_BOUNDARY.approval_round_id = SUPERVISOR.approval_round_id
SET SUPERVISION_CONFIRMATION_COMMENT.secondary_supervisor_id = SUPERVISOR.id
WHERE SUPERVISOR.is_primary = 0
	AND COMMENT.created_timestamp >= APPROVAL_ROUND_BOUNDARY.approval_round_start
	AND (COMMENT.created_timestamp <= APPROVAL_ROUND_BOUNDARY.approval_round_end
		OR APPROVAL_ROUND_BOUNDARY.approval_round_end IS NULL)
;