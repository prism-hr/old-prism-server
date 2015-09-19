alter table project
	drop column require_position_definition
;

alter table program
	drop column require_position_definition
;

alter table institution	
	drop index is_ucl_institution,
	drop column ucl_institution
;

alter table department
	modify opportunity_category varchar(255) not null
;

alter table institution
	modify opportunity_category varchar(255) not null
;

alter table advert
	modify column opportunity_category varchar(255) not null
;
