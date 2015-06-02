update comment
set content = null
where application_rejection_reason_id is not null
	or application_rejection_reason_system is not null
;
