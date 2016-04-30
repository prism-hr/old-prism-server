alter table state
	add column published int(1) unsigned after submitted,
	add index (published)
;

update state
set published = 1
where scope_id in ("PROJECT", "PROGRAM", "DEPARTMENT", "INSTIUTION")
	and id like "%_APPROVED"
;
