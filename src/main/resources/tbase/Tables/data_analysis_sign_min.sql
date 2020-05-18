--- 每分钟签发量
drop table if exists data_analysis_sign_min;
create table data_analysis_sign_min(
id bigserial not null primary key,
collect_time timestamp without time zone,
transfer_times integer,
create_time timestamp without time zone default now(),
status integer default 1
)
distribute by shard(id) to group default_group;
