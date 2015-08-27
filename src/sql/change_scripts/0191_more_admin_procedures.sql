drop procedure admin_delete_institution
;

drop procedure admin_reindex_subject_area
;

create procedure admin_delete_institution(in in_institution_id int(10) unsigned)
begin
	delete
	from advert_resource_selected
	where advert_id in (
		select id
		from advert
		where institution_id = in_institution_id);

	delete
	from advert_resource
	where advert_id in (
		select id
		from advert
		where institution_id = in_institution_id);

	delete
	from advert_subject_area
	where advert_id in (
		select id
		from advert
		where institution_id = in_institution_id);

	update advert
	set institution_id = null
	where institution_id = in_institution_id;

	delete
	from advert
	where institution_id = in_institution_id;

	delete
	from comment_transition_state
	where comment_id in (
		select id
		from comment
		where institution_id = in_institution_id);

	delete
	from comment_state
	where comment_id in (
		select id
		from comment
		where institution_id = in_institution_id);

	delete
	from comment_assigned_user
	where comment_id in (
		select id
		from comment
		where institution_id = in_institution_id);

	delete
	from comment
	where institution_id = in_institution_id;

	delete
	from resource_previous_state
	where institution_id = in_institution_id;

	delete
	from user_role
	where institution_id = in_institution_id;

	delete
	from resource_state
	where institution_id = in_institution_id;

	delete
	from resource_condition
	where institution_id = in_institution_id;

	delete
	from institution
	where id = in_institution_id;
end
;

create procedure admin_insert_user(in in_user_first_name varchar(30), in in_user_last_name varchar(30), in in_user_email varchar(255))
begin
	declare password_default varchar(50) default md5("password");

	set @existing_user_account_id = (
		select user_account_id
		from user
		where email = in_user_email);
		
	if @existing_user_account_id is not null then
		update user_account
		set enabled = true
		where id = @existing_user_account_id;
		
		update user_account
		set password = password_default,
			enabled = true
		where id = @existing_user_account_id
			and password is null;	
	else
		insert into user(first_name, last_name, full_name, email)
		values(in_user_first_name, in_user_last_name, 
			concat(in_user_first_name, in_user_last_name), in_user_email);
		
		insert into user_account(password, send_application_recommendation_notification, enabled)
		values(password_default, 0, 1);
		
		update user
		set user_account_id = last_insert_id() 
		where email = in_user_email;
	end if;  

end
;

create procedure admin_update_subject_area_index()
begin

	update imported_subject_area
	set index_score = null;

	update imported_program
	set indexed = false;

	update imported_institution
	set indexed = false;

end
;
