INSERT IGNORE INTO APPLICATION_ROLE (id, update_visibility)
	SELECT "STATEADMINISTRATOR", 1
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id,
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM.id, APPLICATION_FORM.applicant_id, "APPLICANT", NULL,
		IF (APPLICATION_FORM.next_status IS NULL
			AND APPLICATION_FORM.status = "INTERVIEW"
			AND INTERVIEW.stage = "SCHEDULING"
			AND INTERVIEW_PARTICIPANT.responded = 0,
			"PROVIDE_INTERVIEW_AVAILABILITY",
			NULL),
		IF (APPLICATION_FORM.next_status IS NULL
			AND APPLICATION_FORM.status = "INTERVIEW"
			AND INTERVIEW.stage = "SCHEDULING"
			AND INTERVIEW_PARTICIPANT.responded = 0,
			DATE(INTERVIEW.created_date),
			NULL), 
		IF (APPLICATION_FORM.next_status IS NULL
			AND APPLICATION_FORM.status = "INTERVIEW"
			AND INTERVIEW.stage = "SCHEDULING"
			AND INTERVIEW_PARTICIPANT.responded = 0,
			0,
			NULL)
	FROM APPLICATION_FORM LEFT JOIN INTERVIEW
		ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
	LEFT JOIN INTERVIEW_PARTICIPANT
		ON INTERVIEW.id = INTERVIEW_PARTICIPANT.interview_id
		AND APPLICATION_FORM.applicant_id = INTERVIEW_PARTICIPANT.user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date,
	next_required_action_id2, next_required_action_deadline2, bind_deadline_to_due_date2)	
	SELECT APPLICATION_FORM.id, PROGRAM_ADMINISTRATOR_LINK.administrator_id, "ADMINISTRATOR", NULL,
			CASE
				WHEN APPLICATION_FORM.status = "VALIDATION" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_VALIDATION_STAGE",
						CASE
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "REVIEW" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_REVIEW_STAGE",
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						IF (INTERVIEW.stage = "SCHEDULING",
							"CONFIRM_INTERVIEW_ARRANGEMENTS",
							"COMPLETE_INTERVIEW_STAGE"),
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "APPROVAL" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_APPROVAL_STAGE",
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
			END,
			IF (APPLICATION_FORM.next_status IS NULL 
				OR APPLICATION_FORM.next_status IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED"),
				CASE
					WHEN APPLICATION_FORM.status = "VALIDATION" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "REVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "APPROVAL" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
				END,
				NULL),
			IF (APPLICATION_FORM.next_status IS NULL 
				OR APPLICATION_FORM.next_status IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED"),
				CASE
					WHEN APPLICATION_FORM.status = "VALIDATION" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "REVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "APPROVAL" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
				END,
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				"COMPLETE_INTERVIEW_STAGE",
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				DATE(APPLICATION_FORM.due_date),
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				1,
				NULL)			
			FROM APPLICATION_FORM INNER JOIN PROGRAM_ADMINISTRATOR_LINK
				ON APPLICATION_FORM.program_id = PROGRAM_ADMINISTRATOR_LINK.program_id
			LEFT JOIN REVIEW_ROUND
				ON APPLICATION_FORM.latest_review_round_id = REVIEW_ROUND.id
			LEFT JOIN INTERVIEW
				ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
			LEFT JOIN APPROVAL_ROUND
				ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
			WHERE APPLICATION_FORM.status != "UNSUBMITTED"
				AND APPLICATION_FORM.withdrawn_before_submit = 0
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)	
	SELECT APPLICATION_FORM.id, PROGRAM_APPROVER_LINK.registered_user_id, "APPROVER", NULL,
		IF (APPLICATION_FORM.status = "APPROVAL",
			IF (APPLICATION_FORM.next_status IS NULL,
				"COMPLETE_APPROVAL_STAGE",
				CASE 
					WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
						"ASSIGN_REVIEWERS"
					WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
						"ASSIGN_INTERVIEWERS"
					WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
						"ASSIGN_SUPERVISORS"
					WHEN APPLICATION_FORM.next_status = "APPROVED" THEN
						"CONFIRM_OFFER_RECOMMENDATION"
					WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
						"CONFIRM_REJECTION"
				END),
			NULL),
		IF (APPLICATION_FORM.status = "APPROVAL",
			IF (APPLICATION_FORM.next_status IS NULL,
				DATE(APPLICATION_FORM.due_date),
				CURRENT_DATE()),
			NULL),
		IF (APPLICATION_FORM.status = "APPROVAL",
			IF (APPLICATION_FORM.next_status IS NULL,
				1,
				0),
			NULL)
	FROM APPLICATION_FORM INNER JOIN PROGRAM_APPROVER_LINK
		ON APPLICATION_FORM.program_id = PROGRAM_APPROVER_LINK.program_id
	LEFT JOIN APPROVAL_ROUND
		ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
		AND APPLICATION_FORM.withdrawn_before_submit = 0
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id)
	SELECT APPLICATION_FORM.id, PROGRAM_VIEWER_LINK.viewer_id, "VIEWER"
	FROM APPLICATION_FORM INNER JOIN PROGRAM_VIEWER_LINK
		ON APPLICATION_FORM.program_id = PROGRAM_VIEWER_LINK.program_id
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
			AND APPLICATION_FORM.withdrawn_before_submit = 0
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id)
	SELECT APPLICATION_FORM.id, USER_ROLE_LINK.registered_user_id, "SUPERADMINISTRATOR"
	FROM APPLICATION_FORM INNER JOIN USER_ROLE_LINK
		ON USER_ROLE_LINK.application_role_id = "SUPERADMINISTRATOR"
	WHERE APPLICATION_FORM.status != "UNSUBMITTED"
			AND APPLICATION_FORM.withdrawn_before_submit = 0
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM.id, USER_ROLE_LINK.registered_user_id, "ADMITTER", NULL,
		IF (APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM.next_status NOT IN ("APPROVED", "REJECTED"),
			IF (ADMITTER_COMMENT.id IS NULL 
				AND (VALIDATION_COMMENT.qualified_for_phd = "UNSURE"
					OR VALIDATION_COMMENT.english_compentency_ok = "UNSURE"
					OR VALIDATION_COMMENT.home_or_overseas = "UNSURE"), 
				"CONFIRM_ELIGIBILITY", 
				NULL), 
			NULL),
		IF (APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM.next_status NOT IN ("APPROVED", "REJECTED"),
			IF (ADMITTER_COMMENT.id IS NULL 
				AND (VALIDATION_COMMENT.qualified_for_phd = "UNSURE"
					OR VALIDATION_COMMENT.english_compentency_ok = "UNSURE"
					OR VALIDATION_COMMENT.home_or_overseas = "UNSURE"), 
				DATE(COMMENT.created_timestamp), 
				NULL), 
			NULL),
		IF (APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM.next_status NOT IN ("APPROVED", "REJECTED"),
			IF (ADMITTER_COMMENT.id IS NULL 
				AND (VALIDATION_COMMENT.qualified_for_phd = "UNSURE"
					OR VALIDATION_COMMENT.english_compentency_ok = "UNSURE"
					OR VALIDATION_COMMENT.home_or_overseas = "UNSURE"), 
				0, 
				NULL), 
			NULL)
	FROM APPLICATION_FORM INNER JOIN USER_ROLE_LINK
		ON USER_ROLE_LINK.application_role_id = "ADMITTER"
	INNER JOIN COMMENT
		ON APPLICATION_FORM.id = COMMENT.application_form_id
	INNER JOIN VALIDATION_COMMENT
		ON COMMENT.id = VALIDATION_COMMENT.id
	LEFT JOIN ADMITTER_COMMENT
		ON COMMENT.id = ADMITTER_COMMENT.id
	WHERE APPLICATION_FORM.status NOT IN ("UNSUBMITTED", "VALIDATION")
			AND APPLICATION_FORM.withdrawn_before_submit = 0
	GROUP BY APPLICATION_FORM.id, USER_ROLE_LINK.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)	
	SELECT APPLICATION_FORM.id, APPLICATION_FORM_REFEREE.registered_user_id, "REFEREE", NULL,
		IF (APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM.next_status NOT IN ("APPROVED", "REJECTED"),
			IF (REFERENCE_COMMENT.id IS NULL 
				AND APPLICATION_FORM_REFEREE.declined = 0, 
				"PROVIDE_REFERENCE", 
				NULL), 
			NULL),
		IF (APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM.next_status NOT IN ("APPROVED", "REJECTED"),
			IF (REFERENCE_COMMENT.id IS NULL 
				AND APPLICATION_FORM_REFEREE.declined = 0, 
				CURRENT_DATE(), 
				NULL),
			NULL),
		IF (APPLICATION_FORM.status IN ("REVIEW", "INTERVIEW", "APPROVAL")
			AND APPLICATION_FORM.next_status NOT IN ("APPROVED", "REJECTED"), 
			IF (REFERENCE_COMMENT.id IS NULL 
				AND APPLICATION_FORM_REFEREE.declined = 0, 
				0, 
				NULL), 
			NULL)
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_REFEREE
		ON APPLICATION_FORM.id = APPLICATION_FORM_REFEREE.application_form_id
	LEFT JOIN COMMENT
		ON APPLICATION_FORM.id = COMMENT.application_form_id
	LEFT JOIN REFERENCE_COMMENT
		ON COMMENT.id = REFERENCE_COMMENT.id
	WHERE APPLICATION_FORM.status NOT IN ("UNSUBMITTED", "VALIDATION")
			AND APPLICATION_FORM.withdrawn_before_submit = 0
		AND APPLICATION_FORM_REFEREE.registered_user_id IS NOT NULL
	GROUP BY APPLICATION_FORM.id, APPLICATION_FORM_REFEREE.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id,
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM.id, REVIEWER.registered_user_id, "REVIEWER", 
		IF (REVIEW_COMMENT.id IS NOT NULL,
			IF (REVIEW_COMMENT.willing_to_interview = 1
				OR REVIEW_COMMENT.willing_to_work_with_applicant = 1,
				1,
				0),
			NULL),
		IF (REVIEW_COMMENT.id IS NULL
			AND APPLICATION_FORM.next_status IS NULL,
			"PROVIDE_REVIEW",
			NULL),
		IF (REVIEW_COMMENT.id IS NULL
			AND APPLICATION_FORM.next_status IS NULL,
			IF (APPLICATION_FORM.batch_deadline IS NULL
				OR APPLICATION_FORM.batch_deadline <= REVIEW_ROUND.created_date,
				DATE(REVIEW_ROUND.created_date),
				DATE(APPLICATION_FORM.batch_deadline)),
			NULL),
		IF (REVIEW_COMMENT.id IS NULL
			AND APPLICATION_FORM.next_status IS NULL,
			0,
			NULL)
	FROM APPLICATION_FORM INNER JOIN REVIEW_ROUND
		ON APPLICATION_FORM.latest_review_round_id = REVIEW_ROUND.id
	INNER JOIN REVIEWER
		ON REVIEW_ROUND.id = REVIEWER.review_round_id
	LEFT JOIN REVIEW_COMMENT
		ON REVIEWER.id = REVIEW_COMMENT.reviewer_id
	WHERE APPLICATION_FORM.status = "REVIEW"
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, 
	registered_user_id, application_role_id, is_interested_in_applicant)
	SELECT APPLICATION_FORM.id, REVIEWER.registered_user_id, "REVIEWER",
		MAX(
			IF (REVIEW_COMMENT.id IS NOT NULL,
				IF (REVIEW_COMMENT.willing_to_interview = 1
					OR REVIEW_COMMENT.willing_to_work_with_applicant = 1,
					1,
					0),
				NULL))
	FROM APPLICATION_FORM INNER JOIN REVIEW_ROUND
		ON APPLICATION_FORM.id = REVIEW_ROUND.application_form_id
	INNER JOIN REVIEWER
		ON REVIEW_ROUND.id = REVIEWER.review_round_id
	LEFT JOIN REVIEW_COMMENT
		ON REVIEWER.id = REVIEW_COMMENT.reviewer_id
	WHERE APPLICATION_FORM.status NOT IN ("UNSUBMITTED", "VALIDATION")
			AND APPLICATION_FORM.withdrawn_before_submit = 0
	GROUP BY APPLICATION_FORM.id, REVIEWER.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date,
	next_required_action_id2, next_required_action_deadline2, bind_deadline_to_due_date2)
	SELECT APPLICATION_FORM.id, APPLICATION_FORM.app_administrator_id, "STATEADMINISTRATOR", NULL,
		IF (APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW.id IS NOT NULL,
				IF (INTERVIEW.stage = "SCHEDULING",
					"CONFIRM_INTERVIEW_ARRANGEMENTS",
					IF (INTERVIEW.stage = "SCHEDULED",
						"COMPLETE_INTERVIEW_STAGE",
						NULL)),
				NULL),
			IF (APPLICATION_FORM.next_status = "INTERVIEW",
				"ASSIGN_INTERVIEWERS",
				NULL)),
		IF (APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW.id IS NOT NULL,
				IF (INTERVIEW.stage IN ("SCHEDULING", "SCHEDULED"),
					DATE(APPLICATION_FORM.due_date),
					NULL),
				NULL),
			IF (APPLICATION_FORM.next_status = "INTERVIEW",
				CURRENT_DATE(),
				NULL)),	
		IF (APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW.id IS NOT NULL,
				IF (INTERVIEW.stage IN ("SCHEDULING", "SCHEDULED"),
					1,
					NULL),
				NULL),
			IF (APPLICATION_FORM.next_status = "INTERVIEW",
				0,
				NULL)),
		IF (APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW.id IS NOT NULL,
				IF (INTERVIEW.stage = "SCHEDULING",
					"COMPLETE_INTERVIEW_STAGE",
					NULL),
				NULL),
			NULL),
		IF (APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW.id IS NOT NULL,
				IF (INTERVIEW.stage = "SCHEDULING",
					DATE(APPLICATION_FORM.due_date),
					NULL),
				NULL),
			NULL),
		IF (APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW.id IS NOT NULL,
				IF (INTERVIEW.stage = "SCHEDULING",
					1,
					NULL),
				NULL),
			NULL)
	FROM APPLICATION_FORM LEFT JOIN INTERVIEW
		ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
	WHERE APPLICATION_FORM.app_administrator_id IS NOT NULL
		AND (APPLICATION_FORM.status = "INTERVIEW"
		OR APPLICATION_FORM.next_status = "INTERVIEW")
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM.id, INTERVIEWER.registered_user_id, "INTERVIEWER",
		IF (INTERVIEW_COMMENT.id IS NOT NULL,
			IF (INTERVIEW_COMMENT.willing_to_supervise = 1,
				1,
				0),
			NULL),
		IF (INTERVIEW.stage = "SCHEDULING"
			AND APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW_VOTE_COMMENT.id IS NULL,
				"PROVIDE_INTERVIEW_AVAILABILITY",
				NULL),
			IF (INTERVIEW_COMMENT.id IS NULL,
				"PROVIDE_INTERVIEW_FEEDBACK",
				NULL)),
		IF (INTERVIEW.stage = "SCHEDULING"
			AND APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW_VOTE_COMMENT.id IS NULL,
				DATE(INTERVIEW.created_date),
				NULL),
			IF (INTERVIEW_COMMENT.id IS NULL,
				INTERVIEW.due_date,
				NULL)),
		IF (INTERVIEW.stage = "SCHEDULING"
			AND APPLICATION_FORM.next_status IS NULL,
			IF (INTERVIEW_VOTE_COMMENT.id IS NULL,
				0,
				NULL),
			IF (INTERVIEW_COMMENT.id IS NULL,
				0,
				NULL))
	FROM APPLICATION_FORM INNER JOIN INTERVIEW
		ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
	INNER JOIN INTERVIEWER
		ON INTERVIEW.id = INTERVIEWER.interview_id
	LEFT JOIN INTERVIEW_COMMENT
		ON INTERVIEWER.id = INTERVIEW_COMMENT.interviewer_id
	LEFT JOIN INTERVIEW_VOTE_COMMENT
		ON INTERVIEWER.id = INTERVIEW_VOTE_COMMENT.interview_participant_id
	WHERE APPLICATION_FORM.status = "INTERVIEW"
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, 
	registered_user_id, application_role_id, is_interested_in_applicant)
	SELECT APPLICATION_FORM.id, INTERVIEWER.registered_user_id, "INTERVIEWER",
		MAX(
			IF (INTERVIEW_COMMENT.id IS NOT NULL,
				IF (INTERVIEW_COMMENT.willing_to_supervise = 1,
					1,
					0),
				NULL))
	FROM APPLICATION_FORM INNER JOIN INTERVIEW
		ON APPLICATION_FORM.id = INTERVIEW.application_form_id
	INNER JOIN INTERVIEWER
		ON INTERVIEW.id = INTERVIEWER.interview_id
	LEFT JOIN INTERVIEW_COMMENT
		ON INTERVIEWER.id = INTERVIEW_COMMENT.interviewer_id
	WHERE APPLICATION_FORM.status NOT IN ("UNSUBMITTED", "VALIDATION")
		AND APPLICATION_FORM.withdrawn_before_submit = 0
	GROUP BY APPLICATION_FORM.id, INTERVIEWER.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "SUPERVISOR",
		IF (SUPERVISION_CONFIRMATION_COMMENT.id IS NOT NULL,
			SUPERVISOR.confirmed_supervision,
			NULL),
		IF (SUPERVISION_CONFIRMATION_COMMENT.id IS NULL
			AND APPLICATION_FORM.next_status IS NULL,
			"CONFIRM_PRIMARY_SUPERVISION",
			NULL),
		IF (SUPERVISION_CONFIRMATION_COMMENT.id IS NULL
			AND APPLICATION_FORM.next_status IS NULL,
			DATE(APPROVAL_ROUND.created_date),
			NULL),
		IF (SUPERVISION_CONFIRMATION_COMMENT.id IS NULL
			AND APPLICATION_FORM.next_status IS NULL,
			0,
			NULL)
	FROM APPLICATION_FORM INNER JOIN APPROVAL_ROUND
		ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
	INNER JOIN SUPERVISOR
		ON APPROVAL_ROUND.id = SUPERVISOR.approval_round_id
	LEFT JOIN SUPERVISION_CONFIRMATION_COMMENT
		ON SUPERVISOR.id = SUPERVISION_CONFIRMATION_COMMENT.supervisor_id
	WHERE APPLICATION_FORM.status = "APPROVAL"
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, 
	registered_user_id, application_role_id, is_interested_in_applicant)
	SELECT APPLICATION_FORM.id, SUPERVISOR.registered_user_id, "SUPERVISOR",
		MAX(
			IF (SUPERVISION_CONFIRMATION_COMMENT.id IS NOT NULL,
				SUPERVISOR.confirmed_supervision,
				NULL))
	FROM APPLICATION_FORM INNER JOIN APPROVAL_ROUND
		ON APPLICATION_FORM.id = APPROVAL_ROUND.application_form_id
	INNER JOIN SUPERVISOR
		ON APPROVAL_ROUND.id = SUPERVISOR.approval_round_id
	LEFT JOIN SUPERVISION_CONFIRMATION_COMMENT
		ON SUPERVISOR.id = SUPERVISION_CONFIRMATION_COMMENT.supervisor_id
	WHERE APPLICATION_FORM.status NOT IN ("UNSUBMITTED", "VALIDATION")
		AND APPLICATION_FORM.withdrawn_before_submit = 0
	GROUP BY APPLICATION_FORM.id, SUPERVISOR.registered_user_id
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date,
	next_required_action_id2, next_required_action_deadline2, bind_deadline_to_due_date2)	
	SELECT APPLICATION_FORM.id, PROJECT.primary_supervisor_id, "PROJECTADMINISTRATOR", NULL,
			CASE
				WHEN APPLICATION_FORM.status = "VALIDATION" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_VALIDATION_STAGE",
						CASE
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "REVIEW" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_REVIEW_STAGE",
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						IF (INTERVIEW.stage = "SCHEDULING",
							"CONFIRM_INTERVIEW_ARRANGEMENTS",
							"COMPLETE_INTERVIEW_STAGE"),
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "APPROVAL" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_APPROVAL_STAGE",
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
			END,
						IF (APPLICATION_FORM.next_status IS NULL 
				OR APPLICATION_FORM.next_status IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED"),
				CASE
					WHEN APPLICATION_FORM.status = "VALIDATION" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "REVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "APPROVAL" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
				END,
				NULL),
			IF (APPLICATION_FORM.next_status IS NULL 
				OR APPLICATION_FORM.next_status IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED"),
				CASE
					WHEN APPLICATION_FORM.status = "VALIDATION" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "REVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "APPROVAL" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
				END,
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				"COMPLETE_INTERVIEW_STAGE",
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				DATE(APPLICATION_FORM.due_date),
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				1,
				NULL)			
			FROM APPLICATION_FORM INNER JOIN PROJECT
				ON APPLICATION_FORM.project_id = PROJECT.id
			LEFT JOIN REVIEW_ROUND
				ON APPLICATION_FORM.latest_review_round_id = REVIEW_ROUND.id
			LEFT JOIN INTERVIEW
				ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
			LEFT JOIN APPROVAL_ROUND
				ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
			WHERE APPLICATION_FORM.status != "UNSUBMITTED"
				AND APPLICATION_FORM.withdrawn_before_submit = 0
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, registered_user_id, application_role_id, 
	is_interested_in_applicant, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date,
	next_required_action_id2, next_required_action_deadline2, bind_deadline_to_due_date2)	
	SELECT APPLICATION_FORM.id, PROJECT.administrator_id, "PROJECTADMINISTRATOR", NULL,
			CASE
				WHEN APPLICATION_FORM.status = "VALIDATION" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_VALIDATION_STAGE",
						CASE
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "REVIEW" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_REVIEW_STAGE",
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						IF (INTERVIEW.stage = "SCHEDULING",
							"CONFIRM_INTERVIEW_ARRANGEMENTS",
							"COMPLETE_INTERVIEW_STAGE"),
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
				WHEN APPLICATION_FORM.status = "APPROVAL" THEN
					IF (APPLICATION_FORM.next_status IS NULL,
						"COMPLETE_APPROVAL_STAGE",
						CASE 
							WHEN APPLICATION_FORM.next_status = "REVIEW" THEN
								"ASSIGN_REVIEWERS"
							WHEN APPLICATION_FORM.next_status = "INTERVIEW" THEN
								"ASSIGN_INTERVIEWERS"
							WHEN APPLICATION_FORM.next_status = "APPROVAL" THEN
								"ASSIGN_SUPERVISORS"
							WHEN APPLICATION_FORM.next_status = "REJECTED" THEN
								"CONFIRM_REJECTION"
						END)
			END,
						IF (APPLICATION_FORM.next_status IS NULL 
				OR APPLICATION_FORM.next_status IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED"),
				CASE
					WHEN APPLICATION_FORM.status = "VALIDATION" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "REVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
					WHEN APPLICATION_FORM.status = "APPROVAL" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							DATE(APPLICATION_FORM.due_date),
							CURRENT_DATE())
				END,
				NULL),
			IF (APPLICATION_FORM.next_status IS NULL 
				OR APPLICATION_FORM.next_status IN ("REVIEW", "INTERVIEW", "APPROVAL", "REJECTED"),
				CASE
					WHEN APPLICATION_FORM.status = "VALIDATION" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "REVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "INTERVIEW" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
					WHEN APPLICATION_FORM.status = "APPROVAL" THEN
						IF (APPLICATION_FORM.next_status IS NULL,
							1,
							0)
				END,
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				"COMPLETE_INTERVIEW_STAGE",
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				DATE(APPLICATION_FORM.due_date),
				NULL),
			IF (APPLICATION_FORM.status = "INTERVIEW"
				AND INTERVIEW.stage = "SCHEDULING"
				AND APPLICATION_FORM.next_status IS NULL,
				1,
				NULL)			
			FROM APPLICATION_FORM INNER JOIN PROJECT
				ON APPLICATION_FORM.project_id = PROJECT.id
			LEFT JOIN REVIEW_ROUND
				ON APPLICATION_FORM.latest_review_round_id = REVIEW_ROUND.id
			LEFT JOIN INTERVIEW
				ON APPLICATION_FORM.latest_interview_id = INTERVIEW.id
			LEFT JOIN APPROVAL_ROUND
				ON APPLICATION_FORM.latest_approval_round_id = APPROVAL_ROUND.id
			WHERE APPLICATION_FORM.status != "UNSUBMITTED"
				AND APPLICATION_FORM.withdrawn_before_submit = 0
