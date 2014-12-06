UPDATE COMMENT
SET content = NULL
WHERE application_rejection_reason_id IS NOT NULL
	OR application_rejection_reason_system IS NOT NULL
;
