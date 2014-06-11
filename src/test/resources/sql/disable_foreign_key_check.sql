SET FOREIGN_KEY_CHECKS=0;

INSERT INTO `user_account` (`id`, `password`, `enabled`, `application_filter_group_id`, `application_list_last_access_timestamp`) VALUES (1,'19df540b25683bb88aec535fc454ac2a',1,null,null);

INSERT INTO `user` (`id`, `first_name`, `first_name_2`, `first_name_3`, `last_name`, `email`, `activation_code`, `parent_user_id`, `user_account_id`) VALUES (1024,'UCL',NULL,NULL,'Prism','prism@ucl.ac.uk','08fcc210-5a67-11e2-bcfd-0800200c9a66',1024,1);