;

INSERT IGNORE INTO REGISTERED_USER (username, firstName, lastName, email, 
	accountNonExpired, accountNonLocked, credentialsNonExpired, enabled)
	SELECT PERSON.email, PERSON.firstname, PERSON.lastname, PERSON.email, 1, 1, 1, 0
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_PROGRAMME_DETAIL
		ON APPLICATION_FORM.id = APPLICATION_FORM_PROGRAMME_DETAIL.application_form_id
	INNER JOIN SUGGESTED_SUPERVISOR
		ON APPLICATION_FORM_PROGRAMME_DETAIL.id = SUGGESTED_SUPERVISOR.programme_detail_id
	INNER JOIN PERSON
		ON SUGGESTED_SUPERVISOR.id = PERSON.id
	LEFT JOIN REGISTERED_USER
		ON PERSON.email = REGISTERED_USER.username
	WHERE REGISTERED_USER.id IS NULL
		AND APPLICATION_FORM.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
;

INSERT IGNORE INTO APPLICATION_FORM_USER_ROLE (application_form_id, 
	registered_user_id, application_role_id, is_interested_in_applicant)
	SELECT APPLICATION_FORM.id, REGISTERED_USER.id, "SUGGESTEDSUPERVISOR", 1
	FROM APPLICATION_FORM INNER JOIN APPLICATION_FORM_PROGRAMME_DETAIL
		ON APPLICATION_FORM.id = APPLICATION_FORM_PROGRAMME_DETAIL.application_form_id
	INNER JOIN SUGGESTED_SUPERVISOR
		ON APPLICATION_FORM_PROGRAMME_DETAIL.id = SUGGESTED_SUPERVISOR.programme_detail_id
	INNER JOIN PERSON
		ON SUGGESTED_SUPERVISOR.id = PERSON.id
	LEFT JOIN REGISTERED_USER
		ON PERSON.email = REGISTERED_USER.email
	LEFT JOIN APPLICATION_FORM_USER_ROLE
		ON REGISTERED_USER.id = APPLICATION_FORM_USER_ROLE.registered_user_id
		AND APPLICATION_FORM.id = APPLICATION_FORM_USER_ROLE.application_form_id
		AND APPLICATION_FORM_USER_ROLE.application_role_id IN (
			"REVIEWER", "INTERVIEWER", "SUPERVISOR", "SUGGESTEDSUPERVISOR", "APPROVER",
			"STATEADMINISTRATOR", "PROJECTADMINISTRATOR")
	WHERE REGISTERED_USER.id IS NOT NULL
		AND APPLICATION_FORM_USER_ROLE.registered_user_id IS NULL
	AND APPLICATION_FORM.status IN ("VALIDATION", "REVIEW", "INTERVIEW", "APPROVAL")
