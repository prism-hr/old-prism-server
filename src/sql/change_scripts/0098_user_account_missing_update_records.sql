insert into user_account_update (user_account_id, content, created_timestamp)
	select user.user_account_id, "Submitted a new application", application.submitted_timestamp
	from application inner join user
		on application.user_id = user.id
	where application.submitted_timestamp is not null
;

insert into user_account_update (user_account_id, content, created_timestamp)
	select user.user_account_id, "Received a new rating", comment.submitted_timestamp
	from comment inner join application
		on comment.application_id = application.id
	inner join user
		on application.user_id = user.id
	left join comment_competence
		on comment.id = comment_competence.comment_id
	where comment.application_rating is not null || comment_competence.id is not null
;

update user_account inner join (
	select user_account_id as user_account_id,
		max(created_timestamp) as updated_timestamp
	from user_account_update
	group by user_account_id) as latest_user_account_update
set user_account.updated_timestamp = latest_user_account_update.updated_timestamp
;

update user_account_update
set sequence_identifier = concat(unix_timestamp(created_timestamp), "000", lpad(id, 10, "0"))
;

update user_account
set sequence_identifier = concat(unix_timestamp(updated_timestamp), "000", lpad(id, 10, "0"))
;
