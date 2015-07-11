update project
set due_date = end_date
where state_id in ("PROJECT_APPROVED", "PROJECT_DEACTIVATED")
	and (due_date is null
	or due_date > end_date)
;
