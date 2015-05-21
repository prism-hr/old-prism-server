delete
from comment_custom_response
where property_value is null
;

alter table comment_custom_response
	modify column property_value text not null
;
