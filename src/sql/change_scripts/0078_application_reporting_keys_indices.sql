alter table application
	add column application_year varchar (10) after submitted_timestamp,
	add column application_month int(2) unsigned after application_year,
	add index (institution_id, program_id, project_id, application_year, application_month)
;

update application
	set application_month = month(submitted_timestamp)
;

update application
set application_year = concat((year(submitted_timestamp) - 1), "/", year(submitted_timestamp))
where application_month < 10
;

update application
set application_year = concat(year(submitted_timestamp), "/", (year(submitted_timestamp) + 1))
where application_month > 9
;

update application inner join (
	select comment.application_id, 
		max(date(comment.created_timestamp)) as completion_date
	from comment inner join application
		on comment.application_id = application.id
	where comment.action_id in("APPLICATION_CONFIRM_OFER_RECOMMENDATION",
		"APPLICATION_CONFIRM_REJECTION",
		"APPLICATION_WITHDRAW", 
		"APPLICATION_PURGE")
		and application.submitted_timestamp is not null
	group by comment.application_id) as application_complete_comment
	on application.id = application_complete_comment.application_id
set application.completion_date = application_complete_comment.completion_date
where application.completion_date is null
;
