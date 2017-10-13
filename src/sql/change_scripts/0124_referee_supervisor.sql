alter table application_referee
    add column supervisor int(1) unsigned after skype
;

update application_referee
set supervisor = 1
;

alter table application_referee
    modify column supervisor int(1) unsigned not null
;

alter table user_referee
  add column supervisor int(1) unsigned after skype
;

update user_referee
set supervisor = 1
;

alter table user_referee
  modify column supervisor int(1) unsigned not null
;
