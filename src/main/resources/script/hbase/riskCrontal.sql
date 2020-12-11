------ 风控日志落库hbase表 ------
create_namespace 'RISKCONTROL'
--- hbase创建预分区表
--- create 'RISKCONTROL:RISKCONTROL_LOGS', 'data', SPLITS => ['0|','1|','2|','3|','4|','5|','6|','7|','8|','9|']

--- phoenix后台命令创建预分区表
create table if not exists RISKCONTROL.RISKCONTROL_LOGS(
"uniformOrderId" varchar primary key,
"data"."uniformOrderId" varchar,
"data"."uniformOrderCreateTime" varchar,
"data"."essCardNo" varchar,
"data"."cardType" varchar,
"data"."esscAab301" varchar,
"data"."transArea" varchar,
"data"."ownPayCh" varchar,
"data"."payGroupType" varchar,
"data"."bankCode" varchar,
"data"."ak" varchar,
"data"."ministryMerchantId" varchar,
"data"."aaz570" varchar,
"data"."serviceType" varchar,
"data"."akc264" varchar,
"data"."userName" varchar,
"data"."tradeStatus" varchar,
"data"."isHandle" varchar) column_encoded_bytes=0 split on ('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
