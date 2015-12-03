alter table advert_target_pending
    change column advert_target_list advert_target_connect_list text,
    add column advert_target_invite_list text after advert_target_connect_list
;

alter table advert_target_pending
    modify column advert_target_invite_list text after user_id
;
