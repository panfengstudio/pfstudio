alter table user
	add constraint github_id_key
		unique (github_id);