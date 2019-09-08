create table attend
(
    id bigint auto_increment,
    creator long null,
    gmt_created bigint null,
    constraint attend_pk
        primary key (id)
);