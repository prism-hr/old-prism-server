alter table application_referee
	add column referee_type VARCHAR(50) after application_id
;

update application_referee inner join user
	on application_referee.user_id = user.id
set application_referee.referee_type = "ACADEMIC"
where user.email LIKE "$.ac.uk"
;

update application_referee
set referee_type = "OTHER"
where referee_type is null
;

alter table application_referee
	modify column referee_type VARCHAR(50) not null
;
