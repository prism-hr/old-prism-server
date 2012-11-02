drop table if exists application_form_transfer;

create table APPLICATION_FORM_TRANSFER (
    id bigint primary key,
    application_id integer not null references application_form,
    transfer_begin_timepoint timestamp,
    transfer_end_timepoint timestamp,
    status varchar(50),
    ucl_user_id_received varchar(50),
    ucl_booking_ref_number_received varchar(50)
)
ENGINE = InnoDB;

create table APPLICATION_FORM_TRANSFER_ERROR (
    id bigint primary key,
    transfer_id bigint not null references application_form_transfer,
    handling_time timestamp,
    diagnostic_info text,
    problem_classification varchar(50),
    error_handling_strategy varchar(50)
)
ENGINE = InnoDB;
