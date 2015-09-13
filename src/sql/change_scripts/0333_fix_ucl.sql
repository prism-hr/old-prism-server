delete from resource_condition
	where institution_id = 5243
;

insert into resource_condition(institution_id, action_condition, internal_mode, external_mode)
values (5243, "ACCEPT_DEPARTMENT", 0, 1),
	(5243, "ACCEPT_PROJECT", 1, 1)
;

update institution inner join advert
	on institution.advert_id = advert.id
set advert.opportunity_category = institution.opportunity_category
;