;

DELETE FROM APPLICATION_FORM_USER_ROLE
WHERE (next_required_action_id IS NOT NULL
	AND next_required_action_deadline IS NULL)
	OR (next_required_action_id2 IS NOT NULL
		AND next_required_action_deadline2 IS NULL)
;

INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, action_id, deadline_timestamp, bind_deadline_to_due_date)
	SELECT id, next_required_action_id, next_required_action_deadline, bind_deadline_to_due_date
	FROM APPLICATION_FORM_USER_ROLE
	WHERE next_required_action_id IS NOT NULL
		UNION
	SELECT id, next_required_action_id2, next_required_action_deadline2, bind_deadline_to_due_date2
	FROM APPLICATION_FORM_USER_ROLE
	WHERE next_required_action_id2 IS NOT NULL
;

INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, action_id, deadline_timestamp, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM_USER_ROLE2.id, APPLICATION_FORM_ACTION_REQUIRED.action_id, 
		APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date
	FROM APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_FORM_ACTION_REQUIRED
		ON APPLICATION_FORM_USER_ROLE.id = APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id
	INNER JOIN APPLICATION_FORM_USER_ROLE AS APPLICATION_FORM_USER_ROLE2
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_USER_ROLE2.application_form_id
	INNER JOIN APPLICATION_FORM
		ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM.id
	WHERE (APPLICATION_FORM_USER_ROLE.application_role_id LIKE "%ADMINISTRATOR%"
		OR APPLICATION_FORM_USER_ROLE.application_role_id IN ("ADMITTER", "APPROVER"))
		AND APPLICATION_FORM_USER_ROLE2.application_role_id = "SUPERADMINISTRATOR"
		AND APPLICATION_FORM.withdrawn_before_submit = 0
	GROUP BY APPLICATION_FORM_USER_ROLE2.id, APPLICATION_FORM_ACTION_REQUIRED.action_id
