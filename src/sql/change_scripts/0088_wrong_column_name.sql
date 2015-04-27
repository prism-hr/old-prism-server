alter table action_custom_question_configuration
	change column program_type opportunity_type varchar(50)
;

alter table state_duration_configuration
	change column program_type opportunity_type varchar(50)
;

alter table display_property_configuration
	change column program_type opportunity_type varchar(50)
;

alter table workflow_property_configuration
	change column program_type opportunity_type varchar(50)
;

alter table notification_configuration
	change column program_type opportunity_type varchar(50)
;
