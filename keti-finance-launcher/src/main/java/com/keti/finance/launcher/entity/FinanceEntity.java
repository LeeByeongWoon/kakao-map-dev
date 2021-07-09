package com.keti.finance.launcher.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;


@Data
@NoArgsConstructor
@Measurement(name = "kor-finance")
public class FinanceEntity {

    @Column(timestamp = true)
    private Instant timestamp;

    @Column(name = "country", tag = true)
    private String country;
    @Column(name = "exchange", tag = true)
    private String exchange;
    @Column(name = "industry", tag = true)
    private String industry;
    @Column(name = "company", tag = true)
    private String company;
    @Column(name = "ticker", tag = true)
    private String ticker;
    
    @Column(name = "open_values")
    private Double openValues;
    @Column(name = "high_values")
    private Double highValues;
    @Column(name = "low_values")
    private Double lowValues;
    @Column(name = "close_values")
    private Double closeValues;
    @Column(name = "adj_close_values")
    private Double adjCloseValues;
    @Column(name = "volume_values")
    private Double volumeValues;

}
