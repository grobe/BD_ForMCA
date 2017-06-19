# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table bd_data (
  id                            bigint auto_increment not null,
  collection_id                 bigint,
  title                         varchar(255),
  number                        varchar(255),
  price                         double,
  creation_date                 datetime(6),
  scenario                      varchar(255),
  designer                      varchar(255),
  image_base64                  varchar(20000),
  isbn                          varchar(255),
  constraint pk_bd_data primary key (id)
);

create table collection_bd (
  id                            bigint auto_increment not null,
  title                         varchar(255),
  editor                        varchar(255),
  completed                     tinyint(1) default 0,
  constraint pk_collection_bd primary key (id)
);

create table scraper_bot (
  id                            bigint auto_increment not null,
  collection_id                 bigint,
  url                           varchar(255),
  store                         varchar(255),
  constraint pk_scraper_bot primary key (id)
);

create table scraper_results (
  id                            bigint auto_increment not null,
  collection_id                 bigint,
  title                         varchar(255),
  number                        varchar(255),
  price                         double,
  creation_date                 datetime(6),
  scenario                      varchar(255),
  designer                      varchar(255),
  image_base64                  varchar(20000),
  availability                  varchar(255),
  constraint pk_scraper_results primary key (id)
);

create table test (
  id                            bigint auto_increment not null,
  data                          varchar(255),
  done                          tinyint(1) default 0,
  due_date                      datetime(6),
  constraint pk_test primary key (id)
);

alter table bd_data add constraint fk_bd_data_collection_id foreign key (collection_id) references collection_bd (id) on delete restrict on update restrict;
create index ix_bd_data_collection_id on bd_data (collection_id);

alter table scraper_bot add constraint fk_scraper_bot_collection_id foreign key (collection_id) references collection_bd (id) on delete restrict on update restrict;
create index ix_scraper_bot_collection_id on scraper_bot (collection_id);

alter table scraper_results add constraint fk_scraper_results_collection_id foreign key (collection_id) references collection_bd (id) on delete restrict on update restrict;
create index ix_scraper_results_collection_id on scraper_results (collection_id);


# --- !Downs

alter table bd_data drop foreign key fk_bd_data_collection_id;
drop index ix_bd_data_collection_id on bd_data;

alter table scraper_bot drop foreign key fk_scraper_bot_collection_id;
drop index ix_scraper_bot_collection_id on scraper_bot;

alter table scraper_results drop foreign key fk_scraper_results_collection_id;
drop index ix_scraper_results_collection_id on scraper_results;

drop table if exists bd_data;

drop table if exists collection_bd;

drop table if exists scraper_bot;

drop table if exists scraper_results;

drop table if exists test;

