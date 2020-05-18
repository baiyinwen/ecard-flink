--- 每分钟签发量
drop table if exists data_analysis_sign_min;
create table data_analysis_sign_min(
id integer not null primary key,
collect_time timestamp without time zone,
transfer_times integer,
create_time timestamp without time zone,
status integer default 1
)
distribute by shard(id) to group default_group;

drop sequence data_analysis_sign_min_seq;
create sequence data_analysis_sign_min_seq
start with 1 increment by 1
no minvalue no maxvalue
cache 1;

alter table data_analysis_sign_min alter column id set default nextval('data_analysis_sign_min_seq');
