package com.wanglingfeng.influxdbdemo;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public class InfluxDB2Example {
    public static void main(final String[] args) {
        //Initialize the Client
        // You can generate an API token from the "API Tokens Tab" in the UI
        String token = "HSsd31yikvlHdFVA0rpnjLJCFWB6Y962PVQcqaHWPTUdu8gS8RuMfBAhCW69l9pRRjz5f-x0_K2iQMeXZUuJuA==";
        String bucket = "test-bucket";
        String org = "wlf";
        //Write Data
        //Use InfluxDB Line Protocol to write data
        InfluxDBClient client = InfluxDBClientFactory.create("http://ericwang.ddns.net:8086", token.toCharArray());
        String data = "mem,host=host1 used_percent=23.43234543";
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeRecord(bucket, org, WritePrecision.NS, data);
        //Use a Data Point to write data
//        Point point = Point
//                .measurement("test_influxdb2")
//                .addTag("host", "host1")
//                .addField("used_percent", 22.43234543)
//                .time(Instant.now(), WritePrecision.NS);
//        writeApi.writePoint(bucket, org, point);
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            points.add(Point.measurement("test_influxdb2")
                    .addTag("host", "host1")
                    .addField("used_percent", new Random().nextDouble()*10)
                    .time(System.currentTimeMillis()+(i*1000), WritePrecision.MS));
        }
        writeApi.writePoints(bucket, org,points);
        //Use POJO and corresponding class to write data
//        Mem mem = new Mem();
//        mem.setHost("host1");
//        mem.setUsed_percent(25.43234543);
//        mem.setTime(Instant.now());
//        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, mem);
        //Execute a Flux query
        String query = "from(bucket: \"test-bucket\") |> range(start: -1h)" +
                "|> filter(fn: (r) => r._measurement == \"test_influxdb2\" )";
        List<FluxTable> tables = client.getQueryApi().query(query, org);
        for (FluxTable table : tables) {
            table.getColumns().forEach(c-> System.out.println(c.getLabel()));
            for (FluxRecord record : table.getRecords()) {
                System.out.println(record.getField()+"-->"+record.getValue());
            }
        }
        //Dispose the Client
        client.close();

    }

}
