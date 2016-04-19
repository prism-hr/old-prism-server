insert into advert_location (advert_id, location_advert_id)
	select advert.id, institution.advert_id
	from project inner join advert
		on project.id = advert.project_id
	inner join institution
		on advert.institution_id = institution.id
	inner join resource_state
		on project.id = resource_state.project_id
	left join advert_location
		on advert.id = advert_location.advert_id
	where resource_state.state_id = "PROJECT_APPROVED"
		and advert_location.id is null
;
