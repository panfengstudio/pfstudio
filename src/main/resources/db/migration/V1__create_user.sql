create table user
(
	id bigint auto_increment,
	avatar_url varchar(100) null,
	name varchar(50) null,
	token int null,
	bio varchar(256) null,
	gmt_created bigint null,
	gmt_modified bigint null,
	constraint user_pk
		primary key (id)
);

