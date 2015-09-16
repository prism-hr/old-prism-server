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
	change column imported_program_id advert_id int(10) unsigned not null,
	add index (advert_id),
	add foreign key (advert_id) references advert (id)
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

alter table application_employment_position
	drop foreign key application_form_employment_position_address_fk,
	add foreign key (address_id) references advert_address (id)
;

drop table address
;

rename table advert_address to address
;

alter table application_employment_position
	drop column employer_name,
	drop foreign key application_employment_position_ibfk_2,
	drop index application_form_employment_position_address_fk,
	drop column address_id
;

alter table application_employment_position
	add column advert_id int(10) unsigned after application_id,
	add index (advert_id),
	add foreign key (advert_id) references advert (id)
;

alter table application_referee
	drop column job_employer,
	drop foreign key application_referee_ibfk_2,
	drop index application_form_referee_address_fk,
	drop column address_id
;

alter table application_referee
	add column advert_id int(10) unsigned not null after application_id,
	add index (advert_id),
	add foreign key (advert_id) references advert (id)
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_13,
	drop index country_fk,
	drop column imported_country_id
;

rename table imported_advert_domicile to imported_domicile
;

alter table application_employment_position
	drop column position,
	drop column remit
;

alter table application_referee
	drop column referee_type,
	drop column job_title
;

drop procedure admin_delete_institution
;

drop procedure admin_insert_user
;

drop procedure admin_insert_user_role
;

drop table application_prize
;

alter table institution
	drop column staff_email_list,
	drop column staff_email_list_group,
	drop column student_email_list,
	drop column student_email_list_group
;

alter table department
	drop column staff_email_list,
	drop column staff_email_list_group,
	drop column student_email_list,
	drop column student_email_list_group
;

alter table program
	drop column staff_email_list,
	drop column staff_email_list_group,
	drop column student_email_list,
	drop column student_email_list_group
;

alter table project
	drop column staff_email_list,
	drop column staff_email_list_group,
	drop column student_email_list,
	drop column student_email_list_group
;

alter table application_personal_detail
	modify column first_language_locale int(1) unsigned after imported_domicile_id,
	drop foreign key application_personal_detail_ibfk_17,
	drop foreign key application_personal_detail_ibfk_16,
	drop index first_nationality,
	drop index domicile_fk,
	modify column imported_nationality_id varchar(10),
	add index (imported_nationality_id),
	add foreign key (imported_nationality_id) references imported_domicile (id),
	modify column imported_domicile_id varchar(10),
	add index (imported_domicile_id),
	add foreign key (imported_domicile_id) references imported_domicile (id)
;

alter table address
	change column imported_advert_domicile_id imported_domicile_id varchar(10) not null
;

drop table user_program
;

alter table application_employment_position
	drop index application_form_employment_position_fk,
	drop index advert_id,
	add unique index(application_id, advert_id),
	add index (advert_id),
	drop column start_date,
	drop column end_date,
	add column start_year int(4) unsigned not null after advert_id,
	add column start_month int(2) unsigned not null after start_year,
	add column end_year int(4) unsigned after start_month,
	add column end_month int(2) unsigned after end_year
;

alter table application_qualification
	drop index application_form_qual_fk,
	drop index advert_id,
	add unique index(application_id, advert_id),
	add index (advert_id),
	drop column start_date,
	drop column award_date,
	add column start_year int(4) unsigned not null after grade,
	add column start_month int(2) unsigned not null after start_year,
	add column award_year int(4) unsigned after start_month,
	add column award_month int(2) unsigned after award_year
;

alter table application_qualification
	modify column grade varchar(200) not null after award_month
;

create table user_address (
	id int(10) unsigned not null auto_increment,
	current_address_id int(10) unsigned not null,
	contact_address_id int(10) unsigned not null,
	primary key (id),
	index current_address_id (current_address_id),
	index contact_address_id (contact_address_id),
	foreign key (current_address_id) references address (id),
	foreign key (contact_address_id) references address (id))	
collate = utf8_general_ci,
engine = innodb
;

create table user_address (
	id int(10) unsigned not null auto_increment,
	current_address_id int(10) unsigned not null,
	contact_address_id int(10) unsigned not null,
	primary key (id),
	index current_address_id (current_address_id),
	index contact_address_id (contact_address_id),
	foreign key (current_address_id) references address (id),
	foreign key (contact_address_id) references address (id))	
collate = utf8_general_ci,
engine = innodb
;

