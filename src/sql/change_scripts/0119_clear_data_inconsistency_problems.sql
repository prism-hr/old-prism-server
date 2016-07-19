update project inner join advert
	on project.advert_id = advert.id
set advert.published = 0
where project.state_id != "PROJECT_APPROVED"
;

update program inner join advert
	on program.advert_id = advert.id
set advert.published = 0
where program.state_id != "PROGRAM_APPROVED"
;

update department inner join advert
	on department.advert_id = advert.id
set advert.published = 0
where department.state_id != "DEPARTMENT_APPROVED"
;

update institution inner join advert
	on institution.advert_id = advert.id
set advert.published = 0
where institution.state_id != "INSTITUTION_APPROVED"
;

update user_account
set activity_cache = null,
	activity_cached_timestamp = null,
	activity_cached_increment = null
;
