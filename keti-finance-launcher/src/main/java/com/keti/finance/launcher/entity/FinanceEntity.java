package com.keti.finance.launcher.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;


@Data
@NoArgsConstructor
@Measurement(name = "finance")
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
    
    @Column(name = "open_value")
    private double openValue;
    @Column(name = "high_value")
    private double highValue;
    @Column(name = "low_value")
    private double lowValue;
    @Column(name = "close_value")
    private double closeValue;
    @Column(name = "adj_close_value")
    private double adjCloseValue;
    @Column(name = "volume_value")
    private double volumeValue;

}
