Insert into registered_user(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('bob', 'password', 1, 1, 1, 1);
Insert into registered_user(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('fred', 'password', 1, 1, 1, 1);


Insert into user_role_link(registered_user_id,application_role_id) values(1, 1);

