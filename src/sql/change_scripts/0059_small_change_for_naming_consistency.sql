alter table advert
    change column payment_option pay_option varchar(15) after advert_address_id
;
