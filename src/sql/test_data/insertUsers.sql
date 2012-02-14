Insert into REGISTERED_USER(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('bob', 'password', 1, 1, 1, 1);
Insert into REGISTERED_USER(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('fred', 'password', 1, 1, 1, 1);
Insert into REGISTERED_USER(username, password,   accountNonExpired,  accountNonLocked,  credentialsNonExpired,  enabled) values ('jane', 'password', 1, 1, 1, 1);


Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values(1, 1);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values(2, 2);
Insert into USER_ROLE_LINK (registered_user_id,application_role_id) values(3, 3);

