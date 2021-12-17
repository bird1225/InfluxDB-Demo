package com.wanglingfeng.influxdbdemo;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Measurement(name = "mem")
@Data
public class Mem {
    @Column(tag = true)
    String host;
    @Column
    Double used_percent;
    @Column(timestamp = true)
    Instant time;
}