--- 每分钟签发量
create table data_analysis_sign_min(
id integer PRIMARY KEY,
collect_time timestamp without time zone,
transfer_times integer,
create_time timestamp without time zone,
status integer)
distribute by shard(id) to group default_group;

create sequence data_analysis_id_seq
start with 1 increment by 1
no minvalue no maxvalue
cache 1;

alter table data_analysis_sign_min alter column id set default nextval('data_analysis_id_seq');
