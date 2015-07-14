alter table scope
	modify column precedence int(2) unsigned not null
;

update scope
set precedence = precedence + 10
;
	