alter table application
    modify column advert_id int(10) unsigned
;

alter table project
    modify column advert_id int(10) unsigned
;

alter table program
    modify column advert_id int(10) unsigned
;

alter table department
    modify column advert_id int(10) unsigned
;

alter table institution
    modify column advert_id int(10) unsigned
;

alter table system
    modify column advert_id int(10) unsigned
;

update advert
set address_id = null
where program_id is not null
    or project_id is not null
;
