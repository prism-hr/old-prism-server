alter table state_action_assignment
	add column partner_mode int(1) unsigned not null default 0 after role_id,
	drop index state_action_id,
	add unique index (state_action_id, role_id, partner_mode)
;

alter table state_action_assignment
	modify column partner_mode int(1) unsigned not null
;

alter table state_action_notification
	add column partner_mode int(1) unsigned not null default 0 after role_id,
	drop index state_action_id,
	add unique index (state_action_id, role_id, partner_mode)
;

alter table state_action_notification
	modify column partner_mode int(1) unsigned not null
;

alter table application
	add column institution_partner_id int(10) unsigned after institution_id,
	add index (institution_partner_id, sequence_identifier)
;

alter table institution
	add column institution_partner_id int(10) unsigned after system_id,
	add index (institution_partner_id, sequence_identifier)
;

alter table system
	add column institution_partner_id int(10) unsigned after user_id,
	add index (institution_partner_id, sequence_identifier)
;

alter table application
	add foreign key (institution_partner_id) references institution (id)
;

alter table institution
	add foreign key (institution_partner_id) references institution (id)
;

alter table system
	add foreign key (institution_partner_id) references institution (id)
;

update state_group
set ordinal = ordinal + 30
;
