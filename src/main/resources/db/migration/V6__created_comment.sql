create table comment
(
    id int auto_increment,
    content varchar(256) null,
    creator long null,
    gmt_created bigint null,
    gmt_modified bigint null,
    constraint comment_pk
        primary key (id)
);

