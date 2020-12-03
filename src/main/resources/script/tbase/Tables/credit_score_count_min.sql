--- 每分钟信用分统计次数
drop table if exists public.credit_score_count_min;
create table public.credit_score_count_min
(
    id               bigserial not null primary key,
    collect_time     timestamp without time zone,
    event            varchar(32),
    app_key          varchar(64),
    transfer_times   integer,
    create_time      timestamp without time zone default now(),
    status           integer default 1
) distribute by shard(id) to group default_group;
comment on table public.credit_score_count_min is '信用分调用统计表';
comment on column public.credit_score_count_min.id is '自增主键';
comment on column public.credit_score_count_min.collect_time is '统计时间';
comment on column public.credit_score_count_min.event is '信用分类别（个人、单位）';
comment on column public.credit_score_count_min.app_key is '信用分机构';
comment on column public.credit_score_count_min.transfer_times is '签发数量';
comment on column public.credit_score_count_min.create_time is '创建时间';
comment on column public.credit_score_count_min.status is '数据状态';

create index idx_collect_time on public.credit_score_count_min (collect_time);
