create table user_account_external (
	id int(10) unsigned not null auto_increment,
	user_account_id int(10) unsigned not null,
	external_account_type varchar(50) not null,
	external_account_identifier varchar(50) not null,
	primary key (id),
	index (user_account_id),
	unique index (user_account_id, external_account_type),
	unique index (external_account_type, external_account_identifier),
	foreign key (user_account_id) references user_account (id)) 
engine = innodb
;

alter table user_account
	add column user_account_external_id int(10) unsigned after password,
	add unique index (user_account_external_id),
	add foreign key (user_account_external_id) references user_account_external (id)
;
