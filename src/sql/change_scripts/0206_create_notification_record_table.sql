CREATE TABLE NOTIFICATION_RECORD (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  notification_date DATETIME NOT NULL, 	
  notification_type varchar(50),
  PRIMARY KEY (id)
)
ENGINE = InnoDB;