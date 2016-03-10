alter table advert_target
    add column severed int(1) unsigned not null default 0 after partnership_state,
    add index (advert_id, severed),
    add index (target_advert_id, severed)
;

alter table advert_target
    modify column severed int(1) unsigned not null
;
