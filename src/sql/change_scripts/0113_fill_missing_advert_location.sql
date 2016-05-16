insert into advert_location (advert_id, location_advert_id)
	select advert.id, department.advert_id
	from advert left join advert_location
		on advert.id = advert_location.advert_id
	inner join department
		on advert.department_id = department.id
	where advert_location.id is null
		and advert.scope_id in ("PROJECT", "PROGRAM")
;

insert into advert_location (advert_id, location_advert_id)
	select advert.id, institution.advert_id
	from advert left join advert_location
		on advert.id = advert_location.advert_id
	inner join institution
		on advert.institution_id = institution.id
	where advert_location.id is null
		and advert.scope_id in ("PROJECT", "PROGRAM")
;
