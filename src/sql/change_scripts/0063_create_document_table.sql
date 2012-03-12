CREATE TABLE DOCUMENT(
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
 	file_name varchar(500),
 file_content blob,
  PRIMARY KEY(id)
)
ENGINE = InnoDB;