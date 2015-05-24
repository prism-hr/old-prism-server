alter table institution
	drop column twitter_uri,
	drop column facebook_uri,
	drop column linkedin_uri
;

alter table user
	drop column twitter_uri,
	drop column linkedin_uri
;

alter table application
	add column advert_id int(10) unsigned after project_id,
	add index (advert_id, sequence_identifier),
	add foreign key (advert_id) references advert (id)
;

update application inner join project
	on application.project_id = project.id
set application.advert_id = project.advert_id
;

update application inner join program
	on application.program_id = program.id
set application.advert_id = program.advert_id
where application.advert_id is null
;

alter table application
	modify column advert_id int(10) unsigned not null
;

update application inner join (
	select application_id as application_id, 
		min(created_timestamp) as completed_timestamp
	from comment
	where action_id = "APPLICATION_PURGE"
		or (action_id = "APPLICATION_ESCALATE"
			and (transition_state_id like "APPLICATION_REJECTED%"
				and state_id not like "APPLICATION_REJECTED%")
			or (transition_state_id like "APPLICATION_WITHDRAWN%"
				and state_id not like "APPLICATION_WITHDRAWN%"))
	group by application_id) as completion
	on application.id = completion.application_id
set application.completion_date = date(completion.completed_timestamp)
where application.completion_date is null
;

update application
set completion_date = null
where id = 15529
;
