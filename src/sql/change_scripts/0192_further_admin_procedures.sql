drop procedure admin_insert_user
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
		
		update user
		set activation_code = uuid()
		where user_account_id = @existing_user_account_id;	
	else
		insert into user(first_name, last_name, full_name, email, activation_code)
		values(in_user_first_name, in_user_last_name, 
			concat(in_user_first_name, " ", in_user_last_name), in_user_email, uuid());
		
		insert into user_account(password, send_application_recommendation_notification, enabled)
		values(password_default, 0, 1);
		
		update user
		set user_account_id = last_insert_id() 
		where email = in_user_email;
	end if;  

end
;

create procedure admin_insert_user_role(in in_user_email varchar(255), in in_role_id varchar(50), in in_scope_id varchar(50), in in_resource_id INT(10) unsigned)
begin

	set @user_id = (
		select user.id
		from user inner join user_account
			on user.user_account_id = user_account.id
		where email = in_user_email
			and user_account.enabled is true);
	
	if @user_id is not null then
		set @insert_user_role = concat(
			"insert into user_role(user_id, role_id, ", lower(in_scope_id), "_id, assigned_timestamp)",
				"values(", @user_id, ", '", upper(in_role_id), "', ", in_resource_id, ", '", now(), "');");
		prepare insert_user_role from @insert_user_role;
		execute insert_user_role;
		deallocate prepare insert_user_role;
	end if; 

end
;
