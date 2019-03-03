create table tweet
(
  id serial not null
    constraint tweet_pk
    primary key,
  hash varchar(128) not null,
  text text not null
);

alter table tweet owner to postgres;

create unique index tweet_hash_uindex
  on tweet (hash);

