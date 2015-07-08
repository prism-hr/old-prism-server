delete
from imported_entity_feed
where location like "xml/defaultEntities%"
	and !(institution_id = 5243 
		and imported_entity_type in("INSTITUTION", "STUDY_OPTION"))
;

update application_personal_detail inner join application
	on application_personal_detail.id = application.application_personal_detail_id
inner join imported_age_range_mapping
	on application_personal_detail.age_range_id = imported_age_range_mapping.id
set application_personal_detail.age_range_id = imported_age_range_mapping.imported_age_range_id
;

alter table application_personal_detail
	add foreign key (age_range_id) references imported_age_range (id)
;

delete 
from imported_age_range_mapping
;

alter table imported_age_range_mapping
	drop index id,
	drop index institution_id,
	drop index institution_id_2,
	add index (institution_id, enabled)
;

alter table address
	drop foreign key address_ibfk_1
;

update address inner join imported_entity_mapping
	on address.domicile_id = imported_entity_mapping.id
set domicile_id = imported_entity_mapping.imported_entity_id
;

alter table address
	add foreign key (domicile_id) references imported_entity (id)
;

alter table application_funding
	drop foreign key application_funding_ibfk_2
;

update application_funding inner join imported_entity_mapping
	on application_funding.funding_source_id = imported_entity_mapping.id
set funding_source_id = imported_entity_mapping.imported_entity_id
;

