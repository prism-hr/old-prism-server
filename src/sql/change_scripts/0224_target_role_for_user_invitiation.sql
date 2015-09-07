alter table user_role
	add column target_role_id varchar(50) after role_id,
	add index (target_role_id),
	add foreign key (target_role_id) references role (id)
;
