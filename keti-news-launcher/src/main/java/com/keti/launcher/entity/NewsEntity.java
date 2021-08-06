package com.keti.launcher.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;


@Data
@NoArgsConstructor
@Measurement(name = "news")
public class NewsEntity {

    @Column(name = "article_date", timestamp = true)
    private Instant articleDate;

    @Column(name = "crawled_date", tag = true)
    private String crawledDate;
    @Column(name = "update_date", tag = true)
    private String updateDate;
    @Column(name = "url", tag = true)
    private String url;
    @Column(name = "news_id", tag = true)
    private String newsId;
    // @Column(name = "crawled_date", tag = true)
    // private String crawledDate;
    @Column(name = "news_category", tag = true)
    private String newsCategory;
    @Column(name = "press_name", tag = true)
    private String pressName;
    @Column(name = "title", tag = true)
    private String title;
    @Column(name = "original_url", tag = true)
    private String originalUrl;
    // @Column(name = "content", tag = true)
    // private String content;
    
    @Column(name = "good_count")
    private Double goodCount;
    @Column(name = "warm_count")
    private Double warmCount;
    @Column(name = "sad_count")
    private Double sadCount;
    @Column(name = "angry_count")
    private Double angryCount;
    @Column(name = "want_count")
    private Double wantCount;
    @Column(name = "comment_count")
    private Double commentCount;

}
