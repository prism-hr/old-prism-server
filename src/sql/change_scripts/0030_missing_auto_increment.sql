set session foreign_key_checks = 0
;

alter table invitation
    modify column id int(10) unsigned not null auto_increment
;

set session foreign_key_checks = 1
;
