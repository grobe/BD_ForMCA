# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table collection (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  editor                        varchar(255),
  completed                     tinyint(1) default 0,
  constraint pk_collection primary key (id)
);

create table crowler_results (
  id                            bigint auto_increment not null,
  collection_id                 bigint,
  title                         varchar(255),
  number                        integer,
  constraint pk_crowler_results primary key (id)
);

create table test (
  id                            bigint auto_increment not null,
  data                          varchar(255),
  done                          tinyint(1) default 0,
  due_date                      datetime(6),
  constraint pk_test primary key (id)
);

alter table crowler_results add constraint fk_crowler_results_collection_id foreign key (collection_id) references collection (id) on delete restrict on update restrict;
create index ix_crowler_results_collection_id on crowler_results (collection_id);


# --- !Downs

alter table crowler_results drop foreign key fk_crowler_results_collection_id;
drop index ix_crowler_results_collection_id on crowler_results;

drop table if exists collection;

drop table if exists crowler_results;

drop table if exists test;

