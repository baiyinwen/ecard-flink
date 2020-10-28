package com.ecard.bigdata.customFunction;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/10/15 14:41
 * @Version 1.0
 **/
public class BitMapDistinct implements AggregateFunction<Long, Roaring64NavigableMap, Long> {

    @Override
    public Roaring64NavigableMap createAccumulator() {
        return new Roaring64NavigableMap();
    }

    @Override
    public Roaring64NavigableMap add(Long value, Roaring64NavigableMap accumulator) {
        accumulator.add(value);
        return accumulator;
    }

    @Override
    public Long getResult(Roaring64NavigableMap accumulator) {
        return accumulator.getLongCardinality();
    }

    @Override
    public Roaring64NavigableMap merge(Roaring64NavigableMap accumulator1, Roaring64NavigableMap accumulator2) {
        return null;
    }
}
