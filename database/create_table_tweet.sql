create table tweet
(
  id serial not null
    constraint tweet_pk
    primary key,
  text text not null
);

alter table tweet owner to postgres;

