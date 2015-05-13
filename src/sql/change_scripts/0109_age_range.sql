alter table application_personal_detail
	add column age_range_id int(10) after date_of_birth,
	add index (age_range_id),
	add foreign key (age_range_id) references imported_entity (id)
;

insert into imported_entity_feed(institution_id, imported_entity_type, username, password, location)
	select id, "AGE_RANGE", null, null, "xml/defaultEntities/ageRange.xml"
	from institution
	where state_id = "INSTITUTION_APPROVED_COMPLETED"
;
