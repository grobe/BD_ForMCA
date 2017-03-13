# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table test (
  id                            bigint not null,
  name                          varchar(255),
  done                          boolean,
  due_date                      timestamp,
  constraint pk_test primary key (id)
);
create sequence test_seq;


# --- !Downs

drop table if exists test;
drop sequence if exists test_seq;

