DELIMITER ??

	CREATE PROCEDURE UPDATE_RAISES_URGENT_FLAG ()
	
	BEGIN
	
		UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
			ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
    	SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
			APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
    	WHERE deadline_timestamp < CURRENT_DATE();
		
		SELECT "COMPLETED";
	
	END
	??

	CREATE PROCEDURE DELETE_USER_FROM_ROLE (
		IN in_registered_user_id INT(10) UNSIGNED,
		IN in_application_role_id VARCHAR(50))
		
	BEGIN
	
		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
		   AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		   
		DELETE APPLICATION_FORM_USER_ROLE.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		
		SELECT "COMPLETED";
	
	END
	??

	CREATE PROCEDURE DELETE_USER_FROM_PROGRAM_ROLE (
		IN in_registered_user_id INT(10) UNSIGNED, 
		IN in_program_id INT(10) UNSIGNED, 
		IN in_application_role_id VARCHAR(50))
	
	BEGIN
	
		DELETE APPLICATION_FORM_ACTION_REQUIRED.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM.program_id = in_program_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		   
		DELETE APPLICATION_FORM_USER_ROLE.*
		FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
			ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
		INNER JOIN APPLICATION_FORM
			ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
		WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_registered_user_id
			AND APPLICATION_FORM.program_id = in_program_id
			AND APPLICATION_FORM_USER_ROLE.application_role_id = in_application_role_id;
		
		SELECT "COMPLETED";
		   
	END
	??

	CREATE PROCEDURE INSERT_USER_IN_ROLE (
		IN in_registered_user_id INT(10) UNSIGNED, 
		IN in_application_role_id VARCHAR(50))
	BEGIN

		DECLARE in_base_role_to_copy INT(10) UNSIGNED;
		
		SET in_base_role_to_copy = (
			SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
			FROM APPLICATION_FORM_USER_ROLE
			WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR");
			
		INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
			application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
			SELECT application_form_id, in_registered_user_id, in_application_role_id, is_interested_in_applicant, 
				update_timestamp, raises_update_flag, raises_urgent_flag
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
				AND APPLICATION_FORM.status != "VALIDATION";
		
		INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, 
			action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
			SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
				APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
				APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag		
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
				ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
			INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
				AND APPLICATION_FORM_ACTION_REQUIRED.action_id = "CONFIRM_ELIGIBILITY";
		
		IF in_application_role_id = "SUPERADMINISTRATOR" THEN
		
			INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
				application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
				SELECT application_form_id, in_registered_user_id, in_application_role_id, is_interested_in_applicant, 
					update_timestamp, raises_update_flag, raises_urgent_flag
				FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
				WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
					AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
					AND APPLICATION_FORM.status = "VALIDATION";
				
			INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, 
				action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
				SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
					APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
					APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag		
				FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
					ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
				INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
				WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
					AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
					AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
					AND APPLICATION_FORM_ACTION_REQUIRED.action_id != "CONFIRM_ELIGIBILITY";
				
		END IF;
		
		SELECT "COMPLETED";
		
	END
	??

	CREATE PROCEDURE INSERT_USER_IN_PROGRAM_ROLE (
		IN in_registered_user_id INT(10) UNSIGNED, 
		IN in_program_id INT(10) UNSIGNED, 
		IN in_application_role_id VARCHAR(50))

	BEGIN

		DECLARE in_base_role_to_copy INT(10) UNSIGNED;
		
		SET in_base_role_to_copy = (
			SELECT MIN(APPLICATION_FORM_USER_ROLE.registered_user_id)
			FROM APPLICATION_FORM_USER_ROLE
			WHERE APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR");
			
		INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id,
			application_role_id, is_interested_in_applicant, update_timestamp, raises_update_flag, raises_urgent_flag)
			SELECT application_form_id, in_registered_user_id, in_application_role_id, is_interested_in_applicant, 
				update_timestamp, raises_update_flag, raises_urgent_flag
			FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM
				ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
			WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
				AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
				AND APPLICATION_FORM.program_id = in_program_id;
		
		IF in_application_role_id = "ADMINISTRATOR" THEN
		
			INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, 
				action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
				SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
					APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
					APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag		
				FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
					ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
				INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
					ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
				WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
					AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
					AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
					AND APPLICATION_FORM_ACTION_REQUIRED.action_id NOT IN ("CONFIRM_ELIGIBILITY", "CONFIRM_OFFER_RECOMMENDATION");
		
		ELSEIF in_application_role_id = "APPROVER" THEN
		
				INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, 
					action_id, deadline_timestamp, bind_deadline_to_due_date, raises_urgent_flag)
					SELECT APPLICATION_FORM_USER_ROLE_COPY.id, APPLICATION_FORM_ACTION_REQUIRED.action_id,
						APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date,
						APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag		
					FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
						ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
					INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE_COPY
						ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE_COPY.application_form_id
					INNER JOIN APPLICATION_FORM
						ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
					WHERE APPLICATION_FORM_USER_ROLE.registered_user_id = in_base_role_to_copy
						AND APPLICATION_FORM_USER_ROLE_COPY.registered_user_id = in_registered_user_id
						AND APPLICATION_FORM_USER_ROLE.application_role_id = "SUPERADMINISTRATOR"
						AND APPLICATION_FORM.status = "APPROVAL"
						AND APPLICATION_FORM_ACTION_REQUIRED.action_id != "CONFIRM_ELIGIBILITY";
		
		END IF;
		
		SELECT "COMPLETED";
		
	END
	??

DELIMITER ;