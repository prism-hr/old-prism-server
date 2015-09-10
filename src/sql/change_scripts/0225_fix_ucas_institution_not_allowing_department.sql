insert ignore into resource_condition (institution_id, action_condition, partner_mode)
	select id , "ACCEPT_DEPARTMENT", true
	from institution
	where imported_institution_id is not null
;
