alter table message_thread
	add column search_user_id int (10) unsigned,
	add column search_advert_id int (10) unsigned,
	add column search_resource_code varchar(50),
	add index (search_user_id),
	add index (search_advert_id),
	add index (search_resource_code),
	add foreign key (search_user_id) references user (id),
	add foreign key (search_advert_id) references advert (id)
;

update message_thread inner join comment
	on message_thread.comment_id = comment.id
inner join application
	on comment.application_id = application.id
set message_thread.search_user_id = application.user_id,
	message_thread.search_advert_id = application.advert_id,
	message_thread.search_resource_code = application.code
;

update message_thread inner join comment
	on message_thread.comment_id = comment.id
inner join project
	on comment.project_id = project.id
set message_thread.search_user_id = project.user_id,
	message_thread.search_advert_id = project.advert_id,
	message_thread.search_resource_code = project.code
;

update message_thread inner join comment
	on message_thread.comment_id = comment.id
inner join program
	on comment.program_id = program.id
set message_thread.search_user_id = program.user_id,
	message_thread.search_advert_id = program.advert_id,
	message_thread.search_resource_code = program.code
;

update message_thread inner join comment
	on message_thread.comment_id = comment.id
inner join department
	on comment.department_id = department.id
set message_thread.search_user_id = department.user_id,
	message_thread.search_advert_id = department.advert_id,
	message_thread.search_resource_code = department.code
;

update message_thread inner join comment
	on message_thread.comment_id = comment.id
inner join institution
	on comment.institution_id = institution.id
set message_thread.search_user_id = institution.user_id,
	message_thread.search_advert_id = institution.advert_id,
	message_thread.search_resource_code = institution.code
;

update message_thread inner join comment
	on message_thread.comment_id = comment.id
inner join institution
	on comment.institution_id = institution.id
set message_thread.search_user_id = institution.user_id,
	message_thread.search_advert_id = institution.advert_id,
	message_thread.search_resource_code = institution.code
;

update message_thread inner join user_account
	on message_thread.user_account_id = user_account.id
inner join user
	on user_account.id = user.user_account_id
set message_thread.search_user_id = user.id
;

alter table message_thread
	modify column search_user_id int(10) unsigned not null
;
