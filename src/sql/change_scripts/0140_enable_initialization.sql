delete
from notification_configuration
where notification_definition_id like "%_SPONSOR_%"
;

delete
from notification_definition
where id like "%_SPONSOR_%"
;
