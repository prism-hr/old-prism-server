UPDATE APPLICATION_FORM
SET withdrawn_before_submit = 1
WHERE status = "WITHDRAWN"
	AND submitted_on_timestamp IS NULL
;
