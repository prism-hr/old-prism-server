CREATE TABLE USER_ROLE_LINK (
  registered_user_id INTEGER UNSIGNED NOT NULL,
  application_role_id INTEGER UNSIGNED NOT NULL,
  CONSTRAINT user_role_user_fk FOREIGN KEY (registered_user_id) REFERENCES REGISTERED_USER(id),
  CONSTRAINT user_role_role_fk FOREIGN KEY (application_role_id) REFERENCES APPLICATION_ROLE(id)
)
ENGINE = InnoDB;

--//@UNDO

DROP CONSTRAINT IF EXISTS user_role_role_fk;
DROP CONSTRAINT IF EXISTS user_role_user_fk;
DROP TABLE IF EXISTS REGISTERED_USER;