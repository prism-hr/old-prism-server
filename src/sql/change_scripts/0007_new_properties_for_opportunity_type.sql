alter table comment
	add column application_shared int(1) unsigned after transition_state_id,
	add column application_on_course int(1) unsigned after application_shared
;

alter table application
	add column on_course int(1) unsigned not null default 0 after shared,
	add index (on_course, sequence_identifier)
;

alter table application
	modify column on_course int(1) unsigned not null
;

set foreign_key_checks = 0
;

update opportunity_type
set id = "PLACEMENT"
where id = "ON_COURSE_PLACEMENT"
;

update advert
set opportunity_type_id = "PLACEMENT"
where opportunity_type_id = "ON_COURSE_PLACEMENT"
;

update project
set opportunity_type_id = "PLACEMENT"
where opportunity_type_id = "ON_COURSE_PLACEMENT"
;

update program
set opportunity_type_id = "PLACEMENT"
where opportunity_type_id = "ON_COURSE_PLACEMENT"
;

set foreign_key_checks = 1
;

alter table opportunity_type
	drop column require_endorsement
;
