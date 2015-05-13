alter table institution
	add column minimum_wage decimal(10,2) unsigned after business_year_start_month
;

update institution
set minimum_wage = 6.50
;

alter table institution
modify column minimum_wage decimal(10, 2) not null
;

alter table system
	add column minimum_wage decimal(10, 2) unsigned after title
;

update system
set minimum_wage = 6.50
;

alter table system
	modify column minimum_wage decimal(10, 2) unsigned not null
;
