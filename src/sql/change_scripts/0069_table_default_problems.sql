set foreign_key_checks = 0
;

alter table user_feedback 
	convert to character set utf8 collate utf8_unicode_ci
;

alter table state_termination 
	convert to character set utf8 collate utf8_unicode_ci,
	engine = innodb
;

alter table user_account_external 
	convert to character set utf8 collate utf8_unicode_ci
;

alter table user_feedback
	modify column id int(10) unsigned not null auto_increment
;

set foreign_key_checks = 1
;
