create table APPLICATION_FORM_TRANSFER (
    id BIGINT NOT NULL AUTO_INCREMENT,
    application_id INTEGER UNSIGNED NOT NULL,
    transfer_begin_timeppoint TIMESTAMP ,
    transfer_end_timepoint TIMESTAMP ,
    status VARCHAR(50),
    ucl_user_id_received VARCHAR(50),
    ucl_booking_ref_number_received VARCHAR(50),
    FOREIGN KEY (application_id) REFERENCES APPLICATION_FORM(id),
    PRIMARY KEY(id)
)
ENGINE = InnoDB
;

create table APPLICATION_FORM_TRANSFER_ERROR (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transfer_id BIGINT NOT NULL,
    handling_time TIMESTAMP,
    diagnostic_info TEXT,
    problem_classification VARCHAR(50),
    error_handling_strategy VARCHAR(50),
    PRIMARY KEY(id),
    FOREIGN KEY (transfer_id) REFERENCES APPLICATION_FORM_TRANSFER(id)
)
ENGINE = InnoDB
;