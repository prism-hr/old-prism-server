update resource_condition
set internal_mode = true
where action_condition = "ACCEPT_APPLICATION"
and project_id is not null
	or program_id is not null
;