alter table application_funding
	add foreign key (funding_source_id) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_10
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.gender_id = imported_entity_mapping.id
set gender_id = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (gender_id) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_3
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.country_id = imported_entity_mapping.id
set country_id = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (country_id) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_4
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.disability_id = imported_entity_mapping.id
set disability_id = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (disability_id) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_5
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.ethnicity_id = imported_entity_mapping.id
set ethnicity_id = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (ethnicity_id) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_6
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.domicile_id = imported_entity_mapping.id
set domicile_id = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (domicile_id) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_7
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.nationality_id1 = imported_entity_mapping.id
set nationality_id1 = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (nationality_id1) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_8
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.nationality_id2 = imported_entity_mapping.id
set nationality_id2 = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (nationality_id2) references imported_entity (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_9
;

update application_personal_detail inner join imported_entity_mapping
	on application_personal_detail.title_id = imported_entity_mapping.id
set title_id = imported_entity_mapping.imported_entity_id
;

alter table application_personal_detail
	add foreign key (title_id) references imported_entity (id)
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_2
;

update application_program_detail inner join imported_entity_mapping
	on application_program_detail.referral_source_id = imported_entity_mapping.id
set referral_source_id = imported_entity_mapping.imported_entity_id
;

alter table application_program_detail
	add foreign key (referral_source_id) references imported_entity (id)
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_3
;

update application_program_detail inner join imported_entity_mapping
	on application_program_detail.study_option_id = imported_entity_mapping.id
set study_option_id = imported_entity_mapping.imported_entity_id
;

alter table application_program_detail
	add foreign key (study_option_id) references imported_entity (id)
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_4
;

update application_program_detail inner join imported_entity_mapping
	on application_program_detail.opportunity_type_id = imported_entity_mapping.id
set opportunity_type_id = imported_entity_mapping.imported_entity_id
;

alter table application_program_detail
	add foreign key (opportunity_type_id) references imported_entity (id)
;

alter table comment
	drop foreign key comment_ibfk_14
;

update comment inner join imported_entity_mapping
	on comment.application_rejection_reason_id = imported_entity_mapping.id
set application_rejection_reason_id = imported_entity_mapping.imported_entity_id
;

alter table comment
	add foreign key (application_rejection_reason_id) references imported_entity (id)
;

alter table program
	drop foreign key program_ibfk_10
;

update program inner join imported_entity_mapping
	on program.opportunity_type_id = imported_entity_mapping.id
set opportunity_type_id = imported_entity_mapping.imported_entity_id
;

alter table program
	add foreign key (opportunity_type_id) references imported_entity (id)
;

alter table project
	drop foreign key project_ibfk_9
;

update project inner join imported_entity_mapping
	on project.opportunity_type_id = imported_entity_mapping.id
set opportunity_type_id = imported_entity_mapping.imported_entity_id
;

alter table project
	add foreign key (opportunity_type_id) references imported_entity (id)
;

alter table resource_study_option
	drop foreign key resource_study_option_ibfk_2
;

update resource_study_option inner join imported_entity_mapping
	on resource_study_option.study_option_id = imported_entity_mapping.id
set study_option_id = imported_entity_mapping.imported_entity_id
;

alter table resource_study_option
	add foreign key (study_option_id) references imported_entity (id)
;

alter table imported_entity_feed
	add column map_for_export int(1) unsigned not null default 0 after location
;

alter table imported_entity_feed
	modify column map_for_export int(1) unsigned not null
;

update imported_entity_feed
set map_for_export = 1
where institution_id = 5243
;

delete imported_entity_mapping.*
from imported_entity_mapping inner join imported_entity
	on imported_entity_mapping.imported_entity_id = imported_entity.id
where imported_entity_mapping.institution_id != 5243
	and !(imported_entity_mapping.institution_id = 6856 
		and imported_entity.imported_entity_type in ("REFERRAL_SOURCE", "FUNDING_SOURCE",
			"REJECTION_REASON", "TITLE"))
;

alter table imported_entity_mapping
	drop index institution_id,
	drop index id,
	drop column imported_entity_type,
	add index (institution_id, enabled)
;

alter table imported_institution_subject_area
	drop foreign key imported_institution_subject_area_ibfk_1
;

update imported_institution_subject_area inner join imported_entity_mapping
	on imported_institution_subject_area.imported_institution_id = imported_entity_mapping.id
set imported_institution_id = imported_entity_mapping.imported_entity_id
;

alter table imported_institution_subject_area
	add foreign key (imported_institution_id) references imported_institution (id)
;

delete
from imported_institution_mapping
where institution_id != 5243
;

delete
from imported_institution_mapping
where code is null
	or code like "PRISM%"
	or code like "CUST%"
;

alter table imported_institution
	drop index imported_domicile_id,
	change column imported_domicile_id domicile_id int(10) unsigned not null,
	add index (domicile_id),
	add column ucas_id varchar(10),
	add column facebook_id varchar(20),
	add unique index (ucas_id),
	add unique index (facebook_id),
	drop column custom
;

alter table imported_institution_mapping
	drop index institution_id,
	drop index institution_id_2,
	add index (institution_id, enabled),
	drop index id
;

alter table imported_language_qualification_type_mapping
	drop index institution_id,
	drop index institution_id_2,
	add index (institution_id, enabled),
	drop index id
;

update application_language_qualification inner join imported_language_qualification_type_mapping
	on application_language_qualification.language_qualification_type_id = imported_language_qualification_type_mapping.id
set application_language_qualification.language_qualification_type_id = imported_language_qualification_type_mapping.imported_language_qualification_type_id
;

alter table imported_language_qualification_type_mapping
	add foreign key (imported_language_qualification_type_id) references imported_language_qualification_type (id)
;

delete
from imported_language_qualification_type_mapping
where institution_id != 5243
;

delete
from application_qualification
where program_id is null
;

update application_qualification inner join imported_program_mapping
	on application_qualification.program_id = imported_program_mapping.id
set application_qualification.program_id = imported_program_mapping.imported_program_id
;

alter table application_qualification
	add foreign key (program_id)  references imported_program (id)
;

alter table imported_program
	drop index imported_institution_id_2,
	drop column custom
;

alter table imported_program_mapping
	drop index institution_id,
	drop index institution_id_2,
	add index (institution_id, enabled)
;
