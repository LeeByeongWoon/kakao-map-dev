package com.keti.launcher.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.core.type.TypeReference;

import com.keti.launcher.entity.NewsEntity;
import com.keti.launcher.repository.NewsRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    NewsRepository newsRepository;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String dataValue = data.value();
        
        try {
            Map<String, JSONObject> newsMessage = 
                    objectMapper.readValue(dataValue, new TypeReference<Map<String, JSONObject>>(){});
            JSONObject news = newsMessage.get("messages");

            NewsEntity newsEntity = convertNewsEntity(news);
            newsRepository.save(newsEntity);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public NewsEntity convertNewsEntity(JSONObject news) {
        NewsEntity newsEntity = new NewsEntity();
        
        Set<String> keys = news.keySet();
        for (String key : keys) {
            Object value = news.get(key);;

            switch (key) {
                case "article_date":
                    String sdt = String.valueOf(news.get(key));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+09:00'");
                    LocalDateTime ldt = LocalDateTime.parse(sdt, formatter).minusHours(9);
                    ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));

                    String date = zdt.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    String time = zdt.format(DateTimeFormatter.ISO_LOCAL_TIME);

                    Instant articleDate = Instant.parse(date + "T" + time + ".000000Z");
                    
                    newsEntity.setArticleDate(articleDate);
                    
                    break;

                case "crawled_date":
                    String crawledDate = String.valueOf(news.get(key));
                    newsEntity.setCrawledDate(crawledDate);
                    break;
                
                case "url":
                    String url = value.toString();
                    newsEntity.setUrl(url);
                    break;

                case "news_id":
                    String newsId = value.toString();
                    newsEntity.setNewsId(newsId);
                    break;

                case "press_name":
                    String pressName = value.toString();
                    newsEntity.setPressName(pressName);
                    break;

                case "title":
                    String title = value.toString();
                    newsEntity.setTitle(title);
                    break;

                case "update_date":
                    String updateDate = value.toString();
                    newsEntity.setUpdateDate(updateDate);
                    break;

                // case "original_url":
                //     String originalUrl = value.toString();
                //     logger.info("originalUrl: " + originalUrl);
                //     newsEntity.setOriginalUrl(originalUrl);
                //     break;

                case "good_count":
                    Double goodCount = Double.parseDouble(value.toString());
                    newsEntity.setGoodCount(goodCount);
                    break;

                case "warm_count":
                    Double warmCount = Double.parseDouble(value.toString());
                    newsEntity.setWarmCount(warmCount);
                    break;

                case "sad_count":
                    Double sadCount = Double.parseDouble(value.toString());
                    newsEntity.setSadCount(sadCount);
                    break;

                case "angry_count":
                    Double angryCount = Double.parseDouble(value.toString());
                    newsEntity.setAngryCount(angryCount);
                    break;

                case "want_count":
                    Double wantCount = Double.parseDouble(value.toString());
                    newsEntity.setWantCount(wantCount);
                    break;

                case "comment_count":
                    Double commentCount = Double.parseDouble(value.toString());
                    newsEntity.setCommentCount(commentCount);
                    break;
            
                default:
                    break;
            }
        }

        return newsEntity;
    }

}