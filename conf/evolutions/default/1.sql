# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table collection_bd (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  editor                        varchar(255),
  completed                     tinyint(1) default 0,
  constraint pk_collection_bd primary key (id)
);

create table crowler_bot (
  id                            bigint auto_increment not null,
  collection_id                 bigint,
  url                           varchar(255),
  store                         varchar(255),
  constraint pk_crowler_bot primary key (id)
);

create table crowler_results (
  id                            bigint auto_increment not null,
  collection_id                 bigint,
  title                         varchar(255),
  number                        integer,
  price                         double,
  creation_date                 datetime(6),
  scenario                      varchar(255),
  designer                      varchar(255),
  availability                  varchar(255),
  constraint pk_crowler_results primary key (id)
);

create table test (
  id                            bigint auto_increment not null,
  data                          varchar(255),
  done                          tinyint(1) default 0,
  due_date                      datetime(6),
  constraint pk_test primary key (id)
);

alter table crowler_bot add constraint fk_crowler_bot_collection_id foreign key (collection_id) references collection_bd (id) on delete restrict on update restrict;
create index ix_crowler_bot_collection_id on crowler_bot (collection_id);

alter table crowler_results add constraint fk_crowler_results_collection_id foreign key (collection_id) references collection_bd (id) on delete restrict on update restrict;
create index ix_crowler_results_collection_id on crowler_results (collection_id);


# --- !Downs

alter table crowler_bot drop foreign key fk_crowler_bot_collection_id;
drop index ix_crowler_bot_collection_id on crowler_bot;

alter table crowler_results drop foreign key fk_crowler_results_collection_id;
drop index ix_crowler_results_collection_id on crowler_results;

drop table if exists collection_bd;

drop table if exists crowler_bot;

drop table if exists crowler_results;

drop table if exists test;

