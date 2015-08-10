alter table imported_entity_feed
	drop foreign key program_feed_institution_fk,
	add foreign key (institution_id) references institution (id)
;

alter table address
	change column domicile_id imported_domicile_id int(10) unsigned
;

alter table application_funding
	change column funding_source_id imported_funding_source_id int(10) unsigned
;

alter table application_personal_detail
	change column title_id imported_title_id int(10) unsigned
;

alter table application_personal_detail
	change column gender_id imported_gender_id int(10) unsigned
;

alter table application_personal_detail
	change column country_id imported_country_id int(10) unsigned
;

alter table application_personal_detail
	change column ethnicity_id imported_ethnicity_id int(10) unsigned
;

alter table application_personal_detail
	change column disability_id imported_disability_id int(10) unsigned
;

alter table application_personal_detail
	change column domicile_id imported_domicile_id int(10) unsigned
;

alter table application_personal_detail
	change column nationality_id1 imported_nationality_id1 int(10) unsigned
;

alter table application_personal_detail
	change column nationality_id2 imported_nationality_id2 int(10) unsigned
;

alter table application_program_detail
	change column opportunity_type_id imported_opportunity_type_id int(10) unsigned not null
;

alter table application_program_detail
	change column study_option_id imported_study_option_id int(10) unsigned
;

alter table application_program_detail
	change column referral_source_id imported_referral_source_id int(10) unsigned not null
;

alter table comment
	change column application_rejection_reason_id imported_rejection_reason_id int(10) unsigned
;

alter table imported_entity_mapping
	change column imported_entity_id imported_entity_id int(10) unsigned not null
;

alter table imported_program
	change column imported_qualification_type_id imported_qualification_type_id int(10) unsigned
;

alter table program
	change column opportunity_type_id imported_opportunity_type_id int(10) unsigned not null
;

alter table project
	change column opportunity_type_id imported_opportunity_type_id int(10) unsigned not null
;

alter table resource_study_option
	change column study_option_id imported_study_option_id int(10) unsigned not null
;

alter table imported_entity_mapping
	drop index institution_id_2
;

alter table imported_institution
	drop index domicile_id_2
;

alter table imported_age_range_mapping
	modify code varchar(50)
;

alter table imported_entity_mapping
	modify code varchar(50)
;

alter table imported_age_range
	add column enabled int(1) unsigned not null default 1,
	add index (enabled)
;

alter table imported_age_range
	modify column enabled int(1) unsigned not null
;

alter table imported_entity
	add column enabled int(1) unsigned not null default 1,
	add index (enabled)
;

alter table imported_entity
	modify column enabled int(1) unsigned not null
;

alter table imported_institution
	add column enabled int(1) unsigned not null default 1,
	add index (enabled)
;

alter table imported_institution
	modify column enabled int(1) unsigned not null
;

alter table imported_language_qualification_type
	add column enabled int(1) unsigned not null default 1,
	add index (enabled)
;

alter table imported_language_qualification_type
	modify column enabled int(1) unsigned not null
;

alter table imported_language_qualification_type_mapping
	modify column code varchar(50)
;

alter table imported_program
	add column enabled int(1) unsigned not null default 1,
	add index (enabled)
;

alter table imported_program
	modify column enabled int(1) unsigned not null
;

alter table imported_subject_area
	add column enabled int(1) unsigned not null default 1,
	add index (enabled)
;

alter table imported_subject_area
	modify column enabled int(1) unsigned not null
;

alter table imported_subject_area_mapping
	drop index institution_id,
	add index (institution_id, enabled)
;

alter table imported_institution
	change column domicile_id imported_domicile_id int(10) unsigned not null,
	add foreign key (imported_domicile_id) references imported_entity (id)
;

alter table imported_language_qualification_type_mapping
	drop foreign key imported_language_qualification_type_mapping_ibfk_2
;

alter table application_language_qualification
	change column language_qualification_type_id imported_language_qualification_type_id int(10) unsigned not null,
	add foreign key (imported_language_qualification_type_id) references imported_language_qualification_type (id)
;

delete
from imported_program_mapping
;

alter table imported_program
	add column code varchar(50) after imported_qualification_type_id,
	add unique index (code)
;

alter table imported_subject_area
	change column description name varchar(255) not null
;

alter table imported_program
	add column level varchar(255) after code,
	change column qualification type varchar(255),
	add index (imported_institution_id, level, type, name)
;

alter table comment
	change column imported_rejection_reason_id application_imported_rejection_reason_id int(10) unsigned
;

alter table imported_program
	change column type qualification varchar(255)
;

update imported_entity inner join imported_entity_mapping
	on imported_entity.id = imported_entity_mapping.imported_entity_id
set imported_entity.name = imported_entity_mapping.code, 
	imported_entity_mapping.code = imported_entity.name
where imported_entity_type in ("STUDY_OPTION", "OPPORTUNITY_TYPE")
;

delete imported_entity_mapping.*
from imported_entity_mapping inner join imported_entity
	on imported_entity_mapping.imported_entity_id = imported_entity.id
left join imported_entity_feed
	on imported_entity_mapping.institution_id = imported_entity_feed.institution_id
		and imported_entity.imported_entity_type = imported_entity_feed.imported_entity_type
where imported_entity_feed.id is null
;

alter table imported_age_range_mapping
	drop index institution_id,
	add unique index (institution_id, imported_age_range_id, code),
	add index (institution_id, enabled)
;

alter table imported_entity_mapping
	drop index institution_id,
	add unique index (institution_id, imported_entity_id, code),
	add index (institution_id, enabled)
;

alter table imported_institution_mapping
	drop index institution_id,
	add unique index (institution_id, imported_institution_id, code),
	add index (institution_id, enabled)
;

alter table imported_language_qualification_type_mapping
	drop index institution_id,
	add unique index (institution_id, imported_language_qualification_type_id, code),
	add index (institution_id, enabled)
;

alter table imported_program_mapping
	drop index institution_id,
	add unique index (institution_id, imported_program_id, code),
	add index (institution_id, enabled)
;

alter table imported_subject_area_mapping
	drop index institution_id,
	add unique index (institution_id, imported_subject_area_id, code),
	add index (institution_id, enabled)
;

drop table advert_competency
;

alter table advert_competence
	drop primary key,
	add column id int(10) unsigned not null auto_increment first,
	add primary key (id),
	add unique index (advert_id, competence_id),
	add column importance decimal(3,2) unsigned not null
;
