drop index creator_index on attend;

create index creator_index
    on attend (creator);

alter table attend drop key creator_index;