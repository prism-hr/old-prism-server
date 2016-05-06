alter table advert_target_pending
    add column advert_id int(10) unsigned not null after id,
    add index (advert_id),
    add foreign key (advert_id) references advert (id)
;
