CREATE TABLE public.data_analysis_sign_detail
(
    id bigserial not null primary key,
    collect_time timestamp without time zone,
    channel_no varchar(32),
    card_region_code varchar(32),
    aac002 varchar(100),
    create_time timestamp without time zone DEFAULT now(),
    status integer DEFAULT 1
)distribute by shard(id) to group default_group;
comment on table public.data_analysis_sign_detail is '签发的明细数据表';
comment on column public.data_analysis_sign_detail.id is '自增主键';
comment on column public.data_analysis_sign_detail.collect_time is '统计时间';
comment on column public.data_analysis_sign_detail.channel_no is '渠道号';
comment on column public.data_analysis_sign_detail.card_region_code is '区划码';
comment on column public.data_analysis_sign_detail.aac002 is 'MD5加密';
comment on column public.data_analysis_sign_detail.create_time is '创建时间';
comment on column public.data_analysis_sign_detail.status is '数据状态';