alter table user_account
	add column personal_summary varchar(1000) after send_application_recommendation_notification,
	add column cv_id int(10) unsigned after personal_summary,
	add column linkedin_profile_url varchar(2000) after cv_id,
	add column convictions_text varchar(400) after linkedin_profile_url
;

alter table user_account
	add unique index (cv_id),
	add foreign key (cv_id) references document (id)
;

alter table user_account
	add column user_address_id int(10) unsigned after personal_summary,
	add index (user_address_id),
	add foreign key (user_address_id) references user_address (id)
;

create table user_employment_position (
	id int(10) unsigned not null auto_increment,
	user_account_id int(10) unsigned not null,
	advert_id int(10) unsigned not null,
	start_year int(4) unsigned not null,
	start_month int(2) unsigned not null,
	end_year int(4) unsigned,
	end_month int(2) unsigned,
	current int(1) unsigned not null,
	primary key (id),
	unique index (user_account_id, advert_id),
	index (advert_id),
	foreign key (user_account_id) references user_account (id),
	foreign key (advert_id) references advert (id))
collate = utf8_general_ci
	engine = innodb
;

create table user_personal_detail (
	id int(10) unsigned not null auto_increment,
	imported_title_id int(10) unsigned null default null,
	imported_gender_id int(10) unsigned null default null,
	date_of_birth date not null,
	imported_nationality_id varchar(10) null default null,
	imported_domicile_id varchar(10) null default null,
	visa_required int(1) unsigned null default null,
	phone varchar(50) not null,
	skype varchar(50) null default null,
	imported_ethnicity_id int(10) unsigned null default null,
	imported_disability_id int(10) unsigned null default null,
	primary key (id),
	index (imported_ethnicity_id),
	index (imported_disability_id),
	index (imported_title_id),
	index (imported_gender_id),
	index (imported_nationality_id),
	index (imported_domicile_id),
	foreign key (imported_gender_id) references imported_entity (id),
	foreign key (imported_disability_id) references imported_entity (id),
	foreign key (imported_ethnicity_id) references imported_entity (id),
	foreign key (imported_title_id) references imported_entity (id),
	foreign key (imported_nationality_id) references imported_domicile (id),
	foreign key (imported_domicile_id) references imported_domicile (id))
collate=utf8_general_ci
	engine=innodb
;

alter table application_personal_detail
	drop column first_language_locale,
	drop column date_of_birth
;

alter table user_account
	add column user_personal_detail_id int(10) unsigned after send_application_recommendation_notification,
	add index (user_personal_detail_id),
	add foreign key (user_personal_detail_id) references user_personal_detail (id),
	modify column personal_summary varchar(1000) after user_address_id
;

alter table application_document
	add column personal_summary varchar(1000) after id
;

create table user_document (
	id int(10) unsigned not null auto_increment,
	personal_summary varchar(1000) null default null,
	cv_id int(10) unsigned null default null,
	linkedin_profile_url varchar(2000),
	primary key (id),
	unique index cv_id (cv_id),
	foreign key (cv_id) references document (id))
collate=utf8_general_ci
	engine=innodb
;

alter table user_account
	drop column personal_summary,
	drop column cv_id,
	drop column linkedin_profile_url,
	drop index cv_id,
	drop foreign key user_account_ibfk_3,
	add column user_document_id int(10) unsigned after user_address_id,
	add unique index (user_document_id),
	add foreign key (user_document_id) references user_document (id)
;

alter table user_account
	drop index user_address_id,
	add unique index (user_address_id)
;

create table user_qualification (
	id int(10) unsigned not null auto_increment,
	user_id int(10) unsigned not null,
	advert_id int(10) unsigned not null,
	start_year int(4) unsigned not null,
	start_month int(2) unsigned not null,
	award_year int(4) unsigned null default null,
	award_month int(2) unsigned null default null,
	grade varchar(200) not null,
	completed varchar(10) not null,
	document_id int(10) unsigned null default null,
	primary key (id),
	unique index (user_id, advert_id),
	unique index (document_id),
	index (advert_id),
	foreign key (user_id) references user (id),
	foreign key (document_id) references document (id),
	foreign key (advert_id) references advert (id))
collate=utf8_general_ci
	engine=innodb
;

create table user_referee (
	id int(10) unsigned not null auto_increment,
	user_id int(10) unsigned not null,
	advert_id int(10) unsigned not null,
	skype varchar(50) null default null,
	referee_user_id int(10) unsigned null default null,
	phone varchar(50) not null,
	last_updated_timestamp datetime null default null,
	primary key (id),
	unique index (user_id, referee_user_id),
	index (referee_user_id),
	index (advert_id),
	foreign key (user_id) references application (id),
	foreign key (advert_id) references advert (id),
	foreign key (referee_user_id) references user (id))