;

INSERT IGNORE INTO APPLICATION_FORM_ACTION_REQUIRED (application_form_user_role_id, action_id, deadline_timestamp, bind_deadline_to_due_date)
	SELECT APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id, "MOVE_TO_DIFFERENT_STAGE", 
		APPLICATION_FORM_ACTION_REQUIRED.deadline_timestamp, APPLICATION_FORM_ACTION_REQUIRED.bind_deadline_to_due_date
	FROM APPLICATION_FORM_ACTION_REQUIRED
	WHERE APPLICATION_FORM_ACTION_REQUIRED.action_id IN ("ASSIGN_REVIEWERS", "ASSIGN_INTERVIEWERS", "ASSIGN_SUPERVISORS",
		"CONFIRM_APPROVAL", "CONFIRM_REJECTION")
;

ALTER TABLE APPLICATION_FORM_USER_ROLE
	DROP COLUMN next_required_action_id,
	DROP COLUMN next_required_action_deadline,
	DROP COLUMN bind_deadline_to_due_date,
	DROP COLUMN next_required_action_id2,
	DROP COLUMN next_required_action_deadline2,
	DROP COLUMN bind_deadline_to_due_date2
;

UPDATE APPLICATION_FORM_ACTION_REQUIRED INNER JOIN APPLICATION_FORM_USER_ROLE
	ON APPLICATION_FORM_ACTION_REQUIRED.application_form_user_role_id = APPLICATION_FORM_USER_ROLE.id
