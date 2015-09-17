alter table imported_institution_subject_area
	add column concentration_factor int(10) unsigned not null default 5 after imported_subject_area_id,
	add column proliferation_factor decimal(20, 10) unsigned not null default 0.05 after concentration_factor,
	add index (imported_subject_area_id, concentration_factor, proliferation_factor)
;

alter table imported_institution_subject_area
	modify column concentration_factor int(10) unsigned not null,
	modify column proliferation_factor decimal(20, 10) unsigned not null
;

alter table imported_institution_subject_area
	drop index imported_institution_id,
	add unique index (imported_institution_id, imported_subject_area_id, concentration_factor, proliferation_factor)
;

alter table imported_institution_subject_area
	add column enabled int(1) unsigned not null default 1,
	add index (imported_subject_area_id, enabled)
;

alter table imported_institution_subject_area
	modify column enabled int(1) unsigned not null
;
