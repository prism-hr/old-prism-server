alter table application
	add column offered_manager_id int(10) unsigned after completion_date,
	add column offered_position_name varchar(255) after offered_manager_id,
	add column offered_position_description text after offered_position_name,
	change column confirmed_start_date offered_start_date date,
	add column offered_appointment_conditions text after offered_start_date,
	add index (offered_manager_id),
	add foreign key (offered_manager_id) references user (id)
;

alter table application
	drop index offered_manager_id,
	drop foreign key application_ibfk_9,
	drop column offered_manager_id
;

create table application_assigned_user (
	id int(10) unsigned not null,
	application_id int(10) unsigned not null,
	user_id int(10) unsigned not null,
	role_id varchar(50) not null,
	primary key (id),
	unique index (application_id, user_id, role_id),
	index (user_id),
	index (role_id),
	foreign key (application_id) references application (id),
	foreign key (user_id) references user (id),
	foreign key (role_id) references role (id))
collate = utf8_general_ci
engine = innodb
;

alter table application_assigned_user
	drop index application_id,
	drop index role_id,
	drop foreign key application_assigned_user_ibfk_3,
	drop column role_id,
	add unique index (application_id, user_id)
;

rename table application_assigned_user to application_hiring_manager
;

update application inner join (	
	select comment.application_id as application_id,
		comment.application_position_name as position_name,
		comment.application_position_description as position_description,
		comment.application_position_provisional_start_date as position_start_date,
		comment.application_appointment_conditions as position_appointment_conditions
	from comment inner join (
		select max(id) as id
		from comment
		where action_id in ("APPLICATION_CONFIRM_OFFER", "APPLICATION_REVISE_OFFER")
		group by application_id) as offer_comment
		on comment.id = offer_comment.id) as offer_comment_data
	on application.id = offer_comment_data.application_id
set application.offered_position_name = offer_comment_data.position_name,
	application.offered_position_description = offer_comment_data.position_description,
	application.offered_start_date = offer_comment_data.position_start_date,
	application.offered_appointment_conditions = offer_comment_data.position_appointment_conditions
;

alter table application_hiring_manager
	modify column id int(10) unsigned not null auto_increment
;

insert into application_hiring_manager (application_id, user_id)
	select comment.application_id as application_id,
		comment_assigned_user.user_id as user_id
	from comment inner join (
		select max(id) as id
		from comment
		where action_id in ("APPLICATION_CONFIRM_OFFER", "APPLICATION_REVISE_OFFER")
		group by application_id) as offer_comment
		on comment.id = offer_comment.id
	inner join comment_assigned_user
		on comment.id = comment_assigned_user.comment_id
	where comment_assigned_user.role_id = "APPLICATION_HIRING_MANAGER"
		and comment_assigned_user.role_transition_type = "CREATE"
;
