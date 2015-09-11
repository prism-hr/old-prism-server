set foreign_key_checks = 0
;

alter table state_termination
	convert to character set utf8 collate utf8_general_ci,
	engine = innodb
;

alter table user_account_external
	convert to character set utf8 collate utf8_general_ci
;

alter table department
	convert to character set utf8 collate utf8_general_ci
;

set foreign_key_checks = 1
;
