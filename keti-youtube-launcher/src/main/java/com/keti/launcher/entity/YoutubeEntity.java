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
@Measurement(name = "youtube")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class YoutubeEntity {

    @Column(name = "article_date", timestamp = true)
    private Instant articleDate;

    @Column(name = "url", tag = true)
    private String url;
    @Column(name = "crawled_date", tag = true)
    private String crawledDate;
    @Column(name = "super_title", tag = true)
    private String superTitle;
    @Column(name = "article_day", tag = true)
    private String articleDay;
    @Column(name = "channel_name", tag = true)
    private String channelName;
    // @Column(name = "script", tag = true)
    // private String script;
    // @Column(name = "analyed_words", tag = true)
    // private String analyedWords;

    @Column(name = "view_count")
    private String viewCount;
    @Column(name = "like_count")
    private Double likeCount;
    @Column(name = "hate_count")
    private Double hateCount;
    @Column(name = "comment_count")
    private Double commentCount;


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
