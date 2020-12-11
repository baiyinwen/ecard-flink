--- 计费系统接口调用次数统计表
--- drop table if exists public.billing_transfer_count_min;
create table public.billing_transfer_count_min
(
    id               bigserial not null primary key,
    collect_time     timestamp without time zone,
    event            varchar(64),
    channel_no       varchar(32),
    transfer_times   integer,
    create_time      timestamp without time zone default now(),
    status           integer                     default 1
) distribute by shard(id) to group default_group;
comment on table public.billing_transfer_count_min is '计费系统接口调用次数统计表';
comment on column public.billing_transfer_count_min.id is '自增主键';
comment on column public.billing_transfer_count_min.collect_time is '统计时间';
comment on column public.billing_transfer_count_min.event is 'event值';
comment on column public.billing_transfer_count_min.channel_no is '渠道号';
comment on column public.billing_transfer_count_min.transfer_times is '签发数量';
comment on column public.billing_transfer_count_min.create_time is '创建时间';
comment on column public.billing_transfer_count_min.status is '数据状态';

create index billing_transfer_idx_collect_time on public.billing_transfer_count_min (collect_time);

--- 表追加event字段，增加event统计维度
ALTER TABLE public.billing_transfer_count_min ADD COLUMN event varchar(64);
comment on column public.billing_transfer_count_min.event is 'event值';


