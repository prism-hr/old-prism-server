alter table advert_target_pending
    drop index advert_id,
    drop column advert_id,
    drop foreign key advert_target_pending_ibfk_1
;