SET APPLICATION_FORM_ACTION_REQUIRED.raises_urgent_flag = 1,
	APPLICATION_FORM_USER_ROLE.raises_urgent_flag = 1
WHERE deadline_timestamp <= CURRENT_DATE()
;

UPDATE APPLICATION_FORM_USER_ROLE INNER JOIN APPLICATION_ROLE
	ON APPLICATION_FORM_USER_ROLE.application_role_id = APPLICATION_ROLE.id
LEFT JOIN (
	SELECT MAX(APPLICATION_FORM_UPDATE.update_timestamp) AS update_timestamp,
		APPLICATION_FORM_UPDATE.application_form_id AS application_form_id,
		APPLICATION_FORM_UPDATE.update_visibility AS update_visibility
	FROM APPLICATION_FORM_UPDATE
	GROUP BY APPLICATION_FORM_UPDATE.application_form_id,
		APPLICATION_FORM_UPDATE.update_visibility) AS LATEST_UPDATE
	ON APPLICATION_FORM_USER_ROLE.application_form_id = LATEST_UPDATE.application_form_id
	AND APPLICATION_ROLE.update_visibility >= LATEST_UPDATE.update_visibility
LEFT JOIN APPLICATION_FORM_LAST_ACCESS
	ON APPLICATION_FORM_USER_ROLE.application_form_id = APPLICATION_FORM_LAST_ACCESS.application_form_id
	AND APPLICATION_FORM_USER_ROLE.registered_user_id = APPLICATION_FORM_LAST_ACCESS.USER_id
SET APPLICATION_FORM_USER_ROLE.update_timestamp = LATEST_UPDATE.update_timestamp,
	APPLICATION_FORM_USER_ROLE.raises_update_flag = 
		IF (LATEST_UPDATE.update_timestamp > APPLICATION_FORM_LAST_ACCESS.last_access_timestamp,
			1,
			0)
;

DROP TABLE APPLICATION_FORM_UPDATE
;

DROP TABLE APPLICATION_FORM_LAST_ACCESS
;

DELETE
FROM APPLICATION_FORM_ACTION_OPTIONAL
WHERE state_id IN ("APPROVED", "REJECTED")
AND action_id = "WITHDRAW"
;
