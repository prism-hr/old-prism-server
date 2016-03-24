alter table advert_target
    drop index advert_id_4,
    drop index target_advert_id_3,
    add column advert_severed int(1) unsigned not null default 0 after advert_user_id,
    add index (advert_id, advert_severed),
    add column target_advert_severed int(1) unsigned not null default 0 after target_advert_user_id
;

alter table advert_target
    modify column advert_severed int(1) unsigned not null,
    modify column target_advert_severed int(1) unsigned not null
;

alter table advert_target
    drop column severed
;
 