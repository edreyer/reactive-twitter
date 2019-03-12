create table tweet
(
  id bigserial not null
    constraint tweet_pk
      primary key,
  text text not null,
  retweet boolean default false not null,
  tweetid bigint not null
);

alter table tweet owner to postgres;

create unique index tweet_tweetid_uindex
  on tweet (tweetid);

