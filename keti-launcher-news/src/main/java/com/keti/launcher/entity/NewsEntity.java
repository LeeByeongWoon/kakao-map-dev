package com.keti.launcher.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;


@Data
@NoArgsConstructor
@Measurement(name = "news")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewsEntity {

    @Column(name = "article_date", timestamp = true)
    private Instant articleDate;

    @Column(name = "url", tag = true)
    private String url;
    @Column(name = "news_id", tag = true)
    private String newsId;
    @Column(name = "crawled_date", tag = true)
    private String crawledDate;
    @Column(name = "news_category", tag = true)
    private String newsCategory;
    @Column(name = "update_date", tag = true)
    private String updateDate;
    @Column(name = "press_name", tag = true)
    private String pressName;
    @Column(name = "title", tag = true)
    private String title;
    @Column(name = "original_url", tag = true)
    private String originalUrl;
    @Column(name = "content", tag = true)
    private String content;
    
    @Column(name = "good_count")
    private double goodCount;
    @Column(name = "warm_count")
    private double warmCount;
    @Column(name = "sad_count")
    private double sadCount;
    @Column(name = "angry_count")
    private double angryCount;
    @Column(name = "want_count")
    private double wantCount;
    @Column(name = "comment_count")
    private double commentCount;


    public void setArticleDate(String sdt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+09:00'");
        LocalDateTime ldt = LocalDateTime.parse(sdt, formatter).minusHours(9);
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));

        String date = zdt.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String time = zdt.format(DateTimeFormatter.ISO_LOCAL_TIME);

        Instant articleDate = Instant.parse(date + "T" + time + ".000000Z");

        this.articleDate = articleDate;
    }

}