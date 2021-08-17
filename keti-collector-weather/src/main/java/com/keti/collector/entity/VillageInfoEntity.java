package com.keti.collector.entity;

import java.util.Date;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Table(name = "Village_Info")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VillageInfoEntity {

    @Id
    @Column(name = "vi_code", length = 50, insertable = false, updatable = false)
    private String viCode;

    @Column(name = "vi_target_id", length = 25, insertable = false, updatable = false)
    private int viTargetId;

    @Column(name = "vi_01_phase", length = 50)
    private String vi01Phase;
    @Column(name = "vi_02_phase", length = 50)
    private String vi02Phase;
    @Column(name = "vi_03_phase", length = 50)
    private String vi03Phase;

    @Column(name = "vi_nx", length = 50, insertable = false, updatable = false)
    private int viNx;
    @Column(name = "vi_ny", length = 50, insertable = false, updatable = false)
    private int viNy;

    @Column(name = "vi_longitude_hours", length = 50)
    private float viLongitudeHours;
    @Column(name = "vi_longitude_minutes", length = 50)
    private float viLongitudeMinutes;
    @Column(name = "vi_longitude_seconds", length = 50)
    private float viLongitudeSeconds;
    @Column(name = "vi_latitude_hours", length = 50)
    private float viLatitudeHours;
    @Column(name = "vi_latitude_minutes", length = 50)
    private float viLatitudeMinutes;
    @Column(name = "vi_latitude_seconds", length = 50)
    private float viLatitudeSeconds;
    @Column(name = "vi_longitude_10milliseconds", length = 50)
    private float viLongitude10milliseconds;
    @Column(name = "vi_latitude_10milliseconds", length = 50)
    private float viLatitude10milliseconds;

    @Column(name = "vi_update_date", length = 50)
    private Date viUpdateDate;
    
}
