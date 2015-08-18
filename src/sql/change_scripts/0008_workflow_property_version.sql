update application
set workflow_property_configuration_version = (
	select version
	from workflow_property_configuration
	where institution_id = 5243
	and active = 1)
where institution_id = 5243
;
