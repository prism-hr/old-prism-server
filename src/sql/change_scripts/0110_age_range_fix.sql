insert into imported_age_range(institution_id, code, name, lower_bound, upper_bound, enabled)
	select institution.id, "ZERO_EIGHTEEN", "0-18", 0, 18, 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		union
	select institution.id, "NINETEEN_TWENTYFOUR", "19-24", 19, 24, 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		union
	select institution.id, "TWENTYFIVE_TWENTYNINE", "25-29", 25, 29, 1 
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		union
	select institution.id, "THIRTY_THIRTYNINE", "30-39", 30, 39, 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		union
	select institution.id, "FORTY_FORTYNINE", "40-49", 40, 49, 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		union
	select institution.id, "FIFTY_FIFTYNINE", "50-59", 50, 59, 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
		union
	select institution.id, "SIXTY_PLUS", "60+", 60, null, 1
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
;

alter table application_personal_detail
	add column age_range_id int(10) unsigned after date_of_birth,
	add index (age_range_id),
	add foreign key (age_range_id) references imported_age_range (id)
;

update application inner join application_personal_detail
	on application.application_personal_detail_id = application_personal_detail.id
inner join imported_age_range
	on application.institution_id = imported_age_range.institution_id
	and ((year(application.created_timestamp) - year(application_personal_detail.date_of_birth)) >= imported_age_range.lower_bound
		and (year(application.created_timestamp) - year(application_personal_detail.date_of_birth)) <= imported_age_range.upper_bound
			or imported_age_range.upper_bound is null)
set application_personal_detail.age_range_id = imported_age_range.id
;

delete application_personal_detail.*
from application_personal_detail left join application
	on application_personal_detail.id = application.application_personal_detail_id
where application.id is null
;

alter table application_personal_detail
	modify column age_range_id int(10) unsigned not null
;

insert into imported_entity_feed(institution_id, imported_entity_type, username, password, location)
	select id, "AGE_RANGE", null, null, "xml/defaultEntities/ageRange.xml"
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
;
