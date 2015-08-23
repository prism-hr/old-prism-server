alter table advert_resource
	add column target_advert_id int(10) unsigned after advert_id,
	add unique index (advert_id, target_advert_id),
	add index (target_advert_id),
	add foreign key (target_advert_id) references advert(id)
;

alter table advert_resource_selected
	add column target_advert_id int(10) unsigned after advert_id,
	add unique index (advert_id, target_advert_id),
	add index (target_advert_id),
	add foreign key (target_advert_id) references advert(id)
;

update advert_resource inner join institution
	on advert_resource.institution_id = institution.id
set advert_resource.target_advert_id = institution.advert_id
;

update advert_resource inner join department
	on advert_resource.department_id = department.id
set advert_resource.target_advert_id = department.advert_id
;

update advert_resource_selected inner join institution
	on advert_resource_selected.institution_id = institution.id
set advert_resource_selected.target_advert_id = institution.advert_id
;

update advert_resource_selected inner join department
	on advert_resource_selected.department_id = department.id
set advert_resource_selected.target_advert_id = department.advert_id
;

alter table advert_resource
	modify column target_advert_id int(10) unsigned not null,
	drop index advert_id,
	drop foreign key advert_resource_ibfk_2,
	drop foreign key advert_resource_ibfk_3,
	drop column institution_id,
	drop column department_id
;

alter table advert_resource_selected
	modify column target_advert_id int(10) unsigned not null,
	drop index advert_id,
	drop foreign key advert_resource_selected_ibfk_2,
	drop foreign key advert_resource_selected_ibfk_3,
	drop column institution_id,
	drop column department_id
;

alter table advert_resource
	modify column target_advert_id int(10) unsigned not null
;

alter table advert_resource_selected
	modify column target_advert_id int(10) unsigned not null
;

alter table advert_resource
	add column selected int(10) unsigned not null default 0,
	add column endorsed int(10) unsigned not null default 0,
	add index (advert_id, selected),
	add index (advert_id, endorsed)
;

alter table advert_resource
	modify column selected int(10) unsigned not null,
	modify column endorsed int(10) unsigned not null
;

insert into advert_resource(advert_id, target_advert_id, selected, endorsed)
	select advert_id, target_advert_id, true, false
	from  advert_resource_selected
	on duplicate key update selected = values(selected)
;

drop table advert_resource_selected
;

rename table advert_resource to advert_target_advert
;
