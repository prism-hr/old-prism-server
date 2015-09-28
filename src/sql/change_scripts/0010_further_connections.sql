call procedure_clear_schema()
;

create table advert_connection (
	id int(10) unsigned not null auto_increment,
	inviting_user_id int(10) unsigned not null,
	inviting_advert_id int(10) unsigned not null,
	receiving_user_id int(10) unsigned,
	receiving_advert_id int(10) unsigned,
	accepted int(1) unsigned,
	primary key (id),
	unique index (inviting_user_id, inviting_advert_id, receiving_user_id, receiving_advert_id),
	index (inviting_advert_id),
	index (receiving_user_id),
	index (receiving_advert_id),
	index (inviting_user_id, inviting_advert_id, accepted),
	index (receiving_user_id, receiving_advert_id, accepted),
	foreign key (inviting_user_id) references user (id),
	foreign key (inviting_advert_id) references advert (id),
	foreign key (receiving_user_id) references user (id),
	foreign key (receiving_advert_id) references advert (id))
collate = utf8_general_ci
	engine = innodb
;

drop table advert_target_advert
;

alter table institution
	add column imported_code varchar(50) after code,
	add index (imported_code, sequence_identifier)
;

alter table user_account
	modify column portrait_image_id int(10) unsigned after linkedin_image_id
;

alter table user_account
	modify column send_application_recommendation_notification int(1) unsigned not null after temporary_password_expiry_timestamp
;

alter table application
	change column retain shared int(1) unsigned
;

alter table project
	modify imported_code varchar(255)
;

alter table program
	modify imported_code varchar(255)
;

alter table department
	modify imported_code varchar(255)
;

alter table institution
	modify imported_code varchar(255)
;

create table opportunity_type (
	id varchar(50) not null,
	opportunity_category varchar(50) not null,
	require_endorsement int(1) unsigned not null,
	primary key (id),
	index (opportunity_category),
	index (require_endorsement))
collate = utf8_general_ci,
	engine = innodb
;

alter table opportunity_type
	add column published int(1) unsigned not null after opportunity_category,
	add index (published)
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_6,
	drop index study_option_id_2,
	change column imported_study_option_id study_option varchar(50) not null,
	add index (study_option)
;

alter table resource_study_option
	drop foreign key resource_study_option_ibfk_6,
	drop index study_option_id,
	change column imported_study_option_id study_option varchar(50) not null,
	add index (study_option)
;

alter table display_property_configuration
	change column opportunity_type opportunity_type_id varchar(50)
;

alter table notification_configuration
	change column opportunity_type opportunity_type_id varchar(50)
;

alter table state_duration_configuration
	change column opportunity_type opportunity_type_id varchar(50)
;

alter table display_property_configuration
	add index (opportunity_type_id),
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

alter table notification_configuration
	add index (opportunity_type_id),
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

alter table state_duration_configuration
	add index (opportunity_type_id),
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

alter table advert
	drop foreign key advert_ibfk_10,
	drop index imported_opportunity_type_id,
	change column imported_opportunity_type_id opportunity_type_id varchar(50),
	add index (opportunity_type_id),
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

alter table program
	drop foreign key program_ibfk_12
;

alter table program
	change column imported_opportunity_type_id opportunity_type_id varchar(50) not null,
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

alter table project
	drop foreign key project_ibfk_12
;

alter table project
	change column imported_opportunity_type_id opportunity_type_id varchar(50) not null,
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

alter table application_program_detail
	drop foreign key application_program_detail_ibfk_7
;

alter table application_program_detail
	change column imported_opportunity_type_id opportunity_type_id varchar(50),
	add foreign key (opportunity_type_id) references opportunity_type (id)
;

create table age_range (
	id varchar(50) not null,
	lower_bound int(2) unsigned,
	upper_bound int(2) unsigned,
	primary key (id),
	unique index (lower_bound),
	unique index (upper_bound))
collate = utf8_general_ci
	engine = innodb
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_11,
	change column imported_age_range_id age_range_id varchar(50),
	add foreign key (age_range_id) references age_range (id)
;

drop table imported_age_range
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_19,
	drop index title_id,
	drop column imported_title_id
;

