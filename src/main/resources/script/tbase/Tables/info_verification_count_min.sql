--- 每分钟信息核验统计次数
--- drop table if exists public.info_verification_count_min;
create table public.info_verification_count_min
(
    id               bigserial not null primary key,
    collect_time     timestamp without time zone,
    event            varchar(64),
    app_key          varchar(128),
    transfer_times   integer,
    create_time      timestamp without time zone default now(),
    status           integer default 1
) distribute by shard(id) to group default_group;
comment on table public.info_verification_count_min is '信息核验调用统计表';
comment on column public.info_verification_count_min.id is '自增主键';
comment on column public.info_verification_count_min.collect_time is '统计时间';
comment on column public.info_verification_count_min.event is 'event类型';
comment on column public.info_verification_count_min.app_key is '渠道编码';
comment on column public.info_verification_count_min.transfer_times is '调用量';
comment on column public.info_verification_count_min.create_time is '创建时间';
comment on column public.info_verification_count_min.status is '数据状态';

create index info_verify_idx_collect_time on public.info_verification_count_min (collect_time);
