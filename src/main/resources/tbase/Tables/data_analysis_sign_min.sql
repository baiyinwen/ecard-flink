--- 每分钟签发量
drop table if exists public.data_analysis_sign_min
create table public.data_analysis_sign_min(
id bigserial not null primary key,
collect_time timestamp without time zone,
channel_no varchar(32),
card_region_code varchar(16),
transfer_times integer,
create_time timestamp without time zone default now(),
status integer default 1
)
distribute by shard(id) to group default_group;
comment on table public.data_analysis_sign_min is '签发量统计表';
comment on column public.data_analysis_sign_min.id is '自增主键';
comment on column public.data_analysis_sign_min.collect_time is '统计时间';
comment on column public.data_analysis_sign_min.channel_no is '渠道号';
comment on column public.data_analysis_sign_min.card_region_code is '签发区域划分';
comment on column public.data_analysis_sign_min.transfer_times is '签发数量';
comment on column public.data_analysis_sign_min.create_time is '创建时间';
comment on column public.data_analysis_sign_min.status is '数据状态';

create index idx_collect_time on public.data_analysis_sign_min (collect_time);

