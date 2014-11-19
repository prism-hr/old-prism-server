CREATE PROCEDURE restart_approval()
BEGIN
	DECLARE done INT DEFAULT 0;
	DECLARE application_id INT(10);
	DECLARE user_id INT(10);
	DECLARE app_cursor CURSOR FOR SELECT af.id FROM APPLICATION_FORM af
		JOIN APPROVAL_ROUND ar ON af.latest_approval_round_id = ar.id
		JOIN SUPERVISOR s ON s.approval_round_id = ar.id
		WHERE status = 'APPROVAL' AND af.pending_approval_restart = 0
		GROUP BY ar.id
		HAVING MAX(s.is_primary) = 0;	
	
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

	SELECT id FROM REGISTERED_USER WHERE username = 'prism@ucl.ac.uk' INTO user_id;

	OPEN app_cursor;

	read_loop: LOOP
		FETCH app_cursor INTO application_id;

		IF done THEN
			LEAVE read_loop;
		END IF;
		
		INSERT
		INTO
			COMMENT
			(application_form_id, comment, user_id)
		VALUES
			(application_id, "Approval restarted by UCL Prism. Approve the application to transmit it to the student record system and generate the offer letter.", user_id);

		INSERT
		INTO
			REQUEST_RESTART_COMMENT
			(comment_type, id) 
		VALUES
			('REQUEST_RESTART', LAST_INSERT_ID());

		UPDATE APPLICATION_FORM af
			SET
				pending_approval_restart = 1,
				approver_requested_restart_id = user_id,
				approver_user_id = NULL,
				status = 'APPROVAL'
		WHERE af.id = application_id;

	END LOOP;

	CLOSE app_cursor;

END 
;

CALL restart_approval()
;

DROP PROCEDURE `restart_approval`
;
