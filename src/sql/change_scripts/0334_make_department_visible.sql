insert ignore into resource_condition(department_id, action_condition, internal_mode, external_mode)
	select id, "ACCEPT_PROJECT", true, true
	from department
;

update advert
set system_id = 1
;

update advert
set opportunity_category = "STUDY"
where opportunity_category is null
;
