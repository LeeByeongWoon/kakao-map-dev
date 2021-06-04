package com.keti.kafka.consumer.weather.vo;

import java.time.Instant;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import lombok.Data;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.TimeColumn;
import org.influxdb.annotation.Measurement;

@Data
@Measurement(name = "partitions", database = "keti_kafka", timeUnit = TimeUnit.MILLISECONDS)
public class PartitionsMeasurementVo {

    @TimeColumn
    @Column(name = "time")
    private Instant time;

    @Column(name = "id")
    private int id;

    @Column(name = "topic_id", tag = true)
    private String topicId;

    @Column(name = "topic_broker_id", tag = true)
    private String topicBrokerId;

    @Column(name = "topic")
    private String topic;

    @Column(name = "broker_id")
    private int brokerId;

    @Column(name = "broker_address", tag = true)
    private String brokerAddress;

    @Column(name = "partition_id")
    private int partitionId;

    @Column(name = "partition_leader")
    private int partitionLeader;

    @Column(name = "partition_replica")
    private int partitionReplica;

    @Column(name = "partition_is_leader")
    private boolean partitionIsLeader;

    @Column(name = "partition_insysc_replica")
    private boolean partitionInsyscReplica;

    @Column(name = "partition_offset_newest")
    private int partitionOffsetNewest;

    @Column(name = "partition_offset_oldest")
    private int partitionOffsetoldest;

}