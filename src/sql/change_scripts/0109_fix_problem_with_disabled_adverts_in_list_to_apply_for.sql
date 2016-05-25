update project inner join advert
	on project.advert_id = advert.id
set advert.published = 0
where project.state_id = "PROJECT_DISABLED_COMPLETED"
;
