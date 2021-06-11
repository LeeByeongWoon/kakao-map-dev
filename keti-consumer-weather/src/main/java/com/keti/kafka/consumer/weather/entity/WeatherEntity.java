package com.keti.kafka.consumer.weather.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;


@Data
@NoArgsConstructor
@Measurement(name = "kor-weather")
public class WeatherEntity {

    @Column(timestamp = true)
    Instant timestamp;

    @Column(name = "status_code",  tag = true)
    private String statusCode;
    @Column(name = "status_code_value",  tag = true)
    private Long statusCodeValue;


    @Column(name = "vi_code", tag = true)
    private String viCode;
    @Column(name = "vi_01_phase", tag = true)
    private String vi01Phase;
    @Column(name = "vi_02_phase", tag = true)
    private String vi02Phase;
    @Column(name = "vi_03_phase", tag = true)
    private String vi03Phase;

    @Column(name = "vi_nx", tag = true)
    private int viNx;
    @Column(name = "vi_ny", tag = true)
    private int viNy;

    @Column(name = "vi_longitude_hours", tag = true)
    private Double viLongitudeHours;
    @Column(name = "vi_longitude_minutes", tag = true)
    private Double viLongitudeMinutes;
    @Column(name = "vi_longitude_seconds", tag = true)
    private Double viLongitudeSeconds;
    @Column(name = "vi_latitude_hours", tag = true)
    private Double viLatitudeHours;
    @Column(name = "vi_latitude_minutes", tag = true)
    private Double viLatitudeMinutes;
    @Column(name = "vi_latitude_seconds", tag = true)
    private Double viLatitudeSeconds;
    @Column(name = "vi_longitude_10milliseconds", tag = true)
    private Double viLongitude10milliseconds;
    @Column(name = "vi_latitude_10milliseconds", tag = true)
    private Double viLatitude10milliseconds;
    
    @Column(name = "vi_collect_active", tag = true)
    private Boolean viCollectActive;

    @Column(name = "vi_update_date", tag = true)
    private Date viUpdateDate;


    @Column(name = "result_code",  tag = true)
    private Long resultCode;
    @Column(name = "result_msg", tag = true)
    private String resultMsg;

    @Column(name = "data_type", tag = true)
    private String dataType;
    @Column(name = "page_no", tag = true)
    private Long pageNo;
    @Column(name = "num_of_rows", tag = true)
    private Long numOfRows;
    @Column(name = "total_count", tag = true)
    private Long totalCount;
    @Column(name = "base_date", tag = true)
    private String baseDate;
    @Column(name = "base_time", tag = true)
    private String baseTime;
    @Column(name = "category", tag = true)
    private String category;
    @Column(name = "nx", tag = true)
    private Long nx;
    @Column(name = "ny", tag = true)
    private Long ny;
    
    @Column(name = "obsr_value")
    private Double obsrValue;

    @Builder
    public WeatherEntity(Instant timestamp, Double obsrValue) {
        this.timestamp = timestamp;
        this.obsrValue = obsrValue;
    }

}
