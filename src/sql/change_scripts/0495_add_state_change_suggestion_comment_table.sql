CREATE TABLE STATE_CHANGE_SUGGESTION_COMMENT (
  id int(10) unsigned NOT NULL,
  comment_type VARCHAR(50) NOT NULL,
  next_status varchar(30) DEFAULT NULL,
  
  CONSTRAINT state_change_suggestion_comment_fk FOREIGN KEY (id) REFERENCES COMMENT (id)
)
ENGINE = InnoDB
;
