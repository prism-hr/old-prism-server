alter table advert_target 
	add column created_timestamp datetime after partnership_state,
	add index (created_timestamp)
;

update advert_target
set created_timestamp = if(accepted_timestamp is null, now(), accepted_timestamp)
;

alter table advert_target
	modify column created_timestamp datetime not null
;
