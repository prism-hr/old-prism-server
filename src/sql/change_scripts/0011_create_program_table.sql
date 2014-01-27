CREATE TABLE PROGRAM (
  id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, 
  code VARCHAR(50) NOT NULL,
  description VARCHAR(2000) NOT NULL,
  UNIQUE (code),
  PRIMARY KEY (id)
)
ENGINE = InnoDB;

--//@UNDO

DROP TABLE IF EXISTS PROGRAM;