alter table user_personal_detail
	drop foreign key user_personal_detail_ibfk_4,
	drop index imported_title_id,
	drop column imported_title_id
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_12,
	change column imported_gender_id gender_id varchar(50)
;

alter table user_personal_detail
	drop foreign key user_personal_detail_ibfk_1,
	change column imported_gender_id gender_id varchar(50)
;

alter table address
	change column imported_domicile_id domicile_id varchar(10) after address_code
;

alter table application_personal_detail
	change column imported_nationality_id nationality_id varchar(10),
	change column imported_domicile_id domicile_id varchar(10)
;

rename table imported_domicile to domicile
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_15,
	drop index user_ethnicity_fk,
	drop column imported_ethnicity_id
;

alter table application_personal_detail
	drop foreign key application_personal_detail_ibfk_14,
	drop index user_disability_fk,
	drop column imported_disability_id
;

alter table user_personal_detail
	drop foreign key user_personal_detail_ibfk_3,
	drop index imported_ethnicity_id,
	drop column imported_ethnicity_id
;

alter table user_personal_detail
	drop foreign key user_personal_detail_ibfk_2,
	drop index imported_disability_id,
	drop column imported_disability_id
;

alter table application
	drop column application_reserve_status,
	drop index application_reserve_rating
;

alter table comment
	drop column application_reserve_status,
	drop column application_rejection_reason_system,
	drop foreign key comment_ibfk_18,
	drop index application_rejection_reason_id,
	change column application_imported_rejection_reason_id application_rejection_reason varchar(50)
;

alter table resource_list_filter_constraint
	drop column value_reserve_status
;

alter table comment
	drop index application_id_2,
	drop index application_id_fk,
	add index (application_id)
;

alter table application_additional_information
	add column requirements text after id,
	change column convictions_text convictions text
;

alter table user_additional_information
	add column requirements text after id,
	change column convictions_text convictions text
;

alter table domicile
	drop column name,
	drop column enabled,
	drop index name
;

drop table imported_entity_type
;

alter table user_personal_detail
	drop foreign key user_personal_detail_ibfk_4,
	drop index imported_title_id,
	drop column imported_title_id
;

drop table imported_entity
;

alter table age_range
	modify column lower_bound int(2) unsigned
;
alter table age_range
	add column ordinal int(3) unsigned not null,
	add index (ordinal)
;

alter table domicile
	add column ordinal int(3) unsigned not null,
	add index (ordinal)
;

alter table opportunity_type
	add column ordinal int(3) unsigned not null,
	add index (ordinal)
;

alter table user_account
	modify column enabled int(1) not null after portrait_image_id
;

alter table application_referee
	modify column user_id int(10) unsigned after application_id,
	modify column skype varchar(50) after phone
;

alter table user_referee
	modify column user_id int(10) unsigned after user_account_id,
	modify column skype varchar(50) after phone
;

alter table application_referee
	modify column user_id int(10) unsigned not null
;

alter table application_qualification
	add column user_id int(10) unsigned not null after application_id,
	add index (user_id),
	add foreign key (user_id) references user (id)
;

alter table user_qualification
	add column user_id int(10) unsigned not null after user_account_id,
	add index (user_id),
	add foreign key (user_id) references user (id)
;

alter table application_employment_position
	add column user_id int(10) unsigned not null after application_id,
	add index (user_id),
	add foreign key (user_id) references user (id)
;

alter table user_employment_position
	add column user_id int(10) unsigned not null after user_account_id,
	add index (user_id),
	add foreign key (user_id) references user (id)
;

alter table project
	add column available_date date after name
;

alter table program
	add column available_date date after name
;

alter table state
	drop column hidden
;

alter table user_role
	drop foreign key user_role_ibfk_10,
	drop index target_role_id,
	drop column target_role_id
;