collate = utf8_general_ci
	engine = innodb
;

alter table application_referee
	change column user_id referee_user_id int(10) unsigned
;

create table user_additional_information (
	id int(10) unsigned not null auto_increment,
	convictions_text varchar(400) null default null,
	primary key (id))
collate = utf8_general_ci
	engine = innodb
;

alter table user_account
	drop column convictions_text,
	add column user_additional_information_id int(10) unsigned after user_document_id,
	add index (user_additional_information_id),
	add foreign key (user_additional_information_id) references user_additional_information (id)
;

alter table application_qualification
	drop index application_id,
	add unique index (application_id, advert_id, start_year)
;

alter table application_employment_position
	drop index application_id,
	add unique index (application_id, advert_id, start_year)
;

alter table application_referee
	change column referee_user_id user_id int(10) unsigned
;

alter table user_referee
	drop foreign key user_referee_ibfk_1,
	drop index user_id,
	change column user_id user_account_id int(10) unsigned not null,
	add unique index (user_account_id, advert_id),
	add foreign key (user_account_id) references user_account (id)
;

alter table user_referee
	drop index referee_user_id,
	drop foreign key user_referee_ibfk_3,
	change column referee_user_id user_id int(10) unsigned,
	add index (user_id),
	add foreign key (user_id) references user (id)
;

alter table user_document
	drop column linkedin_profile_url
;

alter table user_account
	drop index user_personal_detail_id,
	add unique index (user_personal_detail_id),
	drop index user_additional_information_id,
	add unique index (user_additional_information_id)
;

alter table application
	drop column identified
;

alter table application
	drop column workflow_property_configuration_version
;

alter table project
	drop column workflow_property_configuration_version
;

alter table program
	drop column workflow_property_configuration_version
;

alter table department
	drop column workflow_property_configuration_version
;

alter table institution
	drop column workflow_property_configuration_version
;

alter table system
	drop column last_reminded_request_individual,
	drop column last_reminded_request_syndicated,
	drop column last_notified_update_syndicated,
	drop column last_notified_recommendation_syndicated
;

alter table application
	drop column last_reminded_request_individual,
	drop column last_reminded_request_syndicated,
	drop column last_notified_update_syndicated
;

alter table project
	drop column last_reminded_request_individual,
	drop column last_reminded_request_syndicated,
	drop column last_notified_update_syndicated
;

alter table program
	drop column last_reminded_request_individual,
	drop column last_reminded_request_syndicated,
	drop column last_notified_update_syndicated
;

alter table department
	drop column last_reminded_request_individual,
	drop column last_reminded_request_syndicated,
	drop column last_notified_update_syndicated
;

alter table institution
	drop column last_reminded_request_individual,
	drop column last_reminded_request_syndicated,
	drop column last_notified_update_syndicated
;

drop table resource_study_location
;

alter table resource_study_option
	drop column application_start_date,
	drop column application_close_date
;

alter table role_transition
	drop foreign key role_transition_ibfk_5,
	drop index workflow_property_definition_id,
	drop column workflow_property_definition_id
;

drop table workflow_property_configuration
;

drop table workflow_property_definition
;

drop table user_institution_identity
;

alter table user_account
	add column updated_timestamp datetime not null after enabled,
	add column sequence_identifier varchar(23) not null after updated_timestamp
;

alter table advert
	drop column fee_interval,
	drop column fee_currency_specified,
	drop column fee_currency_at_locale,
	drop column month_fee_minimum_specified,
	drop column month_fee_maximum_specified,
	drop column year_fee_minimum_specified,
	drop column year_fee_maximum_specified,
	drop column month_fee_minimum_at_locale,
	drop column month_fee_maximum_at_locale,
	drop column year_fee_minimum_at_locale,
	drop column year_fee_maximum_at_locale,
	drop column fee_converted,
	drop index fee_currency_at_locale,
	drop index month_fee_minimum_specified,
	drop index month_fee_maximum_specified,
	drop index year_fee_minimum_specified,
	drop index year_fee_maximum_specified,
	drop index month_fee_minimum_at_locale,
	drop index month_fee_maximum_at_locale,
	drop index year_fee_minimum_at_locale,
	drop index year_fee_maximum_at_locale
;

alter table comment_competence
	drop column importance
;

alter table user_account
	add column shared int(1) unsigned not null after enabled,
	add index (shared)
;
