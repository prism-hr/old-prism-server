drop table application_supervisor
;

alter table application
	drop column previous_application,
	drop column study_location,
	drop column study_division,
	drop column study_area,
	drop column study_application_id,
	drop column study_start_date,
	drop index previous_application,
	drop index study_location,
	drop index study_division,
	drop index study_area,
	drop index study_application_id,
	drop index study_start_date
;

drop table application_funding
;

alter table application_personal_detail
	drop foreign key APPLICATION_FORM_PERSONAL_DETAIL_ibfk3,
	drop index application_form_language_qualification_id,
	drop column application_language_qualification_id
;

drop table application_language_qualification
;

alter table application_personal_detail
	drop foreign key APPLICATION_FORM_PERSONAL_DETAIL_ibfk4,
	drop index application_form_passport_id,
	drop column application_passport_id
;

drop table application_passport
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_18,
	drop index second_nationality,
	drop column imported_nationality_id2,
	change column imported_nationality_id1 imported_nationality_id int(10) unsigned
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_LANGUAGE"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_LANGUAGE_PROOF_OF_AWARD"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_FUNDING"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_FUNDING_PROOF_OF_AWARD"
;

delete 
from workflow_property_definition
where id = "APPLICATION_LANGUAGE"
;

delete 
from workflow_property_definition
where id = "APPLICATION_LANGUAGE_PROOF_OF_AWARD"
;

delete 
from workflow_property_definition
where id = "APPLICATION_FUNDING"
;

delete 
from workflow_property_definition
where id = "APPLICATION_FUNDING_PROOF_OF_AWARD"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_STUDY_DETAIL"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_STUDY_DETAIL"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_THEME_PRIMARY"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_THEME_PRIMARY"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_THEME_SECONDARY"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_THEME_SECONDARY"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_ASSIGN_SECONDARY_SUPERVISOR"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_ASSIGN_SECONDARY_SUPERVISOR"
;

set foreign_key_checks = 0
;

update workflow_property_configuration
set workflow_property_definition_id = "APPLICATION_ASSIGN_HIRING_MANAGER"
where workflow_property_definition_id = "APPLICATION_ASSIGN_PRIMARY_SUPERVISOR"
;

update workflow_property_definition
set id = "APPLICATION_ASSIGN_HIRING_MANAGER"
where id = "APPLICATION_ASSIGN_PRIMARY_SUPERVISOR"
;

set foreign_key_checks = 1
;

alter table application_document
	drop foreign key application_document_ibfk_2,
	drop foreign key application_document_ibfk_4,
	drop index research_statement_id,
	drop index personal_statement_id,
	drop column research_statement_id,
	drop column personal_statement_id
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_RESIDENCE"
;

delete 
from workflow_property_configuration
where workflow_property_definition_id = "APPLICATION_RESIDENCE"
;

drop table imported_entity_mapping
;

drop table imported_age_range_mapping
;

drop table imported_institution_mapping
;

drop table imported_program_mapping
;

drop table imported_advert_domicile_mapping
;

drop table imported_language_qualification_type_mapping
;

drop table imported_language_qualification_type
;

delete
from imported_entity
where imported_entity_type = "IMPORTED_FUNDING_SOURCE"
;

alter table application_program_detail
	drop index referral_source_id,
	drop foreign key application_program_detail_ibfk_5,
	drop column imported_referral_source_id
;

delete
from application_referee
;

delete
from comment_assigned_user
where comment_id in (
	select id
	from comment
	where application_id is not null)
;

delete
from comment_state
where comment_id in (
	select id
	from comment
	where application_id is not null)
;

delete
from comment_transition_state
where comment_id in (
	select id
	from comment
	where application_id is not null)
;

delete
from document
where comment_id in (
	select id
	from comment
	where application_id is not null)
;

delete
from comment_appointment_preference
where comment_id in (
	select id
	from comment
	where application_id is not null)
;

delete
from comment_appointment_timeslot
where comment_id in (
	select id
	from comment
	where application_id is not null)
;

delete
from comment
where application_id is not null
;

delete 
from resource_state
where application_id is not null
;

delete 
from resource_previous_state
where application_id is not null
;

delete from application_qualification
;

delete from application_employment_position
;

delete from application_prize
;

delete from user_role
where application_id is not null
;

delete
from user_notification
where application_id is not null
;

delete
from user_feedback
where application_id is not null
;

delete from application
;

delete from application_personal_detail
;

delete from application_program_detail
;

delete from application_address
;

delete from application_document
;

delete from application_additional_information
;

delete from address
;

drop table resource_study_option_instance
;

alter table application_qualification
	drop column qualification_language
;

drop procedure admin_update_subject_area_index
;

drop table advert_theme
;

delete
from user_program
;

delete from resource_list_filter_constraint
;

delete from resource_list_filter
;

alter table institution
	drop foreign key institution_ibfk_11,
	drop index imported_institution_id,
	drop column imported_institution_id
;

alter table application_qualification
	drop foreign key application_qualification_ibfk_3
;

alter table application_qualification
	change column imported_program_id program_id int(10) unsigned not null,
	add index (program_id),
	add foreign key (program_id) references program (id)
;

alter table application_qualification
	drop index program_id_2
;

alter table user_program
	drop foreign key user_program_ibfk_2,
	drop index imported_program_id
;

alter table user_program
	change column imported_program_id program_id int(10) unsigned not null,
	add index (program_id),
	add foreign key (program_id) references program (id)
;

drop table imported_program
;

drop table imported_institution
;

alter table comment
	drop column application_export_succeeded,
	drop column application_export_request,
	drop column application_export_exception,
	drop column application_export_reference
;

alter table application_address
	drop foreign key application_address_ibfk_1,
	drop foreign key application_address_ibfk_2,
	add foreign key (current_address_id) references advert_address (id),
	add foreign key (contact_address_id) references advert_address (id)
;

alter table application_referee
	drop foreign key application_form_referee_address_fk
;

alter table application_referee
	add foreign key (address_id) references address (id)
;

alter table application_referee
	add foreign key (address_id) references advert_address (id)
;