alter table advert_connection
	drop foreign key advert_connection_ibfk_1,
	drop foreign key advert_connection_ibfk_2,
	drop foreign key advert_connection_ibfk_3,
	drop foreign key advert_connection_ibfk_4,	
	change column inviting_advert_id advert_id int(10) unsigned not null after id,
	change column inviting_user_id advert_user_id int(10) unsigned after advert_id,
	change column receiving_advert_id target_advert_id int(10) unsigned not null,
	change column receiving_user_id target_advert_user_id int(10) unsigned after target_advert_id,
	drop index inviting_user_id,
	drop index inviting_advert_id,
	drop index receiving_user_id,
	drop index receiving_advert_id,
	drop index inviting_user_id_2,
	drop index receiving_user_id_2,
	add unique index (advert_id, advert_user_id, target_advert_id, target_advert_user_id),
	add index (advert_id, accepted),
	add index (advert_user_id, accepted),
	add index (target_advert_id, accepted),
	add index (target_advert_user_id, accepted),
	add foreign key (advert_id) references advert (id),
	add foreign key (advert_user_id) references user (id),
	add foreign key (target_advert_id) references advert (id),
	add foreign key (target_advert_user_id) references user (id),
	add column accepting_user_id int(10) unsigned not null after target_advert_user_id,
	add index (accepting_user_id, accepted),
	add foreign key (accepting_user_id) references user (id)
;

alter table comment
	add column application_applicant_accept_appointment int(1) unsigned after application_recruiter_accept_appointment
;

alter table application
	drop column confirmed_offer_type,
	drop index confirmed_offer_type
;

alter table comment
	add column application_partner_accept_appointment int(1) unsigned after application_recruiter_accept_appointment
;

drop table user_advert
;

alter table action
	add column partnership_state varchar(50) after visible_action,
	add column partnership_transition_state varchar(50) after partnership_state,
	add index (partnership_state),
	add index (partnership_transition_state)
;

alter table advert_connection
	drop column accepted,
	drop index advert_id_2,
	drop index advert_user_id,
	drop index target_advert_id,
	drop index target_advert_user_id,
	add column partnership_state varchar(50) not null,
	add index (advert_id, partnership_state),
	add index (advert_user_id, partnership_state),
	add index (target_advert_id, partnership_state),
	add index (target_advert_user_id, partnership_state)
;

alter table comment
	change column rating application_rating decimal(3, 2) unsigned
;

rename table advert_connection to advert_target
;

alter table project
	drop column opportunity_rating_count,
	drop column opportunity_rating_average,
	drop index opportunity_rating_count,
	drop index opportunity_rating_average
;

alter table program
	drop column opportunity_rating_count,
	drop column opportunity_rating_average,
	drop index opportunity_rating_count,
	drop index opportunity_rating_average
;

alter table department
	drop column opportunity_rating_count,
	drop column opportunity_rating_average,
	drop index opportunity_rating_count,
	drop index opportunity_rating_average
;

alter table institution
	drop column opportunity_rating_count,
	drop column opportunity_rating_average,
	drop index opportunity_rating_count,
	drop index opportunity_rating_average
;

alter table role
	add column verified int(1) unsigned not null default 1 after id,
	add index (verified)
;

alter table role
	modify column verified int(1) unsigned not null
;

alter table project
	add column shared int(1) unsigned not null default 1 after application_rating_average,
	add index (shared, sequence_identifier)
;

alter table project
	modify column shared int(1) unsigned not null
;

alter table program
	add column shared int(1) unsigned not null default 1 after application_rating_average,
	add index (shared, sequence_identifier)
;

alter table program
	modify column shared int(1) unsigned not null
;

alter table department
	add column shared int(1) unsigned not null default 1 after application_rating_average,
	add index (shared, sequence_identifier)
;

alter table department
	modify column shared int(1) unsigned not null
;

alter table institution
	add column shared int(1) unsigned not null default 1 after application_rating_average,
	add index (shared, sequence_identifier)
;

alter table institution
	modify column shared int(1) unsigned not null
;

alter table system
	add column shared int(1) unsigned not null default 1 after amazon_secret_key,
	add index (shared, sequence_identifier)
;

alter table system
	modify column shared int(1) unsigned not null
;

alter table application	
	modify column shared int(1) unsigned not null
;

alter table scope
	add column default_shared int(1) unsigned not null default 1 after short_code
;

alter table scope
	modify column default_shared int(1) unsigned not null
;

alter table role
	modify column verified int(1) unsigned not null after role_category
;

alter table advert
	add column globally_visible int(1) unsigned not null default 1 after last_currency_conversion_date,
	add index (globally_visible, sequence_identifier)
;

alter table advert
	modify column globally_visible int(1) unsigned not null
;
