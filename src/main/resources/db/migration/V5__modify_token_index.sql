alter table user
    add constraint token_key
        unique (token);

