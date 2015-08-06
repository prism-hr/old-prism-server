update imported_entity_feed
set location = replace(location, "programType", "opportunityType")
;

update notification_configuration
set subject = replace(subject, "APPLICATION_PROGRAM_TYPE", "APPLICATION_OPPORTUNITY_TYPE"),
	content = replace(content, "APPLICATION_PROGRAM_TYPE", "APPLICATION_OPPORTUNITY_TYPE")
;

update resource_state inner join application
	on resource_state.application_id = application.id
	and resource_state.primary_state is true
set resource_state.state_id = application.state_id
where resource_state.state_id != application.state_id
;
