insert ignore into imported_entity (institution_id, imported_entity_type, code, name, enabled)
	select id, "OPPORTUNITY_TYPE", "VOLUNTEERING", "Volunteering", 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
;
