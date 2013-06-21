CREATE TABLE RESEARCH_OPPORTUNITIES_FEED (
  id INT(10) UNSIGNED NOT NULL ,
  registered_user_id INT(10) UNSIGNED NOT NULL ,
  feed_format VARCHAR(10) NOT NULL ,
  PRIMARY KEY (id) ,
  INDEX registered_user_feed_fk_idx (registered_user_id ASC) ,
  CONSTRAINT registered_user_feed_fk
    FOREIGN KEY (registered_user_id )
    REFERENCES pgadmissions.registered_user (id )
)
;

CREATE TABLE RESEARCH_OPPORTUNITIES_FEED_PROGRAM_LINK (
  feed_id INT(10) UNSIGNED NOT NULL ,
  program_id INT(10) UNSIGNED NOT NULL ,
  PRIMARY KEY (feed_id, program_id) ,
  INDEX feed_id_fk_idx (feed_id ASC) ,
  INDEX feed_program_id_fk_idx (program_id ASC) ,
  CONSTRAINT feed_id_fk
    FOREIGN KEY (feed_id )
    REFERENCES pgadmissions.research_opportunities_feed (id )
  ,
  CONSTRAINT feed_program_id_fk
    FOREIGN KEY (program_id )
    REFERENCES pgadmissions.program (id )
)
;

ALTER TABLE RESEARCH_OPPORTUNITIES_FEED ADD COLUMN title VARCHAR(100) NOT NULL  AFTER registered_user_id
;

ALTER TABLE RESEARCH_OPPORTUNITIES_FEED CHANGE COLUMN id id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT
;



