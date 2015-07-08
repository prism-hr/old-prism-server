alter table comment
	add column institution_partner_id int(10) unsigned after transition_state_id,
	add index (institution_partner_id),
	add foreign key (institution_partner_id) references institution (id),
	add column removed_partner int(1) unsigned after institution_partner_id,
	drop index sponsor_id,
	drop foreign key comment_ibfk_16,
	change column sponsor_id institution_sponsor_id int(10) unsigned,
	add index (institution_sponsor_id),
	add foreign key (institution_sponsor_id) references institution (id)
;
