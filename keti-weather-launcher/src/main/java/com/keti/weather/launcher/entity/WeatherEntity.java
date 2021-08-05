package com.keti.weather.launcher.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;


@Data
@NoArgsConstructor
@Measurement(name = "weather")
public class WeatherEntity {

    @Column(timestamp = true)
    private Instant timestamp;

    @Column(name = "status_code", tag = true)
    private String statusCode;
    @Column(name = "result_code", tag = true)
    private String resultCode;


    @Column(name = "vi_code", tag = true)
    private String viCode;
    @Column(name = "vi_target_id", tag = true)
    private String viTargetId;
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

    @Column(name = "vi_update_date", tag = true)
    private Date viUpdateDate;


    @Column(name = "base_date", tag = true)
    private String baseDate;
    @Column(name = "base_time", tag = true)
    private String baseTime;
    
    @Column(name = "T1H_value")
    private Double t1hValue;
    @Column(name = "RN1_value")
    private Double rn1Value;
    @Column(name = "UUU_value")
    private Double uuuValue;
    @Column(name = "VVV_value")
    private Double vvvValue;
    @Column(name = "REH_value")
    private Double rehValue;
    @Column(name = "PTY_value")
    private Double ptyValue;
    @Column(name = "VEC_value")
    private Double vecValue;
    @Column(name = "WSD_value")
    private Double wsdValue;

}
